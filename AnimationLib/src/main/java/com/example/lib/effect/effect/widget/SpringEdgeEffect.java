package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewGroup;
import android.widget.EdgeEffect;

import androidx.dynamicanimation.animation.SpringAnimation;

public class SpringEdgeEffect extends EdgeEffect {
    private final float mVelocityMultiplier;
    private boolean mReleased = true;
    private ISpringPro springPro;
    private SpringAnimation mSpring;
    private int height;

    public SpringEdgeEffect(Context context, float velocityMultiplier, SpringAnimation spring, int h, ISpringPro iss) {
        super(context);
        mVelocityMultiplier = velocityMultiplier;
        springPro = iss;
        mSpring = spring;
        height = h;
    }

    @Override
    public boolean draw(Canvas canvas) {
        return false;
    }

    @Override
    public void onAbsorb(int velocity) {
        //android.util.Log.d("SpringRelativeLayout", "onAbsorb " + velocity);
        //finishScrollWithVelocity(velocity * mVelocityMultiplier);
        springPro.finishScrollWithVelocity(velocity * mVelocityMultiplier);
        //mDistance = 0;
        springPro.setDistance(0);
    }

    public void setSpringAnimation(SpringAnimation animation) {
        mSpring = animation;
    }

    @Override
    public void onPull(float deltaDistance, float displacement) {
        if (mSpring.isRunning()) {
            mSpring.cancel();
        }
        int pullCount = springPro.getPullCount();
        springPro.setPullCount(pullCount++);
        //mPullCount++;
        //setActiveEdge(this);
        //android.util.Log.d("SpringRelativeLayout", " displacement " + displacement);
        //if (displacement < .4f) {
        //mDistance += deltaDistance * (mVelocityMultiplier / 3f);
        float distance = springPro.getDistance();
        distance += deltaDistance * (mVelocityMultiplier / 3f);
        springPro.setDistance(distance);
        //Log.d("SpringRelativeLayout", "onPull " + mDistance + " displacement " + displacement);
        //springPro.setDampedScrollShift(mDistance * getHeight());
        springPro.setDampedScrollShift(distance * height);
        //}
        mReleased = false;
    }

    @Override
    public void onRelease() {
        //Log.d("SpringRelativeLayout", "onRelease mDampedScrollShift " + mDampedScrollShift);
        if (mReleased) {
            return;
        }
        //mDistance = 0;
        springPro.setDistance(0);
        //mPullCount = 0;
        springPro.setPullCount(0);
        //if (mDampedScrollShift != .0)
        //    mReadyToGo = false;
        springPro.finishScrollWithVelocity(0);
        mReleased = true;
    }

    public interface ISpringPro {
        void finishScrollWithVelocity(float velocity);
        float getDistance();
        void setDistance(float f);
        void setDampedScrollShift(float shift);
        int getPullCount();
        void setPullCount(int x);

    }
}
