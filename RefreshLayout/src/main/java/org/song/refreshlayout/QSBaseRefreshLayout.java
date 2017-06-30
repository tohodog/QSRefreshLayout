package org.song.refreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

/**
 * Created by song on 2017/6/30.
 */

public abstract class QSBaseRefreshLayout extends ViewGroup {

    public static final int STATUS_NORMAL = 0;

    public static final int STATUS_PULLING = 1;
    public static final int STATUS_PULLING_REACH = 2;
    public static final int STATUS_REFRESHING = 3;
    public static final int STATUS_REFRESHED = 4;

    private boolean isReady;

    private View mTarget;//滑动的view
    private IRefreshView mHeadRefreshView, mFootRefreshView;//刷新的view
    private int mCurrentOffsetTop;//刷新时拖动的间隔

    private boolean isToEdgeImmediatelyRefresh;//是否滚动到边缘可以立即触发刷新 不需要停一下


    private boolean isOpenHeadRefresh = true;
    private boolean isOpenFootRefresh = true;

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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return interceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return touchEvent(ev);
    }
    private boolean isReady(){
        return
    }

    protected abstract boolean interceptTouchEvent(MotionEvent ev);

    protected abstract boolean touchEvent(MotionEvent ev);

    protected abstract View ensureTarget();

}
