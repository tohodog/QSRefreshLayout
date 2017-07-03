package org.song.refreshlayout;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by song on 2017/7/3.
 */

public class RView extends FrameLayout implements IRefreshView {

    public RView(Context context) {
        super(context);
        setLayoutParams(new FrameLayout.LayoutParams(-1, 100));
        setBackgroundColor(Color.BLUE);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean isMoveTarget() {
        return false;
    }

    @Override
    public void updateStatus(int Status) {

    }

    @Override
    public void updateProgress(float progress) {

    }

    @Override
    public int triggerDistance() {
        return getLayoutParams().height;
    }

    @Override
    public int getOffsetFormat(int offset) {
        return offset;
    }
}
