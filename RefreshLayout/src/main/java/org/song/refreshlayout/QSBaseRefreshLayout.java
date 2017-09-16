package org.song.refreshlayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 轻松刷新
 * Created by song on 2017/6/30.
 */

public abstract class QSBaseRefreshLayout extends ViewGroup {

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_DRAGGING = 1;
    public static final int STATUS_DRAGGING_REACH = 2;
    public static final int STATUS_REFRESHING = 3;
    public static final int STATUS_REFRESHED = 4;

    protected int animaDuration = 365;

    protected int refreshStatus;

    protected View mTarget;//滑动的view

    protected IRefreshView footRefreshView, headRefreshView;//刷新的view
    protected IRefreshView draggedRefreshView;//s

    protected Interpolator animeInterpolator = new DecelerateInterpolator(2F);//自动回弹动画插值器
    protected int currentOffset;//刷新时拖动的间隔

    protected boolean isToEdgeImmediatelyRefresh = true;//是否滚动到边缘可以立即触发刷新 不需要停一下

    protected int touchSlop;//触发刷新的最小滑动距离
    protected boolean isOpenHeadRefresh;
    protected boolean isOpenFootRefresh;

    public QSBaseRefreshLayout(Context context) {
        this(context, null);
    }


    public QSBaseRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTarget = ensureTarget();
    }

    @Override//确定子view大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom(), lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override//确定子view位置
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if ((mTarget = ensureTarget()) == null)
            return;
        final int width = r - l;
        final int height = b - t;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        //滑动控件强制match 无视它的属性
        int offset = 0;
        if (draggedRefreshView != null)
            offset = draggedRefreshView.getTargetOffset(currentOffset);
        mTarget.layout(left, top + offset, left + width - right, top + height - bottom + offset);

        if (headRefreshView != null) {
            View child = headRefreshView.getView();
            final int w = child.getMeasuredWidth();
            final int h = child.getMeasuredHeight();
            offset = headRefreshView.getThisViewOffset(draggedRefreshView == headRefreshView ? currentOffset : 0);
            child.layout((width - w) / 2, -h + offset + top, (width - w) / 2 + w, offset + top);
        }

        if (footRefreshView != null) {
            View child = footRefreshView.getView();
            final int w = child.getMeasuredWidth();
            final int h = child.getMeasuredHeight();
            offset = footRefreshView.getThisViewOffset(draggedRefreshView == footRefreshView ? currentOffset : 0);
            child.layout((width - w) / 2, top + height + offset, (width - w) / 2 + w, top + height + h + offset);
        }
        //Log.e("currentOffset=", "" + currentOffset);
    }


    @Override//判断是否截取事件进行刷新
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isNormal() || mTarget == null)
            return false;
        boolean isHeadBeingDragged = false, isFootBeingDragged = false;
        if (isOpenHeadRefresh && !canChildScrollUp())
            isHeadBeingDragged = handlerInterceptTouchEvent(ev, true);
        if (!isHeadBeingDragged) {
            if (isOpenFootRefresh && !canChildScrollDown())
                isFootBeingDragged = handlerInterceptTouchEvent(ev, false);
        }
        boolean b = isHeadBeingDragged || isFootBeingDragged;
        if (b) {
            draggedRefreshView = isHeadBeingDragged ? headRefreshView : footRefreshView;
            setRefreshStatus(STATUS_DRAGGING);
        }
        return b;
    }

    private int mActivePointerId;
    private float mInitialMotionY, mInitialMotionX;

    //是否截取事件
    private boolean handlerInterceptTouchEvent(MotionEvent ev, boolean isHead) {
        final int action = ev.getActionMasked();
        boolean mIsBeingDragged = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                final float initialMotionY = ev.getY(mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionX = ev.getX(mActivePointerId);
                mInitialMotionY = initialMotionY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == MotionEvent.INVALID_POINTER_ID) {
                    return false;
                }
                final int index = ev.findPointerIndex(mActivePointerId);
                if (index < 0) {
                    return false;
                }
                final float y = ev.getY(mActivePointerId);
                final float x = ev.getX(mActivePointerId);

                if (y == -1) {
                    return false;
                }
                float yDiff = y - mInitialMotionY;
                float xDiff = x - mInitialMotionX;
                if (!isHead)
                    yDiff = -yDiff;
                if (yDiff > touchSlop && Math.abs(yDiff) > Math.abs(xDiff)) {
                    mInitialMotionY = y;//触发拖曳 刷新下Y值
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP://兼容多个手指
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
        }
        return mIsBeingDragged;
    }

    @Override//拖曳view
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isBeingDragged() || mTarget == null || draggedRefreshView == null) {
            return super.onTouchEvent(ev);
        }

        final int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                final float yDiff = y - mInitialMotionY;
                float scrollTop = yDiff * draggedRefreshView.dragRate();
                //防止拖回去
                if (draggedRefreshView == headRefreshView) {
                    if (scrollTop < 0)
                        scrollTop = 0;
                } else {
                    if (scrollTop > 0)
                        scrollTop = 0;
                }
                //先设置好值才改变状态
                setDragViewOffsetAndPro((int) scrollTop, true);
                if (Math.abs(scrollTop) >= draggedRefreshView.triggerDistance())
                    setRefreshStatus(STATUS_DRAGGING_REACH);
                else
                    setRefreshStatus(STATUS_DRAGGING);
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                final int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == MotionEvent.INVALID_POINTER_ID)
                    return false;
                int triggerDistance = draggedRefreshView.triggerDistance();
                if (draggedRefreshView == footRefreshView)
                    triggerDistance = -triggerDistance;
                if (refreshStatus == STATUS_DRAGGING_REACH) {
                    setRefreshStatus(STATUS_REFRESHING);//刷新
                    scrollAnimation(currentOffset, triggerDistance, animaDuration);
                } else if (refreshStatus == STATUS_DRAGGING) {//距离不够取消刷新
                    scrollAnimation(currentOffset, 0, animaDuration, STATUS_NORMAL);
                }
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                return false;
            }
        }

        return true;
    }

    //实现view移动变化的方法
    private void setDragViewOffsetAndPro(int offset, boolean requiresUpdate) {
        if (offset == currentOffset)
            return;
        int max = draggedRefreshView.maxDistance();
        if (Math.abs(offset) > max && max > 0)
            offset = offset > 0 ? max : -max;
        int temp = currentOffset;
        currentOffset = offset;


        if (draggedRefreshView.isBringToFront())
            draggedRefreshView.getView().bringToFront();
        int temp1, temp2;

        temp1 = draggedRefreshView.getTargetOffset(temp);
        temp2 = draggedRefreshView.getTargetOffset(offset);
        mTarget.offsetTopAndBottom(temp2 - temp1);

        temp1 = draggedRefreshView.getThisViewOffset(temp);
        temp2 = draggedRefreshView.getThisViewOffset(offset);
        draggedRefreshView.getView().offsetTopAndBottom(temp2 - temp1);

        float pro = Math.abs(1f * offset / draggedRefreshView.triggerDistance());
        draggedRefreshView.updateProgress(pro);

        if (requiresUpdate) {
            invalidate();
        }
    }

    //刷新完成
    public void refreshComplete() {
        if (mScrollAnimator != null)
            mScrollAnimator.cancel();
        if (refreshStatus == STATUS_REFRESHING) {
            setRefreshStatus(STATUS_REFRESHED);
            scrollAnimation(currentOffset, 0, draggedRefreshView.completeAnimaDuration(), STATUS_NORMAL);
        }
    }

    //自动进入刷新状态[isAnime是否显示动画过程
    public void enterHeadRefreshing(boolean isAnime) {
        enterRefreshing(isAnime, headRefreshView);
    }

    //自动进入刷新状态[isAnime是否显示动画过程
    public void enterFootRefreshing(boolean isAnime) {
        enterRefreshing(isAnime, headRefreshView);
    }

    private void enterRefreshing(final boolean isAnime, IRefreshView iRefreshView) {
        if (refreshStatus != STATUS_NORMAL)
            return;
        draggedRefreshView = iRefreshView;
        if (draggedRefreshView == null)
            return;
        post(new Runnable() {
            @Override
            public void run() {
                if (isAnime) {
                    setRefreshStatus(STATUS_DRAGGING);
                    scrollAnimation(0, draggedRefreshView.triggerDistance(), animaDuration, STATUS_DRAGGING_REACH, STATUS_REFRESHING);
                } else {
                    setRefreshStatus(STATUS_DRAGGING);
                    setDragViewOffsetAndPro(draggedRefreshView.triggerDistance(), true);
                    setRefreshStatus(STATUS_DRAGGING_REACH);
                    setRefreshStatus(STATUS_REFRESHING);
                }
            }
        });
    }

    private ValueAnimator mScrollAnimator;

    //插值动画 模拟拖曳
    protected void scrollAnimation(final int start, final int end, final int time, final int... status) {
        if (mScrollAnimator != null)
            mScrollAnimator.cancel();
        mScrollAnimator = new ValueAnimator();
        mScrollAnimator.setIntValues(start, end);
        mScrollAnimator.setInterpolator(animeInterpolator);
        mScrollAnimator.setDuration(time > 0 ? time : animaDuration);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setDragViewOffsetAndPro((int) animation.getAnimatedValue(), true);
            }
        });
        mScrollAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (status != null)
                    for (int s : status)
                        setRefreshStatus(s);
                mScrollAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setDragViewOffsetAndPro(end, true);
                if (status != null)
                    for (int s : status)
                        setRefreshStatus(s);
                mScrollAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mScrollAnimator.start();
    }

    //给IOSRefreshView用的 直接进入刷新
    public void forcedIntoRefresh(int triggerDistance) {
        if (isBeingDragged()) {
            setRefreshStatus(STATUS_REFRESHING);//刷新
            scrollAnimation(currentOffset, triggerDistance, animaDuration);
        }
    }

    public boolean isBeingDragged() {
        return refreshStatus == STATUS_DRAGGING | refreshStatus == STATUS_DRAGGING_REACH;
    }

    public boolean isNormal() {
        return refreshStatus == STATUS_NORMAL;
    }


    private void setRefreshStatus(int status) {
        if (refreshStatus == status)
            return;
        refreshStatus = status;
        if (draggedRefreshView != null) {
            draggedRefreshView.updateStatus(status);
            draggedRefreshView.getView().setVisibility(status == STATUS_NORMAL ? INVISIBLE : VISIBLE);
        }
        //必须放最后
        changeStatus(refreshStatus);
        if (status == STATUS_NORMAL)
            draggedRefreshView = null;
    }

    //扩展用 目前集成在一个类也可以
    //滑动view
    protected abstract View ensureTarget();

    //protected abstract boolean ensureRefreshView();

    //判断是否滑动到顶部
    protected abstract boolean canChildScrollUp();

    //判断是否滑动到底部
    protected abstract boolean canChildScrollDown();

    protected abstract void changeStatus(int status);

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // Nope.
        if (!isToEdgeImmediatelyRefresh)
            super.requestDisallowInterceptTouchEvent(b);
    }

}
