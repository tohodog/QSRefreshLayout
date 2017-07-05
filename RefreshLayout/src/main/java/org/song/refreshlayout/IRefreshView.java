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

    int triggerDistance();//触发刷新的距离

    int maxDistance();//最大滑动距离

    int getThisViewOffset(int offset);//确定该view位移可以拿来实现视差移动

    int getTargetOffset(int offset);//确定滚动view的位移,可以拿来实现视差移动

}
