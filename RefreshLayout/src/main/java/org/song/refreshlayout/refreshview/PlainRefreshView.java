package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
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
        super(context);
        float density = context.getResources().getDisplayMetrics().density;
        mTotalDragDistance = Math.round((float) 120 * density);

        setLayoutParams(new ViewGroup.LayoutParams(-1, mTotalDragDistance));


        plainRefreshDraw = new PlainRefreshDraw(context, mTotalDragDistance);
        setImageDrawable(plainRefreshDraw);
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
        plainRefreshDraw.setPercent(progress);

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
        invalidate();
        return mTotalDragDistance;
    }

    @Override
    public int getTargetOffset(int offset) {
        if (offset >= triggerDistance())
            offset = triggerDistance() + (offset - triggerDistance()) / 10;
        if (offset > mTotalDragDistance)
            offset = mTotalDragDistance;
        return offset;
    }
}