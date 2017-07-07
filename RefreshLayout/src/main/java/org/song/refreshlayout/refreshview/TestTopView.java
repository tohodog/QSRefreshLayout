package org.song.refreshlayout.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import org.song.refreshlayout.IRefreshView;

/**
 * Created by song on 2017/7/3.
 */

public class TestTopView extends FrameLayout implements IRefreshView {

    public TestTopView(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(100, 100));
        setBackgroundColor(Color.CYAN);
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
        return true;
    }

    @Override
    public float dragRate() {
        return 1;
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
    public int getThisViewOffset(int offset) {
        return offset;
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
