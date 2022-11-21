package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

public class SpringScrollView extends ScrollView implements ISprintView {
    private scrollingChangeListener mCanScrollingChangeListener;
    private boolean mScrollingAllowed = false;
    private EffectHelper mEffectHelper;

    public SpringScrollView(Context context) {
        super(context);
        init();
    }

    public SpringScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpringScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mEffectHelper = new EffectHelper(SpringScrollView.this);
    }


    public void setEdgeEffectFactory(@NonNull SEdgeEffectFactory edgeEffectFactory) {
        mEffectHelper.setEdgeEffectFactory(edgeEffectFactory);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int r = mEffectHelper.onInterceptTouchEvent2(ev);
        if (r == -1)
            return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int r = mEffectHelper.onTouchEvent2(ev);
        if (r == -1)
            return false;
        return super.onTouchEvent(ev);
    }

    /**
     * control pullGlow & ScrollBy in scroll nested
     * @param b
     * true: work with pullToRefresh;
     * false: default mode
     */
    public void setOverScrollNested(boolean b) {
         mEffectHelper.setOverScrollNested(b);
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Log.d("SpringScrollView", "onScrollChanged " + t + " " + mLastYVel);
        if (mCanScrollingChangeListener != null) {
            boolean canScroll = canScrollVertically(mCanScrollingChangeListener.getDir());
            if (mScrollingAllowed != canScroll) {
                mScrollingAllowed = canScroll;
                if (mScrollingAllowed) {
                    mCanScrollingChangeListener.onAllowed(mCanScrollingChangeListener.getDir());
                } else {
                    mCanScrollingChangeListener.onStop(mCanScrollingChangeListener.getDir());
                }
            }
        }

        mEffectHelper.scrollViewGlowing(l, t, oldl, oldt);
        /*
        //Log.d("SpringNestScrollView", "onScrollChanged " + t + " mGlowingTop " + mGlowingTop + " " + canScrollVertically(-1));
        if (mGlowingTop) {
            if (canScrollVertically(-1) && t > oldt) {
                onRecyclerViewScrolled();
                //mRecycleScrolled = true;
                //Log.d("SpringNestScrollView", "mRecycleScrolled");
            }
        }

        if (mGlowingBottom) {
            if (canScrollVertically(1) && t < oldt) {
                onRecyclerViewScrolled();
                //mRecycleScrolled = true;
            }
        }

        if (!mGlowingTop) {
            if (!canScrollVertically(-1) && t < oldt) {
                float yvel = mLastYVel;
                if (yvel >= 0) {

                    // overscroll-by-fling happened before MotionEvent.ACTION_UP

                    yvel = computeVelocity();
                }
                Log.d("SpringScrollView", "ready go " + yvel + " " + mLastY + " " + mLastYVel);

                pullGlows(mLastX, (float) 0, mLastY, yvel / 20);
                //ensureTopGlow();
                if (mTopGlow != null) {
                    mTopGlow.onAbsorb((int) (yvel / 20));
                }
            }
        }

        if (!mGlowingBottom) {
            if (!canScrollVertically(1) && t > oldt) {
                float yvel = mLastYVel;
                if (yvel <= 0) {

                    // overscroll-by-fling happened before MotionEvent.ACTION_UP

                    yvel = computeVelocity();
                }
                Log.d("SpringScrollView", "ready go bottom " + yvel + " " + mLastY + " " + mLastYVel);

                pullGlows(mLastX, (float) 0, mLastY, yvel / 20);
                //ensureBottomGlow();
                if (mBottomGlow != null) {
                    mBottomGlow.onAbsorb((int) (yvel / 20));
                }
            }
        }

         */


        //super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * To removeEdgeEffect;
     * if setEdgeEffectColor did not work in your case. you can use it to remove EdgeEffect;
     * Limitation: reflection;
     */
    public void removeEdgeEffect() {
        Class<?> superClass = getClass().getSuperclass();

        while (!superClass.getName().contains("android.widget.ScrollView")) {
            superClass = superClass.getSuperclass();
        }

        if (!superClass.getName().contains("android.widget.ScrollView")) {
            return;
        }

        try {
            Field f1 = superClass.getDeclaredField("mEdgeGlowTop");
            f1.setAccessible(true);
            f1.set(SpringScrollView.this, new ClearEdgeEffect(getContext()));

            Field f2 = superClass.getDeclaredField("mEdgeGlowBottom");
            f2.setAccessible(true);
            f2.set(SpringScrollView.this, new ClearEdgeEffect(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SVScrollChanged(int l, int t, int oldl, int oldt) {
        onScrollChanged(l, t, oldl, oldt);
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
    public void SVsetOnScrollListener(AbsListView.OnScrollListener l) {
        return;
    }

    class ClearEdgeEffect extends EdgeEffect {
        public ClearEdgeEffect(Context context) {
            super(context);
        }

        public boolean draw(Canvas canvas) {
            return false;
        }
    }

    public interface scrollingChangeListener {
        int getDir();
        void onAllowed(int i);
        void onStop(int i);
    }

    public void setScrollingChangeListener(scrollingChangeListener l) {
        mCanScrollingChangeListener = l;
    }

}
