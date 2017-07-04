package org.song.refreshlayout;

import android.view.View;

/**
 * Created by song on 2017/6/30.
 * 自定义刷新view样式模块 实现接口
 */

public interface IRefreshView {

    View getView();

    boolean isMoveTarget();//是否listview要移动 不需要移动会把view放在顶层 需要则放在底层

    void updateStatus(int status);//更新刷新状态

    void updateProgress(float progress);//刷新进度0~1

    int triggerDistance();//触发刷新的距离

    int maxDistance();//最大滑动距离

    int getOffsetFormat(int offset);//根据进度获取位移,可以拿来实现视差移动

}
