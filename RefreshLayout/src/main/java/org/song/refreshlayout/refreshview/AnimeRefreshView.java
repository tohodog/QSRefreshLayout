package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import org.song.refreshlayout.IRefreshView;

/**
 * Created by song on 2017/7/12.
 * 通用动画刷新view
 */

public class AnimeRefreshView extends ImageView implements IRefreshView {

    public AnimeRefreshView(Context context) {
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
}
