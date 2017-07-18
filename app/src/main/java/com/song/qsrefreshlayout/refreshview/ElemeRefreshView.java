package com.song.qsrefreshlayout.refreshview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import org.song.refreshlayout.IRefreshView;

/**
 * Created by song on 2017/7/18.
 * 饿了么刷新控件
 */

public class ElemeRefreshView extends FrameLayout implements IRefreshView {

    public ElemeRefreshView(@NonNull Context context) {
        super(context);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void updateStatus(int status) {

    }

    @Override
    public void updateProgress(float progress) {

    }

    @Override
    public boolean isBringToFront() {
        return false;
    }

    @Override
    public float dragRate() {
        return 0;
    }

    @Override
    public int triggerDistance() {
        return 0;
    }

    @Override
    public int maxDistance() {
        return 0;
    }

    @Override
    public int getThisViewOffset(int offset) {
        return 0;
    }

    @Override
    public int getTargetOffset(int offset) {
        return 0;
    }

    @Override
    public int completeAnimaDuration() {
        return 0;
    }

    @Override
    public void isHeadView(boolean isHead) {

    }
}
