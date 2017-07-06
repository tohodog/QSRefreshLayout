package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import org.song.refreshlayout.IRefreshView;

/**
 * Created by song on 2017/7/3.
 */

public class TestBottomView extends FrameLayout implements IRefreshView {

    public TestBottomView(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(100, 100));
        setBackgroundColor(Color.YELLOW);
    }

    @Override
    public View getView() {
        return this;
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
    public boolean isBringToFront() {
        return false;
    }

    @Override
    public float dragRate() {
        return 0.7f;
    }

    @Override
    public int triggerDistance() {
        return getLayoutParams().height;
    }

    @Override
    public int maxDistance() {
        return 300;
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
}
