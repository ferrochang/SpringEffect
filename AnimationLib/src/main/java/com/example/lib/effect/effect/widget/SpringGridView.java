package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridView;

public class SpringGridView extends GridView implements ISprintView {

    private EffectHelper mEffectHelper;


    public SpringGridView(Context context) {
        super(context);
        init();
    }

    public SpringGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpringGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mEffectHelper = new EffectHelper(SpringGridView.this);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        boolean ovs = super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0,
                0, isTouchEvent);
        return ovs;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int r = mEffectHelper.onInterceptTouchEvent(ev);
        if (r == -1) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int r = mEffectHelper.onTouchEvent(ev);
        if (r == -1) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    public void setEdgeEffectFactory(SEdgeEffectFactory edgeEffectFactory) {
        mEffectHelper.setEdgeEffectFactory(edgeEffectFactory);
    }

    /**
     * control pullGlow & ScrollBy in scroll nested
     * @param b:true work with pullToRefresh;
     *         b:false default mode
     */
    public void setOverScrollNested(boolean b) {
        //mOverScrollNested = b;
        mEffectHelper.setOverScrollNested(b);
    }

    @Override
    public void SVScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l,t,oldl,oldt);
    }

    @Override
    public boolean SVawakenScrollBars() {
        return super.awakenScrollBars();
    }

    @Override
    public int SVgetHeaderViewsCount() {
        return 0;
    }

    @Override
    public int SVgetFooterViewsCount() {
        return 0;
    }

    @Override
    public void SVsetOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    public void setOnScrollListener(OnScrollListener l) {
        mEffectHelper.setOnScrollListener(l);
    }

}
