package org.song.refreshlayout;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by song on 2017/7/3.
 */

public class Floatview extends FrameLayout implements IRefreshView {

    public Floatview(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(100, 100));
        setBackgroundColor(Color.CYAN);
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
    public void updateStatus(int status) {
        if (status == 2)
            setBackgroundColor(Color.YELLOW);
        else if (status == 3)
            setBackgroundColor(Color.BLUE);
        else setBackgroundColor(Color.CYAN);


    }

    @Override
    public void updateProgress(float progress) {

    }

    @Override
    public int maxDistance() {
        return 300;
    }

    @Override
    public int triggerDistance() {
        return getLayoutParams().height;
    }

    @Override
    public int getOffsetFormat(int offset) {
        return (int) (offset / 0.7f);
    }
}
