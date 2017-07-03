package org.song.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by song on 2017/7/3.
 */

public class QSRefreshLayout extends QSBaseRefreshLayout {

    public QSRefreshLayout(Context context) {
        this(context, null);
    }


    public QSRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected View ensureTarget() {
        if (mTarget != null)
            return mTarget;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof IRefreshView))
                    mTarget = child;
            }
        }
        return mTarget;
    }

    @Override
    protected boolean canChildScrollUp() {
        return false;
    }

    @Override
    protected boolean canChildScrollDown() {
        return false;
    }
}
