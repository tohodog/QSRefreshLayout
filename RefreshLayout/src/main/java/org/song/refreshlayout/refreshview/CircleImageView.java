package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * MD
 */
public class CircleImageView extends ImageView implements IRefreshView {

    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;

    private int mShadowRadius;
    private MaterialProgressDrawable materialProgressDrawable;

    private int radius = 20;
    private int diameter;
    private int color = 0xFFFAFAFA;

    @SuppressWarnings("deprecation")
    public CircleImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        final float density = getContext().getResources().getDisplayMetrics().density;
        diameter = (int) (radius * density * 2);
        mShadowRadius = (int) (density * SHADOW_RADIUS);

        //设置背景和悬浮
        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
        } else {
            int shadowYOffset = (int) (density * Y_OFFSET);
            int shadowXOffset = (int) (density * X_OFFSET);
            OvalShape oval = new OvalShadow(mShadowRadius, diameter);
            circle = new ShapeDrawable(oval);
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        setBackgroundDrawable(circle);

        //设置具体的动画draw
        materialProgressDrawable = new MaterialProgressDrawable(getContext(), this);
        setImageDrawable(materialProgressDrawable);

        setBackgroundColor(color);
        //view大小
        diameter = diameter + (elevationSupported() ? 0 : mShadowRadius * 2);
        setLayoutParams(new ViewGroup.LayoutParams(diameter, diameter));
    }

    public void setRadius(int radius) {
        this.radius = radius;
        init();
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    public void setColorScheme(int... color1) {
        materialProgressDrawable.setColorSchemeColors(color1);
    }

    @Override
    public View getView() {
        return this;
    }

    boolean flag = true;
    int minAlpha = 255 * 3 / 10;
    int status;

    @Override
    public void updateStatus(int status) {
        this.status = status;
        switch (status) {
            case QSRefreshLayout.STATUS_DRAGGING:
            case QSRefreshLayout.STATUS_DRAGGING_REACH:
                if (flag) {
                    flag = false;
                    materialProgressDrawable.setAlpha(minAlpha);
                }
                break;
            case QSRefreshLayout.STATUS_REFRESHING:
                materialProgressDrawable.start();
                break;

            case QSRefreshLayout.STATUS_REFRESHED:
                break;
            case QSRefreshLayout.STATUS_NORMAL:
                materialProgressDrawable.stop();

                flag = true;
                setScaleX(1);
                setScaleY(1);
                break;
        }

    }

    @Override
    public void updateProgress(float progress) {
        if (status == QSRefreshLayout.STATUS_DRAGGING | status == QSRefreshLayout.STATUS_DRAGGING_REACH) {
            if (progress <= 1) {
                materialProgressDrawable.showArrow(true);
                materialProgressDrawable.setArrowScale(progress);
                materialProgressDrawable.setAlpha((int) (minAlpha + (255 - minAlpha) * progress));
                float max = .8f;
                materialProgressDrawable.setStartEndTrim(0, progress * max);
            }
            materialProgressDrawable.setProgressRotation(-0.25f + progress * 0.4f);
        }
        if (status == QSRefreshLayout.STATUS_REFRESHED) {
            setScaleX(progress);
            setScaleY(progress);
        }
    }

    private void scale(View v) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation.setDuration(500);
        v.startAnimation(scaleAnimation);
    }

    @Override
    public boolean isBringToFront() {
        return true;
    }

    @Override
    public float dragRate() {
        return 0.5f;
    }

    @Override
    public int triggerDistance() {
        return diameter * 3 / 2;
    }

    @Override
    public int maxDistance() {
        return diameter * 3;
    }

    @Override
    public int getThisViewOffset(int offset) {
        if (status == QSRefreshLayout.STATUS_REFRESHED)
            return triggerDistance();
        return offset;
    }

    @Override
    public int getTargetOffset(int offset) {
        return 0;
    }

    @Override
    public int completeAnimaDuration() {
        return 500;
    }

    @Override
    public void isHeadView(boolean isHead) {

    }


    private class OvalShadow extends OvalShape {
        private final RadialGradient mRadialGradient;
        private final Paint mShadowPaint;
        private final int mCircleDiameter;

        OvalShadow(int shadowRadius, int circleDiameter) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            mCircleDiameter = circleDiameter;
            mRadialGradient = new RadialGradient(mCircleDiameter / 2, mCircleDiameter / 2,
                    mShadowRadius, new int[]{
                    FILL_SHADOW_COLOR, Color.TRANSPARENT
            }, null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = CircleImageView.this.getWidth();
            final int viewHeight = CircleImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius),
                    mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable)
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        if (materialProgressDrawable != null)
            materialProgressDrawable.setBackgroundColor(color);
        this.color = color;
    }
}
