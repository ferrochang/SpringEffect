/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static androidx.dynamicanimation.animation.SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_MEDIUM;

public class SpringRelativeLayout extends RelativeLayout {

    private static final float STIFFNESS = (0.3f * STIFFNESS_MEDIUM + 0.7f * STIFFNESS_LOW);
    private static final float DAMPING_RATIO = DAMPING_RATIO_MEDIUM_BOUNCY;
    private static final float VELOCITY_MULTIPLIER = 0.3f;
    private boolean mReadyToGo = true;
    private float mVelocity = VELOCITY_MULTIPLIER;
    private float mStiff = STIFFNESS;

    private static final FloatPropertyCompat<SpringRelativeLayout> DAMPED_SCROLL =
            new FloatPropertyCompat<SpringRelativeLayout>("value") {

                @Override
                public float getValue(SpringRelativeLayout object) {
                    return object.mDampedScrollShift;
                }

                @Override
                public void setValue(SpringRelativeLayout object, float value) {
                    object.setDampedScrollShift(value);
                }
            };

    protected final SparseBooleanArray mSpringViews = new SparseBooleanArray();
    private final SpringAnimation mSpring;

    private float mDampedScrollShift = 0;
    private SpringEdgeEffect mActiveEdge;
    private boolean mHorizontal = false;

    private float mDistance = 0;
    private int mPullCount = 0;

    private DynamicAnimation.OnAnimationEndListener mAnimationEndListener;

    public SpringRelativeLayout(Context context) {
        this(context, null);
    }

