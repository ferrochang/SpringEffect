package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.EdgeEffect;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static androidx.dynamicanimation.animation.SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_MEDIUM;


public class SpringListView2 extends ListView {
    private static final float STIFFNESS = (0.3f * STIFFNESS_MEDIUM + 0.7f * STIFFNESS_LOW);
    private static final float DAMPING_RATIO = DAMPING_RATIO_MEDIUM_BOUNCY;
    private static final float VELOCITY_MULTIPLIER = 0.3f;

    /**
     * not currently scrolling.
     * @see #getScrollState()
     */
    public static final int SCROLL_STATE_IDLE = 0;
    /**
     * currently being dragged by outside input such as user touch input.
     * @see #getScrollState()
     */
    public static final int SCROLL_STATE_DRAGGING = 1;
    /**
     * currently animating to a final position while not under
     * outside control.
     * @see #getScrollState()
     */
    public static final int SCROLL_STATE_SETTLING = 2;

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

    private VelocityTracker mVelocityTracker;

    private int mInitialTouchY;
    private int mLastTouchY;
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
    private SEdgeEffectFactory mEdgeEffectFactory;
    private SpringEdgeEffect mActiveEdge;
    private SpringAnimation mSpring;
    private float mDampedScrollShift = 0;
    private float mDistance = 0;
    private int mPullCount = 0;
    private int mMaxFlingVelocity;
    boolean mReadToOverScroll = false;
    private float mLastYVel = 0f;
    private int mFirstChildTop = 0;
    private float mLastX, mLastY;
    private int mDispatchScrollCounter;
    int mLastChildBottom;
    OnScrollListener mGivenOnScrollListener;
    OnScrollListenerWrapper mOnScrollListenerWrapper = new OnScrollListenerWrapper();

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
        ViewConfiguration vc = ViewConfiguration.get(this.getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mScrollStepConsumed = new int[2];
        mScrollOffset = new int[2];
        mNestedOffsets = new int[2];
        mScrollConsumed = new int[2];
        //mEdgeEffectFactory = new SEdgeEffectFactory();
        mEdgeEffectFactory = createViewEdgeEffectFactory();
        setEdgeEffectFactory(mEdgeEffectFactory);

        //mEffectHelper = new EffectHelper(SpringListView2.this);
        mSpring = new SpringAnimation(this, DAMPED_SCROLL, 0);
        mSpring.setSpring(new SpringForce(0)
                .setStiffness(STIFFNESS)
                .setDampingRatio(DAMPING_RATIO));

        setOnScrollListener(mOnScrollListenerWrapper);
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
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        //Log.d("SpringListView2", "onInterceptTouchEvent  " + action);
        int index;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mReadToOverScroll = false;
                mScrollPointerId = ev.getPointerId(0);
                index = ev.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    return false;
                }
                boolean checkPullup = (getLastVisiblePosition() == getAdapter().getCount() - 1);
                boolean readytoOs = isReadyToOverScroll(!checkPullup, 0);
                //Log.d("SpringListView2", "onInterceptTouchEvent " + readytoOs + " mScrollState " + mScrollState);

