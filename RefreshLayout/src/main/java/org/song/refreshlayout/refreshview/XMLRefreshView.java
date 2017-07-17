package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.song.refreshlayout.IRefreshView;
import org.song.refreshlayout.QSRefreshLayout;
import org.song.refreshlayout.R;

/**
 * Created by song on 2017/7/12.
 */

public class XMLRefreshView extends FrameLayout implements IRefreshView {

    private ViewGroup refreshView;
    private TextView refreshTV;
    private ImageView refreshIV;
    private ProgressBar refreshPB;


    private String dragging = "下拉刷新",
            dragging_reach = "松开刷新",
            refreshing = "加载中...",
            refreshed = "刷新完成";

    public XMLRefreshView(Context context) {
        super(context);
        refreshView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.refresh, this, false);
        refreshTV = (TextView) refreshView.findViewById(R.id.refresh_tv);
        refreshIV = (ImageView) refreshView.findViewById(R.id.refresh_iv);
        refreshPB = (ProgressBar) refreshView.findViewById(R.id.refresh_pb);
        addView(refreshView);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void updateStatus(int status) {
        refreshIV.setVisibility(GONE);
        refreshPB.setVisibility(GONE);

        if (status == QSRefreshLayout.STATUS_DRAGGING) {
            refreshTV.setText(dragging);
            refreshIV.setVisibility(VISIBLE);
            rotate(refreshIV, 0, 180);
        } else if (status == QSRefreshLayout.STATUS_DRAGGING_REACH) {
            refreshTV.setText(dragging_reach);
            refreshIV.setVisibility(VISIBLE);
            rotate(refreshIV, 180, 360);


        } else if (status == QSRefreshLayout.STATUS_REFRESHING) {
            refreshTV.setText(refreshing);
            refreshPB.setVisibility(VISIBLE);


        } else if (status == QSRefreshLayout.STATUS_REFRESHED) {
            refreshTV.setText(refreshed);
            //refreshIV.setVisibility(VISIBLE);

        }
    }


    public void rotate(View v, int start, int end) {
        RotateAnimation rotateAnimation = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(200);
        v.startAnimation(rotateAnimation);
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
        return 0.5f;
    }

    @Override
    public int triggerDistance() {
        return getMeasuredHeight();
    }

    @Override
    public int maxDistance() {
        return getMeasuredHeight() * 3;
    }

    @Override
    public int getThisViewOffset(int offset) {
        return offset;
    }

    @Override
    public int getTargetOffset(int offset) {
        return offset;
    }

    @Override
    public int completeAnimaDuration() {
        return 0;
    }


    public void setDragging(String dragging) {
        this.dragging = dragging;
    }

    public void setDragging_reach(String dragging_reach) {
        this.dragging_reach = dragging_reach;
    }

    public void setRefreshing(String refreshing) {
        this.refreshing = refreshing;
    }

    public void setRefreshed(String refreshed) {
        this.refreshed = refreshed;
    }
}
