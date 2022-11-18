package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SpringRefreshLayout extends SwipeRefreshLayout {

    private View mChildwithOverScrolling = null;
    private boolean mOverScrollTargetReady = false;

    public SpringRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public SpringRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (mChildwithOverScrolling != null)
            mChildwithOverScrolling.onTouchEvent(ev);

        return super.onTouchEvent(ev);
    }

    public void setOverScrollChild(View v) {
        mChildwithOverScrolling = v;
    }
}
