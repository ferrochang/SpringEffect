package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.dynamicanimation.animation.SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_MEDIUM;


public class SpringRecyclerView extends RecyclerView {
    private static final float STIFFNESS = (0.3f * STIFFNESS_MEDIUM + 0.7f * STIFFNESS_LOW);
    private static final float DAMPING_RATIO = DAMPING_RATIO_MEDIUM_BOUNCY;
    private static final float VELOCITY_MULTIPLIER = 0.3f;
    static final String TAG = SpringRecyclerView.class.getSimpleName();

    private static final FloatPropertyCompat<SpringRecyclerView> DAMPED_SCROLL =
            new FloatPropertyCompat<SpringRecyclerView>("value") {

                @Override
                public float getValue(SpringRecyclerView object) {
                    return object.mDampedScrollShift;
                }

                @Override
                public void setValue(SpringRecyclerView object, float value) {
                    object.setDampedScrollShift(value);
                }
            };

    private VelocityTracker mVelocityTracker;

    private int mInitialTouchY;
    private int mLastTouchY;
    private int mLastTouchX;
    private int mScrollState;
    private int mScrollPointerId;
    private int mTouchSlop;
    private int[] mScrollOffset;
    int[] mScrollConsumed;
    private int[] mNestedOffsets;
    int[] mScrollStepConsumed;
    boolean mOverScrollNested = true;
    float mPullGrowTop = 0.1f;
    float mPullGrowBottom = 0.9f;
    private EdgeEffect mTopGlow;
    private EdgeEffect mBottomGlow;
    private boolean mGlowing = false;
    private SpringEdgeEffectFactory mEdgeEffectFactory;
    private SpringEdgeEffect mActiveEdge;
    private SpringAnimation mSpring;
    private float mDampedScrollShift = 0;
    private float mDistance = 0;
    private int mPullCount = 0;
    private int mMaxFlingVelocity;
    private boolean mHorizontal = false;
    private float mLastXVel = 0f;
    private float mLastYVel = 0f;
    private float mLastX, mLastY;
    private boolean mHandleTouch = true;

    public SpringRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public SpringRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ViewConfiguration vc = ViewConfiguration.get(this.getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mScrollStepConsumed = new int[2];
        mScrollOffset = new int[2];
        mNestedOffsets = new int[2];
        mScrollConsumed = new int[2];
        mEdgeEffectFactory = new SpringEdgeEffectFactory();
        setEdgeEffectFactory(mEdgeEffectFactory);
        mSpring = new SpringAnimation(this, DAMPED_SCROLL, 0);
        mSpring.setSpring(new SpringForce(0)
                .setStiffness(STIFFNESS)
                .setDampingRatio(DAMPING_RATIO));
        //addOnScrollListener(mScrollListener);
    }


    /**
     * a workaround for the OverScroll by fling in Horizontal RecyclerView.
     */
    OnScrollListener mScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            //super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            boolean leftScroll = canScrollHorizontally(-1);
            boolean rightScroll = canScrollHorizontally(1);
            if (!leftScroll) {
                if (!mGlowing) {
                    float xvel = mLastXVel;
                    if (xvel >= 0) {
                        xvel = computeVelocity();
                    }
                    pullGlows(mLastX, (float) xvel / 20, mLastY, 0);
                    //ensureTopGlow();
                    if (mTopGlow != null) {
                        mTopGlow.onAbsorb((int) (xvel / 20));
                    }
                }
            }

            if (!rightScroll) {
                if (!mGlowing) {
                    float xvel = mLastXVel;
                    if (xvel <= 0) {
                        xvel = computeVelocity();
                    }
                    pullGlows(mLastX, (float) xvel / 20, mLastY, 0);
                    //ensureTopGlow();
                    if (mBottomGlow != null) {
                        mBottomGlow.onAbsorb((int) (xvel / 20));
                    }
                }
            }
            //super.onScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mHandleTouch) {
            return super.onInterceptTouchEvent(event);
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int action = event.getActionMasked();
        MotionEvent vtev = MotionEvent.obtain(event);