    public SpringRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSpring = new SpringAnimation(this, DAMPED_SCROLL, 0);
        mSpring.setSpring(new SpringForce(0)
                .setStiffness(mStiff)
                .setDampingRatio(DAMPING_RATIO));
    }

    public void addSpringView(int id) {
        mSpringViews.put(id, true);
    }

    public void removeSpringView(int id) {
        mSpringViews.delete(id);
        invalidate();
    }

    /**
     * Used to clip the canvas when drawing child views during overscroll.
     */
    public int getCanvasClipTopForOverscroll() {
        return 0;
    }

    public int getCanvasClipLeftForOverscroll() {
        return 0;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (mDampedScrollShift != 0 && mSpringViews.get(child.getId())) {
            int saveCount = canvas.save();

            if (mHorizontal) {
                canvas.clipRect(getCanvasClipLeftForOverscroll(), 0, getWidth(), getHeight());
                canvas.translate(mDampedScrollShift, 0);
            } else {
                canvas.clipRect(0, getCanvasClipTopForOverscroll(), getWidth(), getHeight());
                canvas.translate(0, mDampedScrollShift);
            }
            boolean result = super.drawChild(canvas, child, drawingTime);

            canvas.restoreToCount(saveCount);

            return result;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    private void setActiveEdge(SpringEdgeEffect edge) {
        if (mActiveEdge != edge && mActiveEdge != null) {
            //mActiveEdge.mDistance = 0; //FIXME
        }
        mActiveEdge = edge;
    }

    protected void setDampedScrollShift(float shift) {
        if (shift != mDampedScrollShift) {
            mDampedScrollShift = shift;
            invalidate();
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

    private void finishScrollWithVelocity(float velocity) {
        if (mAnimationEndListener != null)
            mSpring.addEndListener(mAnimationEndListener);
        mSpring.setStartVelocity(velocity);
        mSpring.setStartValue(mDampedScrollShift);
        mSpring.start();
    }

    protected void finishWithShiftAndVelocity(float shift, float velocity,
            DynamicAnimation.OnAnimationEndListener listener) {
        setDampedScrollShift(shift);
        mSpring.addEndListener(listener);
        finishScrollWithVelocity(velocity);
    }

    public EdgeEffectFactory createEdgeEffectFactory() {
        return createEdgeEffectFactory(false);
    }

    private class SpringEdgeEffectFactory extends EdgeEffectFactory {

        @NonNull @Override
        protected EdgeEffect createEdgeEffect(RecyclerView view, int direction) {
            switch (direction) {
                case EdgeEffectFactory.DIRECTION_TOP:
                case EdgeEffectFactory.DIRECTION_LEFT:
                    return new SpringEdgeEffect(getContext(), +mVelocity);
                case EdgeEffectFactory.DIRECTION_BOTTOM:
                case EdgeEffectFactory.DIRECTION_RIGHT:
                    return new SpringEdgeEffect(getContext(), -mVelocity);
            }
            return super.createEdgeEffect(view, direction);
        }
    }

    public EdgeEffectFactory createEdgeEffectFactory(boolean horizontal) {
        mHorizontal = horizontal;
        return new SpringEdgeEffectFactory();
    }

    private class SpringEdgeEffect extends EdgeEffect {

        private final float mVelocityMultiplier;

        private boolean mReleased = true;

        public SpringEdgeEffect(Context context, float velocityMultiplier) {
            super(context);
            mVelocityMultiplier = velocityMultiplier;
        }

        @Override
        public boolean draw(Canvas canvas) {
            return false;
        }

        @Override
        public void onAbsorb(int velocity) {
            finishScrollWithVelocity(velocity * mVelocityMultiplier);
            mDistance = 0;
        }

        @Override
        public void onPull(float deltaDistance, float displacement) {
            if (mSpring.isRunning()) {
                mSpring.cancel();
            }
            mPullCount++;
            setActiveEdge(this);
            mDistance += deltaDistance * (mVelocityMultiplier / 3f);
            if (mHorizontal)
                setDampedScrollShift(mDistance * getWidth());
            else
                setDampedScrollShift(mDistance * getHeight());
            mReleased = false;
        }

        @Override
        public void onRelease() {
            if (mReleased) {
                return;
            }
            mDistance = 0;
            mPullCount = 0;
            if (mDampedScrollShift != .0)
                mReadyToGo = false;
            finishScrollWithVelocity(0);
            mReleased = true;
        }
    }

    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //android.util.Log.d("SpringRelativeLayout", "ev y " + ev.getY() + " height " + getHeight() + " action " + ev.getAction() + " mReadyToGo " + mReadyToGo + " Spring running " + mSpring.isRunning());
        if (ev.getY() > getHeight() || ev.getY() < 0  || !mReadyToGo) {
            //long downTime = ev.getDownTime();
            //long eventTime = ev.getEventTime();
            //action = MotionEvent.ACTION_CANCEL;
            //float x = ev.getX();
            //float y = ev.getY();
            //int metaState = ev.getMetaState();
            //MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            //setDampedScrollShift(0);
            return super.dispatchTouchEvent(ev);

        } else
            return super.dispatchTouchEvent(ev);
    }
     */


    private class ViewEdgeEffectFactory extends SEdgeEffectFactory {
        @NonNull @Override
        protected EdgeEffect createEdgeEffect(View view, int direction) {
            switch (direction) {
                case DIRECTION_TOP:
                case DIRECTION_LEFT:
                    return new SpringEdgeEffect(getContext(), +mVelocity);
                case DIRECTION_BOTTOM:
                case DIRECTION_RIGHT:
                    return new SpringEdgeEffect(getContext(), -mVelocity);
            }
            return super.createEdgeEffect(view, direction);
        }
    }

    public ViewEdgeEffectFactory createViewEdgeEffectFactory() {
        return createViewEdgeEffectFactory(false);
    }

    public ViewEdgeEffectFactory createViewEdgeEffectFactory(boolean horizontal) {
        mHorizontal = horizontal;
        return new ViewEdgeEffectFactory();
    }

    public static class SEdgeEffectFactory {
        public static final int DIRECTION_LEFT = 0;
        public static final int DIRECTION_TOP = 1;
        public static final int DIRECTION_RIGHT = 2;
        public static final int DIRECTION_BOTTOM = 3;

        public SEdgeEffectFactory() {
        }

        @NonNull
        protected EdgeEffect createEdgeEffect(@NonNull View view, int direction) {
            return new EdgeEffect(view.getContext());
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface EdgeDirection {
        }
    }

    /**
     * Listener to know the EdgeEffect Animation is finished.
     * @param l the given Listener
     */
    public void setAnimationEndListener(DynamicAnimation.OnAnimationEndListener l) {
        mAnimationEndListener = l;
    }

    /**
     * To check the running status of EdgeEffect Animation
     * @return true or false
     */
    public boolean isSpringAnimation() {
        if (mSpring != null) {
            return mSpring.isRunning();
        }
        return false;
    }

    public void setmVelocity(float v) {
        mVelocity = v;
    }

    public void setStiff(float p, float r) {
        mStiff = (p * STIFFNESS_MEDIUM + (1.0f - p) * STIFFNESS_LOW);
        if (mSpring != null) {
            mSpring.setSpring(new SpringForce(0).setStiffness(mStiff).setDampingRatio(r));
        }
    }
}
