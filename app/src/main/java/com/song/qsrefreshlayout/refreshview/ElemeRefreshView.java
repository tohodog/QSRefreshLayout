package com.song.qsrefreshlayout.refreshview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.song.qsrefreshlayout.R;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * Created by song on 2017/7/18.
 * 饿了么刷新控件
 */

public class ElemeRefreshView extends FrameLayout implements IRefreshView {

    private int triggerDistance;
    ImageView eleme_box_l, eleme_box_r;
    ViewGroup eleme_content, eleme_content_box;
    float density;

    public ElemeRefreshView(Context context) {
        this(context, null);
    }

    public ElemeRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        triggerDistance = (int) (density * 100);

        View refreshView = LayoutInflater.from(context).inflate(R.layout.refresh_eleme, this, false);
        eleme_box_l = (ImageView) refreshView.findViewById(R.id.eleme_box_l);
        eleme_box_r = (ImageView) refreshView.findViewById(R.id.eleme_box_r);
        eleme_content = (ViewGroup) refreshView.findViewById(R.id.eleme_content);
        eleme_content_box = (ViewGroup) refreshView.findViewById(R.id.eleme_content_box);

        addView(refreshView);
    }

    @Override
    public View getView() {
        return this;
    }

    int status;

    @Override
    public void updateStatus(int status) {
        this.status = status;
        if (status == QSRefreshLayout.STATUS_DRAGGING) {
        } else if (status == QSRefreshLayout.STATUS_DRAGGING_REACH) {
        } else if (status == QSRefreshLayout.STATUS_REFRESHING) {
            startRefresh();
        } else if (status == QSRefreshLayout.STATUS_REFRESHED) {
            stopRefresh();
        } else {

        }
    }

    private void startRefresh() {
        rotate(eleme_box_l, -220, 0);
        rotate(eleme_box_r, 220, 1);
        scale(eleme_content_box);
        handler.post(run);
    }

    private void stopRefresh() {
        eleme_content_box.clearAnimation();
        handler.removeCallbacks(run);
        eleme_content.removeAllViews();
    }

    @Override
    public void updateProgress(float progress) {
        if (status != QSRefreshLayout.STATUS_DRAGGING && status != QSRefreshLayout.STATUS_DRAGGING_REACH)
            return;
        if (progress > 1)
            progress = 1;
        int rotate = (int) (progress * 250);
        rotate(eleme_box_l, -rotate, 0);
        rotate(eleme_box_r, rotate, 1);
    }

    //箱门旋转
    private void rotate(View v, int rotate, float x) {
        RotateAnimation rotateAnimation = new RotateAnimation(rotate, rotate,
                Animation.RELATIVE_TO_SELF, x, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(0);
        v.startAnimation(rotateAnimation);
    }

    //箱子弹跳
    private void scale(View v) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1, 1, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                1.2f);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setDuration(150);
        scaleAnimation.setRepeatCount(-1);
        v.startAnimation(scaleAnimation);
    }

    private Handler handler = new Handler();
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            popFood();
            handler.postDelayed(run, 150);
        }
    };

    private int[] foods = new int[]{R.drawable.eleme_c, R.drawable.eleme_d, R.drawable.eleme_f, R.drawable.eleme_h
            , R.drawable.eleme_l, R.drawable.eleme_o};
    private int index;

    //弹出食物
    private void popFood() {

        ImageView iv = new ImageView(getContext());
        iv.setImageResource(foods[index]);
        FrameLayout.LayoutParams l = new LayoutParams((int) (30 * density), (int) (30 * density));
        l.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        iv.setLayoutParams(l);
        popAnime(iv);
        eleme_content.addView(iv, 0);

        index++;
        if (index >= foods.length) index = 0;
    }

    private boolean flag;

    private void popAnime(final View v) {
        flag = !flag;
        int w = (int) (2 * 30 * density);
        int h = (int) (1.5 * 30 * density);
        int h1 = h * 8 / 10;

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(v, "translationX", 0.0f, flag ? -w : w);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v, "translationY", 0, -h1, -h, -h, -h1, -(float) Math.random() * h1 / 2);
        //animator2.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        //set.setInterpolator(new AccelerateInterpolator());
        set.setDuration(1000);
        set.play(animator1);
        set.play(animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean isBringToFront() {
        return false;
    }

    @Override
    public float dragRate() {
        return 1;
    }

    @Override
    public int triggerDistance() {
        return triggerDistance;
    }

    @Override
    public int maxDistance() {
        return 0;
    }

    @Override
    public int getThisViewOffset(int offset) {
        int de = (int) (density * 50);//位移一段距离,视差效果
        if (offset < 0) {
            int t = getTargetOffset(offset);
            float f = 1 + 1.f * offset / triggerDistance();
            if (f < 0)
                f = 0;
            return (int) (t - de * f);
        } else {
            int t = getMeasuredHeight();
            int i = t - de + de * offset / triggerDistance();
            return i > t ? t : i;
        }
    }

    @Override
    public int getTargetOffset(int offset) {
        boolean b = offset > 0;
        offset = Math.abs(offset);
        int h = getMeasuredHeight();
        if (offset > h)
            offset = (int) (h + (offset - h) * 0.2f);
        return b ? offset : -offset;
    }

    @Override
    public int completeAnimaDuration() {
        return 0;
    }

    @Override
    public void isHeadView(boolean isHead) {

    }
}
