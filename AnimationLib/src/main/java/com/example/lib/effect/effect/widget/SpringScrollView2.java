package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SpringScrollView2 extends ScrollView {
    private static final float STIFFNESS = (0.3f * SpringForce.STIFFNESS_MEDIUM + 0.7f * SpringForce.STIFFNESS_LOW);
    private static final float DAMPING_RATIO = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
    private static final float VELOCITY_MULTIPLIER = 0.3f;

    private SEdgeEffectFactory mEdgeEffectFactory;
    private EdgeEffect mTopGlow;
    private EdgeEffect mBottomGlow;
    private int mScrollState;
    private int mScrollPointerId;
    private VelocityTracker mVelocityTracker;
    private int mInitialTouchY;
    private int mLastTouchX;
    private int mLastTouchY;
    private int mTouchSlop;
    private int mMaxFlingVelocity;
    private int[] mScrollOffset;
    private int mDispatchScrollCounter;
    int[] mScrollStepConsumed;
    private int[] mNestedOffsets;
    int[] mScrollConsumed;
    boolean mOverScrollNested = false;
    float mPullGrowTop = 0.1f;
    float mPullGrowBottom = 0.9f;
    //private boolean mGlowing = false;
    private boolean mGlowingTop = false;
    private boolean mGlowingBottom = false;
    private float mLastX, mLastY;
    private float mLastYVel;
    private int mScrollRangeY;
    private boolean mRecycleScrolled = false;

    private float mDistance = 0;
    private int mPullCount = 0;
    private SpringAnimation mSpring;

    private SpringEdgeEffect mActiveEdge;
    private float mDampedScrollShift = 0;

    private static final FloatPropertyCompat<SpringScrollView2> DAMPED_SCROLL =
            new FloatPropertyCompat<SpringScrollView2>("value") {

                @Override
                public float getValue(SpringScrollView2 object) {
                    return object.mDampedScrollShift;
                }

                @Override
                public void setValue(SpringScrollView2 object, float value) {
                    object.setDampedScrollShift(value);
                }
            };

    public SpringScrollView2(Context context) {
        super(context);
        init();
    }

    public SpringScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpringScrollView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
        mEdgeEffectFactory = createViewEdgeEffectFactory();
        setEdgeEffectFactory(mEdgeEffectFactory);
        mSpring = new SpringAnimation(this, DAMPED_SCROLL, 0);
        mSpring.setSpring(new SpringForce(0)
                .setStiffness(STIFFNESS)
                .setDampingRatio(DAMPING_RATIO));
    }


    public void setEdgeEffectFactory(@NonNull SEdgeEffectFactory edgeEffectFactory) {
        mEdgeEffectFactory = edgeEffectFactory;
        invalidateGlows();
    }

    void invalidateGlows() {
        mTopGlow = mBottomGlow = null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        //boolean eventAddedToVelocityTracker = false;
        MotionEvent vtev = MotionEvent.obtain(ev);

        //Log.d("SpringScrollView", "onInterceptTouchEvent  " + action);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int)(ev.getY() + 0.5F);

                if (mScrollState == 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                }

                mNestedOffsets[0] = mNestedOffsets[1] = 0;
                int nestedScrollAxis = 0;
                //nestedScrollAxis |= 2;
                //startNestedScroll(nestedScrollAxis);
                mRecycleScrolled = false;
                break;
            case MotionEvent.ACTION_UP:
                //mVelocityTracker.clear();
                //stopNestedScroll();

                mVelocityTracker.addMovement(vtev);
                //eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
                float yvel = -mVelocityTracker.getYVelocity(mScrollPointerId);

                if (yvel == 0.0F) {
                    setScrollState(0);
                } else {
                    //fling((int)yvel);
                }

                resetTouch();
                //stopNestedScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                /*
                int index = ev.findPointerIndex(mScrollPointerId);
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
                        setScrollState(1);
                    }
                }
                 */
                nestedScrollAxis = ev.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e("SpringScrollView", "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
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

                if (mScrollState != 1) {
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
                        setScrollState(1);
                    }
                }

                if (mScrollState == 1) {
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
                cancelTouch();
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
        //if (!intercept)
        //    return false;
        //return mScrollState == 1;
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

        //Log.d("SpringScrollView", "onTouchEvent  " + action);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int)(ev.getY() + 0.5F);
                //int childcount = getChildCount();
                //int lastChildBottom = 0;
                //if (childcount > 0)
                //    lastChildBottom = getChildAt(childcount-1).getBottom();
                //if (mInitialTouchY > lastChildBottom || mInitialTouchY < 0 || mInitialTouchY > getHeight())
                //    return false;

                nestedScrollAxis = 0;

                //nestedScrollAxis |= 2;
                //startNestedScroll(nestedScrollAxis);
                mRecycleScrolled = false;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(vtev);
                eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
                float yvel = -mVelocityTracker.getYVelocity(mScrollPointerId);

                //Log.d("SpringListView", "onTouchEvent ;  yvel " + yvel + " mMaxFlingVelocity  " + mMaxFlingVelocity + "  Top " + getFirstVisiblePosition());
                if (yvel == 0.0F) {
                    setScrollState(0);
                } else {
                    //fling((int)yvel);
                    //Log.d("SpringScrollView", "mGlowing " + mGlowing);
                    //if (!mGlowing) {
                        //    pullGlows(ev.getX(), (float) 0, ev.getY(), -yvel/6);
                    mLastYVel = yvel;
                }

                resetTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                nestedScrollAxis = ev.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e("SpringScrollView", "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
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

                if (mScrollState != 1) {
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
                        setScrollState(1);
                    }
                }

                if (mScrollState == 1) {
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
                cancelTouch();
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
        return super.onTouchEvent(ev);
    }

    void ensureTopGlow() {
        if (mEdgeEffectFactory == null) {
            //throw new IllegalStateException("setEdgeEffectFactory first, please!");
            Log.e("SpringScrollView2", "setEdgeEffectFactory first, please!");
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
            Log.e("SpringScrollView2", "setEdgeEffectFactory first, please!");
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

    private void pullGlows(float x, float overscrollX, float y, float overscrollY) {

        boolean invalidate = false;

        if (y > getHeight() || y < 0) {
            return;
        }

        float yRatio = y/getHeight();
        if (overscrollY < 0.0F && yRatio < mPullGrowBottom && yRatio > mPullGrowTop) {
            ensureTopGlow();
            //Log.d("SpringScrollView", " x " + x + " overscrollY " + overscrollY + " y " + y);
            if (mTopGlow != null) {
                mTopGlow.onPull(-overscrollY / (float) getHeight(), x / (float) getWidth());
                //mGlowing = true;
                mGlowingTop = true;
                invalidate = true;
            }
        } else if (overscrollY > 0.0F && yRatio > mPullGrowTop && yRatio < mPullGrowBottom) {
            ensureBottomGlow();
            //Log.d("SpringScrollView", " overscrollY " + overscrollY + " y " + y);
            if (mBottomGlow != null) {
                mBottomGlow.onPull(overscrollY / (float) getHeight(), 1.0F - x / (float) getWidth());
                //mGlowing = true;
                mGlowingBottom = true;
                invalidate = true;
            }
        }

        if (invalidate || overscrollX != 0.0F || overscrollY != 0.0F) {
            postInvalidateOnAnimation();
        }
    }

    void setScrollState(int state) {
        if (state != mScrollState) {
            mScrollState = state;
            //if (state != 2) {
            //    stopScrollersInternal();
            //}
        }
    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }

        releaseGlows();
    }

    private void releaseGlows() {
        boolean needsInvalidate = false;
        if (mTopGlow != null) {
            mTopGlow.onRelease();
            //mGlowing = false;
            mGlowingTop = false;
            needsInvalidate |= mTopGlow.isFinished();
        }

        if (mBottomGlow != null) {
            mBottomGlow.onRelease();
            //mGlowing = false;
            mGlowingBottom = false;
            needsInvalidate |= mBottomGlow.isFinished();
        }

        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    private void onPointerUp(MotionEvent e) {
        int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == this.mScrollPointerId) {
            int newIndex = actionIndex == 0 ? 1 : 0;
            mScrollPointerId = e.getPointerId(newIndex);
            mInitialTouchY = this.mLastTouchY = (int)(e.getY(newIndex) + 0.5F);
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

    public void onScrolled(@Px int dx, @Px int dy) {
    }
    boolean scrollByInternal(int x, int y, MotionEvent ev) {
        boolean readyToGo = isReadyToOverScroll(y < 0);
        //Log.d("SpringScrollView", "scrollByInternal y " + y + " readyToGo " + readyToGo);
        if (!readyToGo) {
            /*
            if (mSpringLayout == null) {
                ViewGroup vg = (ViewGroup) getParent();
                if (vg instanceof SpringRelativeLayout) {
                    mSpringLayout = (SpringRelativeLayout) vg;
                }
            }
            if (mSpringLayout != null && y != 0) {
                mSpringLayout.onRecyclerViewScrolled();
            }
             */

            return false;
        }

        int unconsumedX = 0;
        int unconsumedY = 0;
        int consumedX = 0;
        int consumedY = 0;
        //consumePendingUpdateOperations();
        if (getChildCount() >= 0) {
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

    void scrollStep(int dx, int dy, @Nullable int[] consumed) {
        /*
        startInterceptRequestLayout();
        this.onEnterLayoutOrScroll();
        TraceCompat.beginSection("RV Scroll");
        this.fillRemainingScrollValues(this.mState);
         */
        int consumedY = 0;

        if (dy != 0 && !mOverScrollNested) {
            //scrollBy(0, dy); //Bugfix, not recoverable space after continuous scrolling up/down
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

    private boolean isReadyToOverScroll(boolean isPullDown) {

        if (getChildCount() <= 0) {
            return false;
        } else {
            if (isPullDown) {
                return !canScrollVertically(-1);
                /*
                View firstVisibleChild = getChildAt(0);
                if (firstVisibleChild != null) {
                    return (getScrollY() == 0);
                }
                 */
            } else if (!isPullDown) {
                return !canScrollVertically(1);
                /*
                View lastVisibleChild = getChildAt(getChildCount() - 1);

                if (lastVisibleChild != null) {
                    //return lastVisibleChild.getBottom() <= getHeight()
                    //        - getPaddingBottom();
                    int diff = lastVisibleChild.getBottom() - (getHeight() + getScrollY());
                    //Log.d("SpringScrollView", "lastVisibleChild " + lastVisibleChild + " diff " + diff + " height " + getHeight());

                    return (diff <= 0);
                }

                 */
            }
        }
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

    /**
     * control pullGlow & ScrollBy in scroll nested
     * @param b
     * true: work with pullToRefresh;
     * false: default mode
     */
    public void setOverScrollNested(boolean b) {
        mOverScrollNested = b;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        boolean ovs = super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0,
                0, isTouchEvent);
        mScrollRangeY = scrollRangeY;

        return ovs;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Log.d("SpringScrollView2", "onScrollChanged " + t + " " + mLastYVel);
        /*
        if (t == 0 && t < oldt) {
            if (!mGlowing) {
                pullGlows(mLastX, (float) 0, mLastY, mLastYVel / 20);
                //ensureTopGlow();
                if (mTopGlow != null) {
                    mTopGlow.onAbsorb((int) (mLastYVel / 20));
                }
            }
        } else if (t == mScrollRangeY && t > oldt){
            if (!mGlowing) {
                pullGlows(mLastX, (float) 0, mLastY, mLastYVel / 20);
                //ensureBottomGlow();
                if (mBottomGlow != null) {
                    mBottomGlow.onAbsorb((int) (mLastYVel / 20));
                }
            }
        }
         */
        //Log.d("SpringNestScrollView", "onScrollChanged " + t + " mGlowingTop " + mGlowingTop + " " + canScrollVertically(-1));
        if (mGlowingTop) {
            if (canScrollVertically(-1) && t > oldt) {
                onRecyclerViewScrolled();
                mRecycleScrolled = true;
                //Log.d("SpringNestScrollView", "mRecycleScrolled");
            }
        }

        if (mGlowingBottom) {
            if (canScrollVertically(1) && t < oldt) {
                onRecyclerViewScrolled();
                mRecycleScrolled = true;
            }
        }

        if (!mGlowingTop) {
            if (!canScrollVertically(-1) && t < oldt) {
                float yvel = mLastYVel;
                if (yvel >= 0) {
                    /**
                     * overscroll-by-fling happened before MotionEvent.ACTION_UP
                     */
                    yvel = computeVelocity();
                }
                //Log.d("SpringNestScrollView", "ready go");
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
                    /**
                     * overscroll-by-fling happened before MotionEvent.ACTION_UP
                     */
                    yvel = computeVelocity();
                }
                pullGlows(mLastX, (float) 0, mLastY, yvel / 20);
                //ensureBottomGlow();
                if (mBottomGlow != null) {
                    mBottomGlow.onAbsorb((int) (yvel / 20));
                }
            }
        }


        super.onScrollChanged(l, t, oldl, oldt);
    }

    void onRecyclerViewScrolled() {
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

    private class SpringPro implements SpringEdgeEffect.ISpringPro {

        @Override
        public void finishScrollWithVelocity(float velocity) {
            finishScrollWithVelocity(velocity);
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

    private void finishScrollWithVelocity(float velocity) {
        //if (mAnimationEndListener != null)
        //    mSpring.addEndListener(mAnimationEndListener);
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

    protected void setDampedScrollShift(float shift) {
        if (shift != mDampedScrollShift) {
            mDampedScrollShift = shift;
            //setTranslationY(mDampedScrollShift);
            invalidate();
        }
    }

    public ViewEdgeEffectFactory createViewEdgeEffectFactory() {
        return new ViewEdgeEffectFactory();
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
