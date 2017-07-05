package org.song.refreshlayout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

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
        initData();
    }

    private void initData() {
        setHeadRefreshView(new Floatview(getContext()));
        setFootRefreshView(new Bview(getContext()));

        requestLayout();
    }

    public void setHeadRefreshView(IRefreshView headRefreshView) {
        if (headRefreshView == null)
            return;
        this.headRefreshView = headRefreshView;
        headRefreshView.getView().setVisibility(INVISIBLE);
        addView(headRefreshView.getView());
        setOpenHeadRefresh(true);
    }

    public void setOpenHeadRefresh(boolean openHeadRefresh) {
        isOpenHeadRefresh = openHeadRefresh;
    }

    public void setFootRefreshView(IRefreshView footRefreshView) {
        if (footRefreshView == null)
            return;
        this.footRefreshView = footRefreshView;
        footRefreshView.getView().setVisibility(INVISIBLE);
        addView(footRefreshView.getView());
        setOpenFootRefresh(true);
    }

    public void setOpenFootRefresh(boolean openFootRefresh) {
        isOpenFootRefresh = openFootRefresh;
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
                        absListView.getChildAt(absListView.getChildCount() - 1).getBottom() <
                                absListView.getHeight() - absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mTarget, 1);
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }

    public void refreshComplete() {
        scrollAnimation(currentOffset, 0, true);
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
}
