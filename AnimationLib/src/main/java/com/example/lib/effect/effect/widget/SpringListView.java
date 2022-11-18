package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import androidx.annotation.NonNull;

public class SpringListView extends ListView implements ISprintView {

    private EffectHelper mEffectHelper;

    public SpringListView(Context context) {
        super(context);
        init();
    }

    public SpringListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpringListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mEffectHelper = new EffectHelper(SpringListView.this);
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
        //mEdgeEffectFactory = edgeEffectFactory;
        //invalidateGlows();
        mEffectHelper.setEdgeEffectFactory(edgeEffectFactory);
    }

    /**
     * control pullGlow & ScrollBy in scroll nested
     * @param b:true work with pullToRefresh;
     *         b:false default mode
     */
    public void setOverScrollNested(boolean b) {
        mEffectHelper.setOverScrollNested(b);
    }

    @Override
    public void SVScrollChanged(int l, int t, int oldl, int oldt) {
        SpringListView.this.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean SVawakenScrollBars() {
        return SpringListView.this.awakenScrollBars();
    }

    @Override
    public int SVgetHeaderViewsCount() {
        return SpringListView.this.getHeaderViewsCount();
    }

    @Override
    public int SVgetFooterViewsCount() {
        return SpringListView.this.getFooterViewsCount();
    }

    @Override
    public void SVsetOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    public void setOnScrollListener(OnScrollListener l) {
        //if (mEffectHelper == null) {
        //    init();
        //}
        mEffectHelper.setOnScrollListener(l);

    }
}
