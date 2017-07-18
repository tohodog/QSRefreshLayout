package org.song.refreshlayout;

import android.view.View;

/**
 * Created by song on 2017/6/30.
 * 自定义刷新view样式模块 实现接口
 */

public interface IRefreshView {

    View getView();

    void updateStatus(int status);//更新刷新状态

    void updateProgress(float progress);//刷新进度0~1

    boolean isBringToFront();//是否view放在顶层

    float dragRate();//滑动速度控制

    int triggerDistance();//触发刷新的距离 [实际触摸距离*dragRate()>triggerDistance() 触发刷新

    int maxDistance();//最大滑动距离 <=0不限制

    int getThisViewOffset(int offset);//根据触摸位移 确定该view的位移 大于0=headview 小于0=footview

    int getTargetOffset(int offset);//根据触摸位移 确定滚动view的位移 大于0=headview 小于0=footview

    int completeAnimaDuration();//完成刷新后到消失 的动画时间, <=0使用默认时间

    void isHeadView(boolean isHead);//是否顶部刷新view

}
