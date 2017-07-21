package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSBaseRefreshLayout;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * Created by song on 2017/7/21.
 */

public class IOSRefreshView extends View implements IRefreshView {


    private Paint mPaint;
    private float density;
    private int circleRadius;
    private int offset;
    private int height;

    public IOSRefreshView(Context context) {
        this(context, null);
    }

    public IOSRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffcccccc);

        density = context.getResources().getDisplayMetrics().density;
        circleRadius = (int) (density * 15);

        setLayoutParams(new ViewGroup.LayoutParams(-1, height = circleRadius * 10));
    }

    public void setColor(int c) {
        mPaint.setColor(c);

    }

    @Override//确定view大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        if (specMode != MeasureSpec.EXACTLY)//高度不确定时(xml设置为wrap_content时) 自己设置默认值
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        float y = circleRadius * 1.5f;
        canvas.drawCircle(w / 2, y, circleRadius, mPaint);
        canvas.drawCircle(w / 2, y + offset, circleRadius / 2, mPaint);
        drawBezierCurve(canvas, y);
    }

    private Path mPath = new Path();

    /**
     * 绘制贝塞尔曲线
     * 计算两圆切点...
     */
    private void drawBezierCurve(Canvas canvas, float y) {
        int w = getWidth();
        mPath.reset();
        mPath.moveTo(w / 2 - circleRadius, y);
        mPath.lineTo(w / 2 + circleRadius, y);
        mPath.quadTo(w / 2 + circleRadius / 2, y + offset / 2, w / 2 + circleRadius / 2, y + offset);
        mPath.lineTo(w / 2 - circleRadius / 2, y + offset);
        mPath.quadTo(w / 2 - circleRadius / 2, y + offset / 2, w / 2 - circleRadius, y);
        mPath.close();  // 闭合
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void updateStatus(int status) {
        if (status == QSRefreshLayout.STATUS_DRAGGING | status == QSRefreshLayout.STATUS_DRAGGING_REACH)
            if (status > 1)
                ((QSBaseRefreshLayout) getParent()).forcedIntoRefresh((int) (circleRadius * 3f));
    }

    @Override
    public void updateProgress(float progress) {
        invalidate();
    }

    @Override
    public boolean isBringToFront() {
        return false;
    }

    @Override
    public float dragRate() {
        return .7f;
    }

    @Override
    public int triggerDistance() {
        return getMeasuredHeight();
    }

    @Override
    public int maxDistance() {
        return 0;
    }

    @Override
    public int getThisViewOffset(int offset) {
        if (Math.abs(offset) > circleRadius * 3f) {
            this.offset = (int) (offset - circleRadius * 3f);
        }
        return isHead ? getMeasuredHeight() : -getMeasuredHeight();
    }

    @Override
    public int getTargetOffset(int offset) {
        return offset;
    }

    @Override
    public int completeAnimaDuration() {
        return 0;
    }

    private boolean isHead;

    @Override
    public void isHeadView(boolean isHead) {
        this.isHead = isHead;
    }
}
