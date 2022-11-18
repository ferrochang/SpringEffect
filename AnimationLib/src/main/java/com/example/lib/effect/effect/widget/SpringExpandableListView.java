package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

public class SpringExpandableListView extends ExpandableListView implements ISprintView {
    private EffectHelper mEffectHelper;

    public SpringExpandableListView(Context context) {
        super(context);
        init();
    }

    public SpringExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mEffectHelper = new EffectHelper(SpringExpandableListView.this);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        boolean ovs = super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0,
                0, isTouchEvent);
        //Log.d("SpringListView", "ovs " + ovs);
        return ovs;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int r = mEffectHelper.onInterceptTouchEvent(ev);
        if (r == -1)
            return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int r = mEffectHelper.onTouchEvent(ev);
        if (r == -1)
            return false;
        return super.onTouchEvent(ev);
    }

    public void setEdgeEffectFactory(@NonNull SpringRelativeLayout.SEdgeEffectFactory edgeEffectFactory) {
        mEffectHelper.setEdgeEffectFactory(edgeEffectFactory);
    }

    @Override
    public void SVScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean SVawakenScrollBars() {
        return super.awakenScrollBars();
    }

    @Override
    public int SVgetHeaderViewsCount() {
        return SpringExpandableListView.this.getHeaderViewsCount();
    }

    @Override
    public int SVgetFooterViewsCount() {
        return SpringExpandableListView.this.getFooterViewsCount();
    }

    @Override
    public void SVsetOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    public void setOverScrollNested(boolean b) {
        mEffectHelper.setOverScrollNested(b);
    }

    public void setOnScrollListener(OnScrollListener l) {
        mEffectHelper.setOnScrollListener(l);
    }
}