                if (!readytoOs) {
                    return super.onInterceptTouchEvent(ev);
                }
                mReadToOverScroll = true;
                mInitialTouchY = mLastTouchY = (int)(ev.getY() + 0.5F);
                if (mScrollState == SCROLL_STATE_SETTLING) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(SCROLL_STATE_DRAGGING);
                }

                mNestedOffsets[0] = mNestedOffsets[1] = 0;
                int nestedScrollAxis = 0;
                nestedScrollAxis |= 2;
                startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.clear();
                stopNestedScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                index = ev.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    return false;
                }
                int x = (int)(ev.getX(index) + 0.5F);
                int y = (int)(ev.getY(index) + 0.5F);
                if (mScrollState != 1) {
                    int dy = y - mInitialTouchY;
                    boolean startScroll = false;
                    if (Math.abs(dy) > mTouchSlop) {
                        mLastTouchY = y;
                        startScroll = true;
                    }

                    if (startScroll) {
                        setScrollState(SCROLL_STATE_DRAGGING);
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
                mScrollPointerId = ev.getPointerId(actionIndex);
                mInitialTouchY = mLastTouchY = (int)(ev.getY(actionIndex) + 0.5F);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(ev);
                break;
        }

        mLastX = ev.getX();
        mLastY = ev.getY();

        return super.onInterceptTouchEvent(ev);
        //return mScrollState == SCROLL_STATE_DRAGGING;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        boolean eventAddedToVelocityTracker = false;
        MotionEvent vtev = MotionEvent.obtain(ev);
        int action = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        if (action == 0) {
            mNestedOffsets[0] = mNestedOffsets[1] = 0;
        }

        vtev.offsetLocation((float)mNestedOffsets[0], (float)mNestedOffsets[1]);
        int nestedScrollAxis;

        //Log.d("SpringListView2", "onTouchEvent  " + action + " 1st " + getFirstVisiblePosition());
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int)(ev.getY() + 0.5F);
                int childcount = getChildCount();
                if (childcount > 0)
                    mLastChildBottom = getChildAt(childcount-1).getBottom();
                else
                    mLastChildBottom = 0;
                //if (mInitialTouchY > mLastChildBottom || mInitialTouchY < 0 || mInitialTouchY > getHeight())
                //    return false;

                nestedScrollAxis = 0;

                nestedScrollAxis |= 2;
                startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(vtev);
                eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
                float yvel = -mVelocityTracker.getYVelocity(mScrollPointerId);

                //Log.d("SpringListView", "onTouchEvent ;  yvel " + yvel + " mMaxFlingVelocity  " + mMaxFlingVelocity + "  Top " + getFirstVisiblePosition());
                if (yvel == 0.0F) {
                    setScrollState(SCROLL_STATE_IDLE);
                } else {
                    //fling((int)yvel);
                    //Log.d("SpringListView", "mGlowing " + mGlowing);
                    //if (!mGlowing) {
                    //    pullGlows(ev.getX(), (float) 0, ev.getY(), -yvel/6);
                    mLastYVel = yvel;


                }

                resetScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                nestedScrollAxis = ev.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e("SpringListView", "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    vtev.recycle();
                    return false;
                }

                int x = (int)(ev.getX(nestedScrollAxis) + 0.5F);
                int y = (int)(ev.getY(nestedScrollAxis) + 0.5F);
                int dy = mLastTouchY - y;

                //Log.d("SpringListView", "onTouchEvent ;  dy " + dy + " mScrollState " + mScrollState);

                if (dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffset)) {
                    //dx -= mScrollConsumed[0];
                    dy -= mScrollConsumed[1];
                    vtev.offsetLocation((float)mScrollOffset[0], (float)mScrollOffset[1]);
                    int[] var10000 = mNestedOffsets;
                    var10000[0] += mScrollOffset[0];
                    var10000 = mNestedOffsets;
                    var10000[1] += mScrollOffset[1];
                }

                if (mScrollState != SCROLL_STATE_DRAGGING) {
                    boolean startScroll = false;
                    if (Math.abs(dy) > mTouchSlop) {
                        if (dy > 0) {
                            dy -= mTouchSlop;
                        } else {
                            dy += mTouchSlop;
                        }

                        startScroll = true;
                    }

                    if (startScroll) {
                        setScrollState(SCROLL_STATE_DRAGGING);
                    }
                }

                if (mScrollState == SCROLL_STATE_DRAGGING) {
                    mLastTouchY = y - mScrollOffset[1];
                    if (scrollByInternal(0, dy, vtev)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                    //if (this.mGapWorker != null && (dx != 0 || dy != 0)) {
                    //    this.mGapWorker.postFromTraversal(this, dx, dy);
                    //}
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelScroll();
                break;
            case MotionEvent.ACTION_OUTSIDE:
            default:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mScrollPointerId = ev.getPointerId(actionIndex);
                mInitialTouchY = mLastTouchY = (int)(ev.getY(actionIndex) + 0.5F);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(ev);

        }
        if (!eventAddedToVelocityTracker) {
            mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        mLastX = ev.getX();
        mLastY = ev.getY();

        //return true;
        boolean res = super.onTouchEvent(ev);
        //Log.d("SpringListView", "onTouchEvent res " + res + " action " + action);
        return res;
    }

    boolean scrollByInternal(int x, int y, MotionEvent ev) {
        boolean readyToGo = isReadyToOverScroll(y < 0, y);
        //Log.d("SpringListView", "scrollByInternal y " + y + "  1st visible pos " + getFirstVisiblePosition());
        if (!readyToGo) {
            //onRecyclerViewScrolled();
            return false;
        }

        int unconsumedX = 0;
        int unconsumedY = 0;
        int consumedX = 0;
        int consumedY = 0;
        //consumePendingUpdateOperations();
        if (getAdapter() != null) {
            scrollStep(x, y, mScrollStepConsumed);
            consumedX = mScrollStepConsumed[0];
            consumedY = mScrollStepConsumed[1];
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

        if (!nestedScrollresult || mOverScrollNested) {
            if (getOverScrollMode() != 2) {
                if (ev != null && !ev.isFromSource(8194)) {
                    //Log.d("SpringListView", "call pullGlows;  y " + y + " unconsumedY " + unconsumedY);
                    pullGlows(ev.getX(), (float) unconsumedX, ev.getY(), (float) unconsumedY);
                }

                considerReleasingGlowsOnScroll(x, y);
            }
        }

        if (consumedX != 0 || consumedY != 0) {
            dispatchOnScrolled(consumedX, consumedY);
        }

        if (!awakenScrollBars()) {
            invalidate();
        }

        return consumedX != 0 || consumedY != 0;
    }

    private void pullGlows(float x, float overscrollX, float y, float overscrollY) {

        boolean invalidate = false;

        if (y > getHeight() || y < 0) {
            return;
        }

        float yRatio = y/getHeight();
        if (overscrollY < 0.0F && yRatio < mPullGrowBottom && yRatio > mPullGrowTop) {
            ensureTopGlow();
            //Log.d("SpringListView", " x " + x + " overscrollY " + overscrollY + " y " + y);
            if (mTopGlow != null) {
                mTopGlow.onPull(-overscrollY / (float) getHeight(), x / (float) getWidth());
                mGlowing = true;
                invalidate = true;
            }
        } else if (overscrollY > 0.0F && yRatio > mPullGrowTop && yRatio < mPullGrowBottom) {
            ensureBottomGlow();
            if (mBottomGlow != null) {
                mBottomGlow.onPull(overscrollY / (float) getHeight(), 1.0F - x / (float) getWidth());
                mGlowing = true;
                invalidate = true;
            }
        }

        if (invalidate || overscrollX != 0.0F || overscrollY != 0.0F) {
            postInvalidateOnAnimation();
        }
    }

    void scrollStep(int dx, int dy, int[] consumed) {
        /*
        startInterceptRequestLayout();
        this.onEnterLayoutOrScroll();
        TraceCompat.beginSection("RV Scroll");
        this.fillRemainingScrollValues(this.mState);
         */
        int consumedY = 0;

        if (dy != 0 && !mOverScrollNested) {
            /* bug fix: not recovered space after continuous pull up/down*/
            //scrollBy(0, dy);
            /* bugfix end*/
            //consumedY = dy;
            //consumedY = this.mLayout.scrollVerticallyBy(dy, this.mRecycler, this.mState);
        }

        /*
        TraceCompat.endSection();
        this.repositionShadowingViews();
        this.onExitLayoutOrScroll();
        this.stopInterceptRequestLayout(false);
         */
        if (consumed != null) {
            consumed[1] = consumedY;
        }

    }

    public void onScrolled(int dx, int dy) {
    }

    void ensureTopGlow() {
        if (mEdgeEffectFactory == null) {
            //throw new IllegalStateException("setEdgeEffectFactory first, please!");
            Log.e(TAG, "setEdgeEffectFactory first, please!");
            return;
        }

        if (mTopGlow == null) {
            mTopGlow = mEdgeEffectFactory.createEdgeEffect(this, 1);
            if (getClipToPadding()) {
                mTopGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
            } else {
                mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }

        }
    }

    void ensureBottomGlow() {
        if (mEdgeEffectFactory == null) {
            //throw new IllegalStateException("setEdgeEffectFactory first, please!");
            Log.e(TAG, "setEdgeEffectFactory first, please!");
            return;
        }

        if (mBottomGlow == null) {
            mBottomGlow = mEdgeEffectFactory.createEdgeEffect(this, 3);
            if (getClipToPadding()) {
                mBottomGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
            } else {
                mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }

        }
    }

    void dispatchOnScrolled(int hresult, int vresult) {
        ++mDispatchScrollCounter;
        int scrollX = this.getScrollX();
        int scrollY = this.getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(hresult, vresult);
        /*
        if (mScrollListener != null) {
            this.mScrollListener.onScrolled(this, hresult, vresult);
        }

        if (this.mScrollListeners != null) {
            for(int i = this.mScrollListeners.size() - 1; i >= 0; --i) {
                ((RecyclerView.OnScrollListener)this.mScrollListeners.get(i)).onScrolled(this, hresult, vresult);
            }
        }
         */

        --mDispatchScrollCounter;
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

    protected void setDampedScrollShift(float shift) {
        if (shift != mDampedScrollShift) {
            mDampedScrollShift = shift;
            //setTranslationY(mDampedScrollShift);
            invalidate();
        }
    }

    public void setEdgeEffectFactory(SEdgeEffectFactory edgeEffectFactory) {
        mEdgeEffectFactory = edgeEffectFactory;
        invalidateGlows();
    }

    void invalidateGlows() {
        mTopGlow = mBottomGlow = null;
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

    private void resetScroll() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
        stopNestedScroll();
        releaseGlows();
        //mScrollingDown = false;
        //mFirstChildTop = 0;
    }

    private void cancelScroll() {
        resetTouch();
        setScrollState(SCROLL_STATE_IDLE);
        //mFirstChildTop = 0;
    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }

        releaseGlows();
    }

    private void releaseGlows() {
        //Log.d("SpringListView", "releaseGlows");
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

    private void onPointerUp(MotionEvent e) {
        int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == this.mScrollPointerId) {
            int newIndex = actionIndex == 0 ? 1 : 0;
            mScrollPointerId = e.getPointerId(newIndex);
            mInitialTouchY = this.mLastTouchY = (int)(e.getY(newIndex) + 0.5F);
        }

    }

    private boolean isReadyToOverScroll(boolean isPullDown, int y) {
        final Adapter adapter = getAdapter();

        if ((null == adapter || adapter.isEmpty()) && (getFooterViewsCount() == 0 && getHeaderViewsCount() == 0)) {
            return false;
        } else {
            //View evaChild = getChildAt(0);
            //Log.d("SpringListView", " y " + y + " " + evaChild.getHeight() * (getFirstVisiblePosition()) + " top " + evaChild.getTop());
            if (isPullDown && getFirstVisiblePosition() == 0) {
                View firstVisibleChild = getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= getListPaddingTop();
                }
            } else if (!isPullDown && null != adapter && getLastVisiblePosition() == adapter.getCount() - 1) {
                View lastVisibleChild = getChildAt(getChildCount() - 1);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= getHeight()
                            - getListPaddingBottom();
                }
            }
        }
        return false;
    }
    void setScrollState(int state) {
        if (state != mScrollState) {
            mScrollState = state;
            //if (state != SCROLL_STATE_SETTLING) {
            //    stopScrollersInternal();
            //}
        }
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

    public ViewEdgeEffectFactory createViewEdgeEffectFactory() {
        return new ViewEdgeEffectFactory();
    }

    private class ViewEdgeEffectFactory extends SEdgeEffectFactory {
        @NonNull @Override
        protected EdgeEffect createEdgeEffect(View view, int direction) {
            switch (direction) {
                case DIRECTION_TOP:
                case DIRECTION_LEFT:
                    return new SpringEdgeEffect(getContext(), +VELOCITY_MULTIPLIER);
                case DIRECTION_BOTTOM:
                case DIRECTION_RIGHT:
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
            //android.util.Log.d("SpringRelativeLayout", "onAbsorb " + velocity);
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
            //android.util.Log.d("SpringRelativeLayout", " displacement " + displacement);
            //if (displacement < .4f) {
            mDistance += deltaDistance * (mVelocityMultiplier / 3f);
            //Log.d("SpringRelativeLayout", "onPull " + mDistance + " displacement " + displacement);
            setDampedScrollShift(mDistance * getHeight());
            //}
            mReleased = false;
        }

        @Override
        public void onRelease() {
            //Log.d("SpringRelativeLayout", "onRelease mDampedScrollShift " + mDampedScrollShift);
            if (mReleased) {
                return;
            }
            mDistance = 0;
            mPullCount = 0;
            //if (mDampedScrollShift != .0)
            //    mReadyToGo = false;
            finishScrollWithVelocity(0);
            mReleased = true;
        }
    }

    /**
     * Return the current scrolling state.
     *
     * @return {@link #SCROLL_STATE_IDLE}, {@link #SCROLL_STATE_DRAGGING} or
     * {@link #SCROLL_STATE_SETTLING}
     */
    public int getScrollState() {
        return mScrollState;
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
        if(isUserOnScrollListener(l))
            mGivenOnScrollListener = l;
        else
            super.setOnScrollListener(l);

    }

    boolean isUserOnScrollListener(OnScrollListener l) {
        return l != mOnScrollListenerWrapper;
    }

    class OnScrollListenerWrapper implements OnScrollListener {
        int state = SCROLL_STATE_IDLE;
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            state = scrollState;
            if(mGivenOnScrollListener != null) {
                mGivenOnScrollListener.onScrollStateChanged(absListView, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if(mGivenOnScrollListener != null) {
                mGivenOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            if (state == SCROLL_STATE_DRAGGING/* && mYdiff != 0*/) {
                onRecyclerViewScrolled();
            }

            if (state != SCROLL_STATE_SETTLING && state != SCROLL_STATE_DRAGGING) {
                return;
            }

            //Log.d("SpringListView2", "onScroll " + canScrollVertically(-1) + " " + canScrollVertically(1));
            if (!canScrollVertically(-1)) {
                if (!mGlowing) {
                    float yvel = mLastYVel;
                    if (yvel >= 0) {
                        yvel = computeVelocity();
                    }
                    //Log.d("SpringListView2", "should go " + yvel);
                    pullGlows(mLastX, (float) 0, mLastY, yvel / 20);
                    //ensureTopGlow();
                    if (mTopGlow != null) {
                        mTopGlow.onAbsorb((int) (yvel / 20));
                    }
                }
            }

            if (!canScrollVertically(1)) {
                if (!mGlowing) {
                    //Log.d("SpringListView2", "should go");
                    float yvel = mLastYVel;
                    if (yvel <= 0) {
                        yvel = computeVelocity();
                    }
                    pullGlows(mLastX, (float)0, mLastY, yvel / 20);
                    if (mBottomGlow != null) {
                        mBottomGlow.onAbsorb((int) (yvel / 20));
                    }
                }
            }
        }
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

    float computeVelocity() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
        return -mVelocityTracker.getYVelocity(mScrollPointerId);
    }


}
