package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * Created by song on 2017/7/12.
 * 通用动画刷新view
 */

public class AnimeRefreshView extends ImageView implements IRefreshView {

    private int[] animes;
    private int status;
    private int tempIndex;
    private AnimationDrawable animationDrawable;

    public AnimeRefreshView(Context context) {
        super(context);
    }

    public void setDrawAnimes(int[] animes) {
        this.animes = animes;
    }

    public void setAnimationDrawable(AnimationDrawable animationDrawable) {
        this.animationDrawable = animationDrawable;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void updateStatus(int status) {
        this.status = status;

        switch (status) {
            case QSRefreshLayout.STATUS_DRAGGING:
            case QSRefreshLayout.STATUS_DRAGGING_REACH:
                break;
            case QSRefreshLayout.STATUS_REFRESHING:
                setImageDrawable(animationDrawable);
                animationDrawable.start();
                break;
            case QSRefreshLayout.STATUS_REFRESHED:
                animationDrawable.stop();
                setImageDrawable(null);
                break;
            case QSRefreshLayout.STATUS_NORMAL:
                break;
        }

    }


    @Override
    public void updateProgress(float progress) {
        if (animes == null)
            return;
        if (status == QSRefreshLayout.STATUS_DRAGGING | status == QSRefreshLayout.STATUS_DRAGGING_REACH) {
            int index = (int) (progress * animes.length);
            if (index >= animes.length)
                index = animes.length - 1;
            if (index != tempIndex)
                setImageResource(animes[index]);
            tempIndex = index;
        }
    }

    @Override
    public boolean isBringToFront() {
        return false;
    }

    @Override
    public float dragRate() {
        return 0.7F;
    }

    @Override
    public int triggerDistance() {
        return getMeasuredHeight();
    }

    @Override
    public int maxDistance() {
        return getMeasuredHeight() * 2;
    }

    @Override
    public int getThisViewOffset(int offset) {
        if (offset == 0)
            return 0;
        boolean b = offset > 0;
        offset = Math.abs(offset);
        int t = triggerDistance();
        int i;
        if (offset > t)
            i = offset;
        else
            i = t / 2 + offset / 2;
        return b ? i : -i;
    }

    @Override
    public int getTargetOffset(int offset) {
        return offset;
    }

    @Override
    public int completeAnimaDuration() {
        return 0;
    }

    @Override
    public void isHeadView(boolean isHead) {

    }
}
