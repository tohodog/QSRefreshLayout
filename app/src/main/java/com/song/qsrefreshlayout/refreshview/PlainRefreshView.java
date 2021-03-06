package com.song.qsrefreshlayout.refreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * Created by song on 2017/7/6.
 */

public class PlainRefreshView extends ImageView implements IRefreshView {

    int mTotalDragDistance;
    PlainRefreshDraw plainRefreshDraw;

    public PlainRefreshView(Context context) {
        this(context, null);
    }

    public PlainRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = context.getResources().getDisplayMetrics().density;
        setLayoutParams(new ViewGroup.LayoutParams(-1, mTotalDragDistance = Math.round((float) 120 * density)));

        plainRefreshDraw = new PlainRefreshDraw(getContext(), mTotalDragDistance);
        setImageDrawable(plainRefreshDraw);
    }

    @Override//确定view大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        if (specMode != MeasureSpec.EXACTLY)//高度不确定时(xml设置为wrap_content时) 自己设置默认值
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mTotalDragDistance, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        plainRefreshDraw.setGetTotalDragDistance(mTotalDragDistance = getMeasuredHeight());
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void updateStatus(int status) {
        if (status == QSRefreshLayout.STATUS_REFRESHING)
            plainRefreshDraw.start();
        else if (status == QSRefreshLayout.STATUS_REFRESHED)
            plainRefreshDraw.setEndOfRefreshing(true);
        else if (status == QSRefreshLayout.STATUS_NORMAL) {
            plainRefreshDraw.setEndOfRefreshing(false);
            plainRefreshDraw.stop();
        }

    }

    @Override
    public void updateProgress(float progress) {
        //if (progress > 1)
        //    progress = 1;
        plainRefreshDraw.setPercent(progress);
        invalidate();
    }

    @Override
    public boolean isBringToFront() {
        return false;
    }

    @Override
    public float dragRate() {
        return 0.7f;
    }

    @Override
    public int triggerDistance() {
        return (int) (mTotalDragDistance * 0.9);
    }

    @Override
    public int maxDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getThisViewOffset(int offset) {
        if (offset > 0)
            offset = mTotalDragDistance;
        else
            offset = getTargetOffset(offset);
        //plainRefreshDraw.offsetTopAndBottom(offset);
        return offset;
    }

    @Override
    public int getTargetOffset(int offset) {
        int abs = Math.abs(offset);
        if (abs >= triggerDistance())
            abs = triggerDistance() + (abs - triggerDistance()) / 10;
        if (abs > mTotalDragDistance)
            abs = mTotalDragDistance;
        return offset > 0 ? abs : -abs;
    }

    @Override
    public int completeAnimaDuration() {
        return 500;
    }

    @Override
    public void isHeadView(boolean isHead) {

    }
}
