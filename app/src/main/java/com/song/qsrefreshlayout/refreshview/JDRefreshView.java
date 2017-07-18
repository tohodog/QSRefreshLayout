package com.song.qsrefreshlayout.refreshview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.song.qsrefreshlayout.R;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;

/**
 * Created by song on 2017/7/18.
 * 京东刷新控件
 */

public class JDRefreshView extends FrameLayout implements IRefreshView {

    ImageView jd_people, jd_pack;
    TextView jd_text;
    int status;

    public JDRefreshView(@NonNull Context context) {
        super(context);
        View refreshView = LayoutInflater.from(context).inflate(R.layout.refresh_jd, this, false);
        jd_text = (TextView) refreshView.findViewById(R.id.jd_text);
        jd_people = (ImageView) refreshView.findViewById(R.id.jd_people);
        jd_pack = (ImageView) refreshView.findViewById(R.id.jd_pack);
        addView(refreshView);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void updateStatus(int status) {
        this.status = status;
        jd_pack.setVisibility(VISIBLE);
        if (status == QSRefreshLayout.STATUS_DRAGGING) {
            jd_text.setText("下拉刷新...");
        } else if (status == QSRefreshLayout.STATUS_DRAGGING_REACH) {
            jd_text.setText("松开刷新...");
        } else if (status == QSRefreshLayout.STATUS_REFRESHING) {
            jd_text.setText("刷新中...");
            jd_people.setImageResource(R.drawable.jd_loading);
            ((AnimationDrawable) jd_people.getDrawable()).start();
            jd_pack.setVisibility(GONE);
        } else if (status == QSRefreshLayout.STATUS_REFRESHED) {
            jd_text.setText("刷新完成");
            ((AnimationDrawable) jd_people.getDrawable()).stop();
            jd_people.setImageResource(R.drawable.jd_refresh_people_0);
        }
    }

    @Override
    public void updateProgress(float progress) {
        if (status == QSRefreshLayout.STATUS_DRAGGING | status == QSRefreshLayout.STATUS_DRAGGING_REACH) {
            if (progress > 1)
                progress = 1;
            jd_pack.setScaleX(progress);
            jd_pack.setScaleY(progress);
            jd_pack.setAlpha(progress);
            jd_people.setAlpha(progress);
            jd_people.setScaleX(progress);
            jd_people.setScaleY(progress);
            jd_people.setTranslationX(-jd_people.getWidth() / 2 * (1 - progress));
        }
    }

    @Override
    public boolean isBringToFront() {
        return false;
    }

    @Override
    public float dragRate() {
        return 0.7f;
    }

    @Override
    public int triggerDistance() {
        return getMeasuredHeight();
    }

    @Override
    public int maxDistance() {
        return 0;
    }

    @Override
    public int getThisViewOffset(int offset) {
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
