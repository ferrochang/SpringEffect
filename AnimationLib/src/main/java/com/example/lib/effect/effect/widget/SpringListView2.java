package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import static androidx.dynamicanimation.animation.SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_MEDIUM;


public class SpringListView2 extends ListView implements ISprintView {
    private static final float STIFFNESS = (0.3f * STIFFNESS_MEDIUM + 0.7f * STIFFNESS_LOW);
    private static final float DAMPING_RATIO = DAMPING_RATIO_MEDIUM_BOUNCY;
    private static final float VELOCITY_MULTIPLIER = 0.3f;

    static final String TAG = SpringListView2.class.getSimpleName();

    private static final FloatPropertyCompat<SpringListView2> DAMPED_SCROLL =
            new FloatPropertyCompat<SpringListView2>("value") {

                @Override
                public float getValue(SpringListView2 object) {
                    return object.mDampedScrollShift;
                }

                @Override
                public void setValue(SpringListView2 object, float value) {
                    object.setDampedScrollShift(value);
                }
            };

    private SEdgeEffectFactory mEdgeEffectFactory;
    private SpringEdgeEffect mActiveEdge;
    private SpringAnimation mSpring;
    private float mDampedScrollShift = 0;
    private float mDistance = 0;
    private int mPullCount = 0;

    private DynamicAnimation.OnAnimationEndListener mAnimationEndListener;

    private EffectHelper mEffectHelper;

    public SpringListView2(Context context) {
        super(context);
        init();
    }

    public SpringListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mEffectHelper = new EffectHelper(SpringListView2.this);
        mEdgeEffectFactory = createViewEdgeEffectFactory();
        setEdgeEffectFactory(mEdgeEffectFactory);


        mSpring = new SpringAnimation(this, DAMPED_SCROLL, 0);
        mSpring.setSpring(new SpringForce(0)
                .setStiffness(STIFFNESS)
                .setDampingRatio(DAMPING_RATIO));

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

    protected void setDampedScrollShift(float shift) {
        if (shift != mDampedScrollShift) {
            mDampedScrollShift = shift;
            //setTranslationY(mDampedScrollShift);
            invalidate();
        }
    }

    public void setEdgeEffectFactory(SEdgeEffectFactory edgeEffectFactory) {
        mEffectHelper.setEdgeEffectFactory(edgeEffectFactory);
    }

    private void finishScrollWithVelocity(float velocity) {
        if (mAnimationEndListener != null)
            mSpring.addEndListener(mAnimationEndListener);
        mSpring.setStartVelocity(velocity);
        mSpring.setStartValue(mDampedScrollShift);
        mSpring.start();
    }

    private void setActiveEdge(SpringEdgeEffect edge) {
        if (mActiveEdge != edge && mActiveEdge != null) {
            //mActiveEdge.mDistance = 0; //FIXME
        }
        mActiveEdge = edge;
    }

    public ViewEdgeEffectFactory createViewEdgeEffectFactory() {
        return new ViewEdgeEffectFactory();
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
        return super.getHeaderViewsCount();
    }

    @Override
    public int SVgetFooterViewsCount() {
        return super.getFooterViewsCount();
    }

    @Override
    public void SVsetOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    private class SpringPro implements SpringEdgeEffect.ISpringPro {

        @Override
        public void finishScrollWithVelocity(float velocity) {
            SpringListView2.this.finishScrollWithVelocity(velocity);
        }

        @Override
        public float getDistance() {
            return mDistance;
        }

        @Override
        public void setDistance(float f) {
            mDistance = f;
        }

        @Override
        public void setDampedScrollShift(float shift) {
            mDampedScrollShift = shift;
        }

        @Override
        public int getPullCount() {
            return mPullCount;
        }

        @Override
        public void setPullCount(int x) {
            mPullCount = x;
        }
    }

    private SpringPro mSpringPro = new SpringPro();
    private class ViewEdgeEffectFactory extends SEdgeEffectFactory {
        @NonNull @Override
        protected EdgeEffect createEdgeEffect(View view, int direction) {
            switch (direction) {
                case DIRECTION_TOP:
                case DIRECTION_LEFT:
                    return new SpringEdgeEffect(getContext(), +VELOCITY_MULTIPLIER, mSpring, view.getHeight(), mSpringPro);
                case DIRECTION_BOTTOM:
                case DIRECTION_RIGHT:
                    return new SpringEdgeEffect(getContext(), -VELOCITY_MULTIPLIER, mSpring, view.getHeight(), mSpringPro);
            }
            return super.createEdgeEffect(view, direction);
        }
    }

    public void onRecyclerViewScrolled() {
        if (mPullCount == 1) {
            // skip it to make animation smoothly when the scroll event is after first onPull occurs.
            return;
        }
        if (mSpring.isRunning()) {
            return;
        }

        mDistance = 0;
        mPullCount = 0;
        finishScrollWithVelocity(0);
    }

    public void setOnScrollListener(OnScrollListener l) {
        mEffectHelper.setOnScrollListener(l);
    }

    public void draw(Canvas canvas) {
        if (mDampedScrollShift != 0) {
            int saveCount = canvas.save();
            canvas.translate(0, mDampedScrollShift);

            super.draw(canvas);
            canvas.restoreToCount(saveCount);

            return;
        }

        super.draw(canvas);
    }

}
