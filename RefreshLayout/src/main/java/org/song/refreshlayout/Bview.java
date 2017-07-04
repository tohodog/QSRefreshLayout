package org.song.refreshlayout;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by song on 2017/7/3.
 */

public class Bview  extends FrameLayout implements IRefreshView {

    public Bview(Context context) {
        super(context);
        setLayoutParams(new FrameLayout.LayoutParams(100, 100));
        setBackgroundColor(Color.YELLOW);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean isMoveTarget() {
        return true;
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
