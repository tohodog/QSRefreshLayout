package org.song.refreshlayout;

/**
 * Created by song on 2017/6/30.
 * 自定义刷新view样式模块 实现接口
 */

public interface IRefreshView {

    boolean isMoveTarget();//是否listview要移动

    void updateStatus(int Status);//更新刷新状态

    void updateProgress(float progress);//刷新进度0~1

    int triggerDistance();//触发刷新的距离


}
