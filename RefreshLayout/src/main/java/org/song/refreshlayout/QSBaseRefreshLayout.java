package org.song.refreshlayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by song on 2017/6/30.
 */

public abstract class QSBaseRefreshLayout extends ViewGroup {

    public static final int STATUS_NORMAL=0;

    public static final int STATUS_PULLING=1;
    public static final int STATUS_PULLING_REACH=2;
    public static final int STATUS_REFRESHING=3;
    public static final int STATUS_REFRESHED=4;


    private View mTarget;//滑动的view
    private View mRefreshView;//刷新的view
    private int mCurrentOffsetTop;//刷新时拖动的间隔

    private boolean isHeadRefresh;
    private boolean isFootRefresh;

    public QSBaseRefreshLayout(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if ((mTarget = ensureTarget()) == null)
            return;
        final int width = r - l;
        final int height = b - t;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);

    }

    protected abstract View ensureTarget();

}
