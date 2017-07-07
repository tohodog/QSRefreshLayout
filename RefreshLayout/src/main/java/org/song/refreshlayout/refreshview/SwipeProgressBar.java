/*
 * Copyright 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.song.refreshlayout.refreshview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;


/**
 * Custom progress bar that shows a cycle of colors as widening circles that
 * overdraw each other. When finished, the bar is cleared from the inside out as
 * the main cycle continues. Before running, this can also indicate how close
 * the user is to triggering something (e.g. how far they need to pull down to
 * trigger a refresh).
 * edit on song by 2017年7月7日
 * 修复颜色显示bug
 * 增加-可以支持多种颜色设置
 */
final class SwipeProgressBar {

    private static final boolean SUPPORT_CLIP_RECT_DIFFERENCE =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;


    // 一种颜色出现到边缘的时间
    public static final int ANIMATION_DURATION_MS = 1000;

    // The duration of the animation to clear the bar.
    public static final int FINISH_ANIMATION_DURATION_MS = 1000;

    // Interpolator for varying the speed of the animation.
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private final Paint mPaint = new Paint();
    private final RectF mClipRect = new RectF();
    private float mTriggerPercentage;
    private long mStartTime;
    private long mFinishTime;
    private boolean mRunning;

    // Colors used when rendering the animation,
    private int[] COLORS = new int[]{
            0xffff4444, 0xffffbb33, 0xff99cc00
    };
    private final View mParent;

    private final Rect mBounds = new Rect();

    public SwipeProgressBar(View parent) {
        mParent = parent;
    }

    /**
     * 颜色设置
     */
    void setColorScheme(int... colors) {
        COLORS = colors;
    }

    /**
     * Update the progress the user has made toward triggering the swipe
     * gesture. and use this value to update the percentage of the trigger that
     * is shown.
     */
    void setTriggerPercentage(float triggerPercentage) {
        mTriggerPercentage = triggerPercentage;
        mStartTime = 0;
        ViewCompat.postInvalidateOnAnimation(
                mParent, mBounds.left, mBounds.top, mBounds.right, mBounds.bottom);
    }

    /**
     * Start showing the progress animation.
     */
    void start() {
        if (!mRunning) {
            mTriggerPercentage = 0;
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mRunning = true;
            mParent.postInvalidate();
        }
    }

    /**
     * Stop showing the progress animation.
     */
    void stop() {
        if (mRunning) {
            mTriggerPercentage = 0;
            mFinishTime = AnimationUtils.currentAnimationTimeMillis();
            mRunning = false;
            mParent.postInvalidate();
        }
    }

    /**
     * @return Return whether the progress animation is currently running.
     */
    boolean isRunning() {
        return mRunning || mFinishTime > 0;
    }

    void draw(Canvas canvas) {
        // API < 18 do not support clipRect(Region.Op.DIFFERENCE).
        // So draw twice for finish animation
        if (draw(canvas, true)) {
            draw(canvas, false);
        }
    }

