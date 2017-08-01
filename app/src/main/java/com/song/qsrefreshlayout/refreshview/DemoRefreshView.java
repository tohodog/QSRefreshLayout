package com.song.qsrefreshlayout.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * Created by song on 2017/8/1.
 * 一个参考demo
 */

public class DemoRefreshView extends FrameLayout implements IRefreshView {


    private int h;

    public DemoRefreshView(@NonNull Context context) {
        this(context, null);
    }

    public DemoRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        float density = context.getResources().getDisplayMetrics().density;
        h = (int) (density * 50);

        //todo 如果在xml配置view 则此参数无效 会被xml的参数覆盖
        //设置这个空间的大小 目前不支持margin gravity等参数,默认居中
        setLayoutParams(new ViewGroup.LayoutParams(h, h));
        setBackgroundColor(Color.BLUE);

    }

    private void init() {

    }

    public void setH(int h) {
        this.h = h;
        requestLayout();
    }

    @Override//确定view大小,你可以在这里强制配置自己的view大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如我的view需要长宽都锁死,不被setLayoutParams所修改
        //则可以这样,然后再提供setH(int h)给外部
        setMeasuredDimension(h, h);
    }

    //返回当前view的实例即可
    @Override
    public View getView() {
        return this;
    }

    /**
     * 刷新的状态回调
     * 刷新控件共有5种状态
     * STATUS_NORMAL = 0;//普通状态
     * STATUS_DRAGGING = 1;//手指拖曳时(未到触发距离)
     * STATUS_DRAGGING_REACH = 2;//手指拖曳(可以触发刷新的距离)
     * STATUS_REFRESHING = 3;//刷新中
     * STATUS_REFRESHED = 4;//刷新完成后到view隐藏的这一段状态
     * 开发者可以根据状态来设置view
     */
    private int status;

    @Override
    public void updateStatus(int status) {
        this.status = status;
        switch (status) {
            //就只是变下颜色
            case QSRefreshLayout.STATUS_DRAGGING_REACH:
                setBackgroundColor(Color.RED);
                break;
            case QSRefreshLayout.STATUS_REFRESHING:
                setBackgroundColor(Color.GREEN);
                break;
            case QSRefreshLayout.STATUS_DRAGGING:
            case QSRefreshLayout.STATUS_REFRESHED:
            case QSRefreshLayout.STATUS_NORMAL:
                setBackgroundColor(Color.BLUE);
                break;
        }
    }

    /**
     * 刷新的进度
     * 刷新进度 0 ~ 1(触发刷新)~ 更大
     * 一些特效动画就可以根据这个值来更新状态
     */
    @Override
    public void updateProgress(float progress) {
        //不是拖曳状态忽略
        if (status != QSRefreshLayout.STATUS_DRAGGING && status != QSRefreshLayout.STATUS_DRAGGING_REACH)
            return;
        if (progress > 1)
            progress = 1;
        //这里就实现一个颜色渐变吧

        int startColor = Color.BLUE;
        int a1 = (startColor >> 24) & 0x000000FF;
        int r1 = (startColor >> 16) & 0x000000FF;
        int g1 = (startColor >> 8) & 0x000000FF;
        int b1 = startColor & 0x000000FF;

        Log.e("b1", "" + b1);
        int endColor = Color.RED;
        int a2 = (endColor >> 24) & 0x000000FF;
        int r2 = (endColor >> 16) & 0x000000FF;
        int g2 = (endColor >> 8) & 0x000000FF;
        int b2 = endColor & 0x000000FF;
        Log.e("b2", "" + b2);

        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);
        int a = (int) (a1 + (a2 - a1) * progress);
        Log.e("b", "" + b);

        int newColor = Color.argb(a, r, g, b);
        Log.e("newColor", "" + newColor);

        setBackgroundColor(newColor);

    }

    /**
     * 是否view在顶层
     * 比如饿了么刷新就需要在底层 返回false
     * 谷歌的小圆球刷新就需要在顶层 返回true
     */
    @Override
    public boolean isBringToFront() {
        return false;//显示在目标的下方
    }

    /**
     * 控制拖曳的速率
     * 比如返回.5f,手指拖动100像素,本框架会认为是50像素
     */
    @Override
    public float dragRate() {
        return .5f;
    }

    /**
     * 触发刷新的距离
     */
    @Override
    public int triggerDistance() {
        return getMeasuredHeight();//触发距离为本view的高度
    }

    /**
     * 最大拖动的距离
     * <=0不限制
     */
    @Override
    public int maxDistance() {
        return 0;//不限制
    }

    /**
     * 根据触摸位移 确定该view的位移
     *
     * @param offset 当前的拖动距离(实际触摸距离*dragRate()), headview大于0, footview小于0
     * @return 返回这个view的移动值 这个值将会确定view的位置
     */
    @Override
    public int getThisViewOffset(int offset) {
        //这里我们实现一个视差移动
        offset = Math.abs(offset);
        int t = triggerDistance();
        int i;
        if (offset > t)
            i = offset;
        else
            i = t / 2 + offset / 2;
        return isHead ? i : -i;//顶部和底部view 移动值是相反的
    }

    /**
     * 根据触摸位移 确定目标view的位移
     *
     * @param offset 当前的拖动距离(实际触摸距离*dragRate()), headview大于0, footview小于0
     * @return 返回目标view(就是listview, 等)的移动值 这个值将会确定view的位置
     * 这个返回值一般要么是
     * 0不会动
     * offset 原值返回
     */
    @Override
    public int getTargetOffset(int offset) {
        return offset;//返回原值,会跟随手指而移动
    }

    /**
     * 完成刷新后到消失的动画时间, <=0使用默认时间300ms
     * 如果有一些动画刷新完成后执行时间较长或需要调慢 可以这里返回时间ms
     * (BarRefreshView使用了)
     */
    @Override
    public int completeAnimaDuration() {
        return 0;
    }

    private boolean isHead;

    //标记这个view是拿去做head还是foot,用变量记录下来,也可以在这里实现一些初始化
    @Override
    public void isHeadView(boolean isHead) {
        this.isHead = isHead;
    }
}
