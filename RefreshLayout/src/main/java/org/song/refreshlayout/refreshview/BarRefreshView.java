package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * Created by song on 2017/7/7.
 */

public class BarRefreshView extends View implements IRefreshView {


    private int height;
    private int triggerDistance;
    private SwipeProgressBar swipeProgressBar;

    public BarRefreshView(Context context) {
        super(context);
        float density = context.getResources().getDisplayMetrics().density;

        swipeProgressBar = new SwipeProgressBar(this);

        height = Math.round(4 * density);
        triggerDistance = Math.round(100 * density);
        setLayoutParams(new ViewGroup.LayoutParams(-1, height));
    }

    public void setColorScheme(int... color1) {
        swipeProgressBar.setColorScheme(color1);
    }

    @Override//确定view大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        swipeProgressBar.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        swipeProgressBar.draw(canvas);
    }

    @Override
    public View getView() {
        return this;
    }

    private int status;

    @Override
    public void updateStatus(int status) {
        this.status = status;
        if (status == QSRefreshLayout.STATUS_REFRESHING)
            swipeProgressBar.start();
        else if (status == QSRefreshLayout.STATUS_REFRESHED)
            swipeProgressBar.stop();

    }

    @Override
    public void updateProgress(float progress) {
        if (status == QSRefreshLayout.STATUS_DRAGGING | status == QSRefreshLayout.STATUS_DRAGGING_REACH)
            swipeProgressBar.setTriggerPercentage(progress);
    }

    @Override
    public boolean isBringToFront() {
        return true;
    }

    @Override
    public float dragRate() {
        return 0.7f;
    }

    @Override
    public int triggerDistance() {
        return triggerDistance;
    }

    @Override
    public int maxDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getThisViewOffset(int offset) {
        return isHead ? height : -height;
    }

    @Override
    public int getTargetOffset(int offset) {
        return 0;
    }

    @Override
    public int completeAnimaDuration() {
        return SwipeProgressBar.FINISH_ANIMATION_DURATION_MS;
    }

    boolean isHead;

    @Override
    public void isHeadView(boolean isHead) {
        this.isHead = isHead;
    }
}