    private boolean draw(Canvas canvas, boolean first) {
        Rect bounds = mBounds;
        final int width = bounds.width();
        final int cx = bounds.centerX();
        final int cy = bounds.centerY();
        boolean drawTriggerWhileFinishing = false;
        boolean drawAgain = false;
        int restoreCount = canvas.save();
        canvas.clipRect(bounds);

        if (!mRunning && (mFinishTime <= 0)) {
            // 画进度
            drawTrigger(canvas, cx, cy);
        } else {//刷新中 画动画
            long now = AnimationUtils.currentAnimationTimeMillis();
            //当前进度
            final long rawProgress = 100 * (now - mStartTime) / ANIMATION_DURATION_MS % 100;
            //动画时间的一半新出来一种颜色
            final long iterations = (now - mStartTime) / (ANIMATION_DURATION_MS / 2);
            //float rawProgress = (elapsed / (ANIMATION_DURATION_MS / 100f));

            // If we're not running anymore, that means we're running through
            // the finish animation.
            if (!mRunning) {
                // If the finish animation is done, don't draw anything, and
                // don't re-post.
                if ((now - mFinishTime) >= FINISH_ANIMATION_DURATION_MS) {
                    mFinishTime = 0;
                    return false;
                }

                // Otherwise, use a 0 opacity alpha layer to clear the animation
                // from the inside out. This layer will prevent the circles from
                // drawing within its bounds.
                long finishElapsed = (now - mFinishTime) % FINISH_ANIMATION_DURATION_MS;
                float finishProgress = (finishElapsed / (FINISH_ANIMATION_DURATION_MS / 100f));
                float pct = (finishProgress / 100f);
                // Radius of the circle is half of the screen.
                float clearRadius = width / 2 * INTERPOLATOR.getInterpolation(pct);
                if (SUPPORT_CLIP_RECT_DIFFERENCE) {
                    mClipRect.set(cx - clearRadius, bounds.top, cx + clearRadius, bounds.bottom);
                    canvas.clipRect(mClipRect, Region.Op.DIFFERENCE);
                } else {
                    if (first) {
                        // First time left
                        drawAgain = true;
                        mClipRect.set(bounds.left, bounds.top, cx - clearRadius, bounds.bottom);
                    } else {
                        // Second time right
                        mClipRect.set(cx + clearRadius, bounds.top, bounds.right, bounds.bottom);
                    }
                    canvas.clipRect(mClipRect);
                }
                // Only draw the trigger if there is a space in the center of
                // this refreshing view that needs to be filled in by the
                // trigger. If the progress view is just still animating, let it
                // continue animating.
                drawTriggerWhileFinishing = true;
            }

            //画底色
            canvas.drawColor(getColor(iterations - 2));

            //
            if (rawProgress < 50) {
                drawCircle(canvas, cx, cy, getColor(iterations - 1), rawProgress / 100.f + 0.5f);
                //新出来的颜色
                drawCircle(canvas, cx, cy, getColor(iterations), rawProgress / 100.f);
            } else {
                drawCircle(canvas, cx, cy, getColor(iterations - 1), rawProgress / 100.f);
                //新出来的颜色
                drawCircle(canvas, cx, cy, getColor(iterations), rawProgress / 100.f - 0.5f);
            }

            if (mTriggerPercentage > 0 && drawTriggerWhileFinishing) {
                // There is some portion of trigger to draw. Restore the canvas,
                // then draw the trigger. Otherwise, the trigger does not appear
                // until after the bar has finished animating and appears to
                // just jump in at a larger width than expected.
                canvas.restoreToCount(restoreCount);
                restoreCount = canvas.save();
                canvas.clipRect(bounds);
                drawTrigger(canvas, cx, cy);
            }
            // Keep running until we finish out the last cycle.
            ViewCompat.postInvalidateOnAnimation(
                    mParent, bounds.left, bounds.top, bounds.right, bounds.bottom);
        }
        canvas.restoreToCount(restoreCount);
        return drawAgain;
    }

    private int getColor(long index) {
        if (index <= 0)
            index = 0;
        else
            index = index % COLORS.length;
        return COLORS[(int) index];
    }

    private void drawTrigger(Canvas canvas, int cx, int cy) {
        mPaint.setColor(getColor(0));
        canvas.drawCircle(cx, cy, cx * mTriggerPercentage, mPaint);
    }

    /**
     * Draws a circle centered in the view.
     *
     * @param canvas the canvas to draw on
     * @param cx     the center x coordinate
     * @param cy     the center y coordinate
     * @param color  the color to draw
     * @param pct    the percentage of the view that the circle should cover
     */
    private void drawCircle(Canvas canvas, float cx, float cy, int color, float pct) {
        mPaint.setColor(color);
        canvas.save();
        canvas.translate(cx, cy);
        float radiusScale = INTERPOLATOR.getInterpolation(pct);
        canvas.scale(radiusScale, radiusScale);
        canvas.drawCircle(0, 0, cx, mPaint);
        canvas.restore();
    }

    /**
     * Set the drawing bounds of this SwipeProgressBar.
     */
    void setBounds(int left, int top, int right, int bottom) {
        mBounds.left = left;
        mBounds.top = top;
        mBounds.right = right;
        mBounds.bottom = bottom;
    }
}