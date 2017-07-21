package org.song.refreshlayout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;

import org.song.refreshlayout.refreshview.BarRefreshView;
import org.song.refreshlayout.refreshview.CircleImageView;

/**
 * Created by song on 2017/7/3.
 */

public class QSRefreshLayout extends QSBaseRefreshLayout {


    private RefreshListener refreshListener;

    public QSRefreshLayout(Context context) {
        this(context, null);
    }


    public QSRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        post(new Runnable() {
            @Override
            public void run() {
                initData();

            }
        });
    }

    private void initData() {
        if (!ensureRefreshView()) {
            setHeadRefreshView(new CircleImageView(getContext()));
            setFootRefreshView(new BarRefreshView(getContext()));
        }
    }

    public void setHeadRefreshView(IRefreshView headRefreshView) {
        if (headRefreshView == null)
            return;
        if (this.headRefreshView != null)
            removeView(this.headRefreshView.getView());
        this.headRefreshView = headRefreshView;
        headRefreshView.isHeadView(true);
        headRefreshView.getView().setVisibility(INVISIBLE);
        addView(headRefreshView.getView(), 0);
        isOpenHeadRefresh(true);
    }

    public void isOpenHeadRefresh(boolean openHeadRefresh) {
        isOpenHeadRefresh = openHeadRefresh;
    }

    public void setFootRefreshView(IRefreshView footRefreshView) {
        if (footRefreshView == null)
            return;
        if (this.footRefreshView != null)
            removeView(this.footRefreshView.getView());
        this.footRefreshView = footRefreshView;
        footRefreshView.isHeadView(false);
        footRefreshView.getView().setVisibility(INVISIBLE);
        addView(footRefreshView.getView(), 0);
        isOpenFootRefresh(true);
    }

    public void isOpenFootRefresh(boolean openFootRefresh) {
        isOpenFootRefresh = openFootRefresh;
    }

    public View getHeadRefreshView() {
        return headRefreshView.getView();
    }

    public View getFootRefreshView() {
        return footRefreshView.getView();
    }

    @Override
    protected View ensureTarget() {
        if (mTarget != null)
            return mTarget;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof IRefreshView))
                    mTarget = child;
            }
        }
        return mTarget;
    }

    protected boolean ensureRefreshView() {
        boolean b = false;
        if (getChildCount() > 1) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof IRefreshView) {
                    IRefreshView v = (IRefreshView) child;
                    if (getChildCount() == 2)
                        b = i == 1;
                    if (!b) {
                        headRefreshView = v;
                        headRefreshView.isHeadView(true);
                        headRefreshView.getView().setVisibility(INVISIBLE);
                        isOpenHeadRefresh(true);
                    } else {
                        footRefreshView = v;
                        footRefreshView.isHeadView(false);
                        footRefreshView.getView().setVisibility(INVISIBLE);
                        isOpenFootRefresh(true);
                    }
                    b = true;
                }
            }
        }
        return b;
    }

    @Override
    protected boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    @Override
    protected boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getCount() - 1 ||
                        absListView.getChildAt(absListView.getChildCount() - 1).getBottom() >
                                absListView.getHeight() - absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mTarget, 1);
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }


    public boolean isRefreshing() {
        return refreshStatus == STATUS_REFRESHING;
    }

    @Override
    protected void changeStatus(int status) {
        if (refreshListener != null)
            refreshListener.changeStatus(draggedRefreshView == headRefreshView, status);
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshListener {
        void changeStatus(boolean isHeadRefresh, int status);
    }

    //自动回弹动画插值器
    public void setAnimeInterpolator(Interpolator animeInterpolator) {
        this.animeInterpolator = animeInterpolator;
    }

    public int getRefreshStatus() {
        return refreshStatus;
    }
}