        int index;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = event.getPointerId(0);
                index = event.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    return false;
                }
                mInitialTouchY = mLastTouchY = (int)(event.getY() + 0.5F);
                mLastTouchX = (int)(event.getX() + 0.5F);
                if (mScrollState == 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                }

                mNestedOffsets[0] = mNestedOffsets[1] = 0;
                int nestedScrollAxis = 0;
                //nestedScrollAxis |= 2;
                //startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_MOVE:
                nestedScrollAxis = event.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    vtev.recycle();
                    return false;
                }

                int x = (int)(event.getX(nestedScrollAxis) + 0.5F);
                int y = (int)(event.getY(nestedScrollAxis) + 0.5F);
                int dy = mLastTouchY - y;
                int dx = mLastTouchX - x;

                if (mScrollState != 1) {
                    boolean startScroll = false;

                    if (!mHorizontal) {
                        if (Math.abs(dy) > mTouchSlop) {
                            if (dy > 0) {
                                dy -= mTouchSlop;
                            } else {
                                dy += mTouchSlop;
                            }

                            startScroll = true;
                        }
                    } else {
                        if (Math.abs(dx) > mTouchSlop) {
                            if (dx > 0) {
                                dx -= mTouchSlop;
                            } else {
                                dx += mTouchSlop;
                            }

                            startScroll = true;
                        }
                    }

                    if (startScroll) {
                        setScrollState(1);
                    }
                }


                if (mScrollState == 1) {
                    if (!mHorizontal) {
                        mLastTouchY = y - mScrollOffset[1];
                        if (scrollByInternal(0, dy, vtev)) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        mLastTouchX = x - mScrollOffset[0];
                        if (scrollByInternal(dx, 0, vtev)) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }

                    //if (this.mGapWorker != null && (dx != 0 || dy != 0)) {
                    //    this.mGapWorker.postFromTraversal(this, dx, dy);
                    //}
                }

                break;
        }
        vtev.recycle();
        mLastX = event.getX();
        mLastY = event.getY();

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mHandleTouch) {
            return super.onTouchEvent(event);
        }
        //Log.d(TAG, "onTouchEvent" + event.getAction());
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        boolean eventAddedToVelocityTracker = false;
        MotionEvent vtev = MotionEvent.obtain(event);
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();
        int nestedScrollAxis;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = event.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int)(event.getY() + 0.5F);
                mLastTouchX = (int)(event.getX() + 0.5F);
                nestedScrollAxis = 0;

                //nestedScrollAxis |= 2;
                //startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(vtev);
                eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
                float yvel = -mVelocityTracker.getYVelocity(mScrollPointerId);
                float xvel = -mVelocityTracker.getXVelocity(mScrollPointerId);

                //Log.d(TAG, "onTouchEvent ;  yvel " + yvel);
                if (xvel == 0.0F && mHorizontal) {
                    setScrollState(0);
                } else if (yvel == 0.0F && !mHorizontal) {
                    setScrollState(0);
                } else {
                    //fling((int)yvel);
                    //Log.d(TAG, "mGlowing " + mGlowing);
                    //if (!mGlowing)
                    //    pullGlows(event.getX(), (float) 0, event.getY(), -yvel/6);
                    mLastYVel = yvel;
                    mLastXVel = xvel;

                }

                resetScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG, "onTouchEvent move " + !canScrollVertically(1));
                nestedScrollAxis = event.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    vtev.recycle();
                    return false;
                }

                int x = (int)(event.getX(nestedScrollAxis) + 0.5F);
                int y = (int)(event.getY(nestedScrollAxis) + 0.5F);
                int dy = mLastTouchY - y;
                int dx = mLastTouchX - x;

                //Log.d(TAG, "onTouchEvent ;  dy " + dy + " mScrollState " + mScrollState);

                if (mScrollState != 1) {
                    boolean startScroll = false;
                    if (!mHorizontal) {
                        if (Math.abs(dy) > mTouchSlop) {
                            if (dy > 0) {
                                dy -= mTouchSlop;
                            } else {
                                dy += mTouchSlop;
                            }

                            startScroll = true;
                        }
                    } else {
                        if (Math.abs(dx) > mTouchSlop) {
                            if (dx > 0) {
                                dx -= mTouchSlop;
                            } else {
                                dx += mTouchSlop;
                            }

                            startScroll = true;
                        }
                    }

                    if (startScroll) {
                        setScrollState(1);
                    }
                }

                //mYdiff = dy;
                if (mScrollState == 1) {
                    if (!mHorizontal) {
                        mLastTouchY = y - mScrollOffset[1];
                        if (scrollByInternal(0, dy, vtev)) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        mLastTouchX = x - mScrollOffset[0];
                        if (scrollByInternal(dx, 0, vtev)) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelScroll();
                break;
            case MotionEvent.ACTION_OUTSIDE:
            default:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mScrollPointerId = event.getPointerId(actionIndex);
                mInitialTouchY = mLastTouchY = (int)(event.getY(actionIndex) + 0.5F);
                mLastTouchX = (int)(event.getX(actionIndex) + 0.5F);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(event);
        }
        if (!eventAddedToVelocityTracker) {
            mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        mLastX = event.getX();
        mLastY = event.getY();
        return super.onTouchEvent(event);
    }

    protected void setDampedScrollShift(float shift) {
        if (shift != mDampedScrollShift) {
            mDampedScrollShift = shift;
            //if (mHorizontal) {
            //    setTranslationX(mDampedScrollShift);
            //} else {
            //    setTranslationY(mDampedScrollShift);
            //}
            invalidate();
        }
    }

    boolean scrollByInternal(int x, int y, MotionEvent ev) {
        boolean readyToGo = isReadyToOverScroll(mHorizontal? x < 0: y < 0); //isReadyToOverScroll(y < 0);
        //Log.d(TAG, "scrollByInternal y " + y + " readyToGo " + readyToGo);
        if (!readyToGo) {
            onRecyclerViewScrolled();
            return false;
        }

        int unconsumedX = x;
        int unconsumedY = y;
        /*
        int consumedX = 0;
        int consumedY = 0;
        //consumePendingUpdateOperations();
        if (getChildCount() >= 0) {
            scrollStep(x, y, mScrollStepConsumed);
            consumedX = mScrollStepConsumed[0];
            consumedY = mScrollStepConsumed[1];
            Log.d(TAG, "consumedY " + consumedY + " y " + y + " consumedX " + consumedX + " x " + x);
            unconsumedX = x - consumedX;
            unconsumedY = y - consumedY;
        }

        //if (!this.mItemDecorations.isEmpty()) {
       invalidate();
        //}

        boolean nestedScrollresult = dispatchNestedScroll(consumedX, consumedY, unconsumedX, unconsumedY, mScrollOffset);
        if (nestedScrollresult) {
            mLastTouchY -= mScrollOffset[1];
            if (ev != null) {
                ev.offsetLocation((float)mScrollOffset[0], (float)mScrollOffset[1]);
            }

            int[] var10000 = mNestedOffsets;
            var10000[0] += mScrollOffset[0];
            var10000 = mNestedOffsets;
            var10000[1] += mScrollOffset[1];
        }
         */

        //Log.d(TAG, "nestedScrollresult " + nestedScrollresult + " mScrollOffset " + mScrollOffset[0] + "   " + mScrollOffset[1] + " " + unconsumedY);

        if (/*!nestedScrollresult || */mOverScrollNested) {
            if (getOverScrollMode() != 2) {
                if (ev != null && !ev.isFromSource(8194)) {
                    //Log.d(TAG, "call pullGlows;  y " + y + " unconsumedY " + unconsumedY);
                    pullGlows(ev.getX(), (float) unconsumedX, ev.getY(), (float) unconsumedY);
                }

                considerReleasingGlowsOnScroll(x, y);
            }
        }

        //if (consumedX != 0 || consumedY != 0) {
        //    dispatchOnScrolled(consumedX, consumedY);
        //}

        if (!awakenScrollBars()) {
            invalidate();
        }

        //return consumedX != 0 || consumedY != 0;
        return false;
    }

    void considerReleasingGlowsOnScroll(int dx, int dy) {
        boolean needsInvalidate = false;
        if (mTopGlow != null && !mTopGlow.isFinished() && dy > 0) {
            mTopGlow.onRelease();
            needsInvalidate |= mTopGlow.isFinished();
        }

        if (mBottomGlow != null && !mBottomGlow.isFinished() && dy < 0) {
            mBottomGlow.onRelease();
            needsInvalidate |= mBottomGlow.isFinished();
        }

        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }

    }

    private boolean isReadyToOverScroll(boolean isPullDown) {
        final Adapter adapter = getAdapter();

        if ((null == adapter || adapter.getItemCount() <= 0)) {
            return false;
        } else {
            //View evaChild = getChildAt(0);
            //Log.d("SpringListView", " y " + y + " " + evaChild.getHeight() * (getFirstVisiblePosition()) + " top " + evaChild.getTop());
            //LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
            if (mGlowing) {
                return true;
            }
            if (isPullDown) {
                //return mAllowTopGlowing;
                //Log.d(TAG, "canScrollVertically(-1) " + canScrollVertically(-1));
                return mHorizontal?!canScrollHorizontally(-1):!canScrollVertically(-1);
            } else {
                return mHorizontal?!canScrollHorizontally(1):!canScrollVertically(1);
            }
        }
    }

    void setScrollState(int state) {
        if (state != mScrollState) {
            mScrollState = state;
        }
    }

    private void setActiveEdge(SpringEdgeEffect edge) {
        if (mActiveEdge != edge && mActiveEdge != null) {
            //mActiveEdge.mDistance = 0; //FIXME
        }
        mActiveEdge = edge;
    }

    private void finishScrollWithVelocity(float velocity) {
        mSpring.setStartVelocity(velocity);
        mSpring.setStartValue(mDampedScrollShift);
        mSpring.start();
    }

    private void pullGlows(float x, float overscrollX, float y, float overscrollY) {
        //Log.d(TAG, "pullGlows " + overscrollX + " " + overscrollY);

        boolean invalidate = false;

        if (mHorizontal) {
            if (x > getWidth() || x < 0) {
                return;
            }
        } else {
            if (y > getHeight() || y < 0) {
                return;
            }
        }

        float ratio = x/getWidth();
        float overscroll = overscrollX;
        if (!mHorizontal) {
            ratio = y/getHeight();
            overscroll = overscrollY;
        }
        if (overscroll < 0.0F && ratio < mPullGrowBottom && ratio > mPullGrowTop) {
            ensureTopGlow();
            //Log.d("SpringRecyclerView", "call onPull " + overscrollX);
            if (mTopGlow != null) {
                if (!mHorizontal) {
                    mTopGlow.onPull(-overscrollY / (float) getHeight(), x / (float) getWidth());
                } else {
                    mTopGlow.onPull(-overscrollX / (float) getWidth(), y / (float) getHeight());
                }
                mGlowing = true;
                invalidate = true;
            }
        } else if (overscroll > 0.0F && ratio > mPullGrowTop && ratio < mPullGrowBottom) {
            ensureBottomGlow();
            if (mBottomGlow != null) {
                if (!mHorizontal) {
                    mBottomGlow.onPull(overscrollY / (float) getHeight(), 1.0F - x / (float) getWidth());
                } else {
                    mBottomGlow.onPull(overscrollX / (float) getWidth(), 1.0F - y / (float) getHeight());
                }
                mGlowing = true;
                invalidate = true;
            }
        }

        if (invalidate || overscrollX != 0.0F || overscrollY != 0.0F) {
            postInvalidateOnAnimation();
        }
    }

    void ensureBottomGlow() {
        /*
        if (mEdgeEffectFactory == null) {
            throw new IllegalStateException("setEdgeEffectFactory first, please!");
        }
         */

        if (mBottomGlow == null) {
            mBottomGlow = mEdgeEffectFactory.createEdgeEffect(this, EdgeEffectFactory.DIRECTION_BOTTOM);
            if (getClipToPadding()) {
                mBottomGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
            } else {
                mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }

        }
    }

    void ensureTopGlow() {
        /*
        if (mEdgeEffectFactory == null) {
            throw new IllegalStateException("setEdgeEffectFactory first, please!");
        }

         */

        if (mTopGlow == null) {
            //mTopGlow = mEdgeEffectFactory.createEdgeEffect(this, 1);
            mTopGlow = mEdgeEffectFactory.createEdgeEffect(this, EdgeEffectFactory.DIRECTION_TOP);
            if (getClipToPadding()) {
                mTopGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
            } else {
                mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }

        }
    }

    private void resetScroll() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
        stopNestedScroll();
        releaseGlows();
    }

    private void releaseGlows() {
        boolean needsInvalidate = false;
        if (mTopGlow != null) {
            mTopGlow.onRelease();
            mGlowing = false;
            needsInvalidate |= mTopGlow.isFinished();
        }

        if (mBottomGlow != null) {
            mBottomGlow.onRelease();
            mGlowing = false;
            needsInvalidate |= mBottomGlow.isFinished();
        }

        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    private class SpringEdgeEffectFactory extends EdgeEffectFactory {

        @NonNull
        @Override
        protected EdgeEffect createEdgeEffect(RecyclerView view, int direction) {
            switch (direction) {
                case EdgeEffectFactory.DIRECTION_TOP:
                case EdgeEffectFactory.DIRECTION_LEFT:
                    return new SpringEdgeEffect(getContext(), +VELOCITY_MULTIPLIER);
                case EdgeEffectFactory.DIRECTION_BOTTOM:
                case EdgeEffectFactory.DIRECTION_RIGHT:
                    return new SpringEdgeEffect(getContext(), -VELOCITY_MULTIPLIER);
            }
            return super.createEdgeEffect(view, direction);
        }
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
            //Log.d(TAG, "onAbsorb " + velocity);
            finishScrollWithVelocity(velocity * mVelocityMultiplier);
        }

        @Override
        public void onPull(float deltaDistance, float displacement) {
            //Log.d(TAG, "onPull " + deltaDistance);
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
            finishScrollWithVelocity(0);
            mReleased = true;
        }
    }

    private void cancelScroll() {
        resetTouch();
        setScrollState(RecyclerView.SCROLL_STATE_IDLE);
    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }

        releaseGlows();
    }

    private void onPointerUp(MotionEvent e) {
        int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == this.mScrollPointerId) {
            int newIndex = actionIndex == 0 ? 1 : 0;
            mScrollPointerId = e.getPointerId(newIndex);
            mInitialTouchY = this.mLastTouchY = (int)(e.getY(newIndex) + 0.5F);
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

    public void setHorizontalOverScroll() {
        mHorizontal = true;
        addOnScrollListener(mScrollListener);
    }

    public void draw(Canvas canvas) {
        if (mDampedScrollShift != 0) {
            int saveCount = canvas.save();
            if (mHorizontal) {
                canvas.translate(mDampedScrollShift, 0);
            } else {
                canvas.translate(0, mDampedScrollShift);
            }

            super.draw(canvas);
            canvas.restoreToCount(saveCount);

            return;
        }

        super.draw(canvas);
    }

    float computeVelocity() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
        return mVelocityTracker.getXVelocity(mScrollPointerId);
    }

    /**
     * handle onTouchEvent/onInterceptTouchEvent in this class or not
     * @param t
     * true: override onTouchEvent/onInterceptTouchEvent
     * false: handle TouchEvent by super class RecyclerView
     */
    public void setHandleTouch(boolean t) {
        mHandleTouch = t;
    }
}
