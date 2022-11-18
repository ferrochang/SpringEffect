package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

public class SpringHorizontalScrollView extends HorizontalScrollView {
    private SEdgeEffectFactory mEdgeEffectFactory;
    private EdgeEffect mLeftGlow;
    private EdgeEffect mRightGlow;
    private int mScrollState;
    private int mScrollPointerId;
    private VelocityTracker mVelocityTracker;
    private int mInitialTouchX;
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
    float mPullGrowLeft = 0.1f;
    float mPullGrowRight = 0.9f;
    private SpringRelativeLayout mSpringLayout = null;
    private boolean mGlowing = false;
    private int mScrollRangeX;
    private float mLastX, mLastY;
    private float mLastXVel;

    public SpringHorizontalScrollView(Context context) {
        super(context);
        init();
    }

    public SpringHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpringHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        MotionEvent vtev = MotionEvent.obtain(ev);

        //Log.d("SpringHorizontalScrollView", "onInterceptTouchEvent  " + action);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchX = mLastTouchX = (int)(ev.getX() + 0.5F);
                //int childcount = getChildCount();
                //int lastChildEnd = 0;
                //if (childcount > 0)
                //    lastChildEnd = getChildAt(childcount-1).getRight();
                //Log.d("SpringScrollView", "onInterceptTouchEvent  mInitialTouchY " + mInitialTouchY + " lastChildBottom " + lastChildBottom + " height " + getHeight());
                //if (mInitialTouchX > lastChildEnd || mInitialTouchX < 0 || mInitialTouchX > getWidth())
                //    return false;

                if (mScrollState == 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                }

                mNestedOffsets[0] = mNestedOffsets[1] = 0;
                int nestedScrollAxis = 0;
                nestedScrollAxis |= 2;
                startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_UP:
                //mVelocityTracker.clear();
                mVelocityTracker.addMovement(vtev);
                //eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
                float xvel = -mVelocityTracker.getXVelocity(mScrollPointerId);

                //Log.d("SpringListView", "onTouchEvent ;  yvel " + yvel + " mMaxFlingVelocity  " + mMaxFlingVelocity + "  Top " + getFirstVisiblePosition());
                if (xvel == 0.0F) {
                    setScrollState(0);
                } else {
                    //fling((int)xvel);
                }

                resetTouch();

                //stopNestedScroll();
                break;
            case MotionEvent.ACTION_MOVE:

                nestedScrollAxis = ev.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e("SpringHorizontalScrollView", "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    vtev.recycle();
                    return false;
                }

                int x = (int)(ev.getX(nestedScrollAxis) + 0.5F);
                int y = (int)(ev.getY(nestedScrollAxis) + 0.5F);
                int dx = mLastTouchX - x;

                //Log.d("SpringListView", "onTouchEvent ;  dy " + dy + " mScrollState " + mScrollState);

                if (dispatchNestedPreScroll(dx, 0, mScrollConsumed, mScrollOffset)) {
                    dx -= mScrollConsumed[0];
                    //dy -= mScrollConsumed[1];
                    vtev.offsetLocation((float)mScrollOffset[0], (float)mScrollOffset[1]);
                    int[] var10000 = mNestedOffsets;
                    var10000[0] += mScrollOffset[0];
                    var10000 = mNestedOffsets;
                    var10000[1] += mScrollOffset[1];
                }

                if (mScrollState != 1) {
                    boolean startScroll = false;
                    if (Math.abs(dx) > mTouchSlop) {
                        if (dx > 0) {
                            dx -= mTouchSlop;
                        } else {
                            dx += mTouchSlop;
                        }

                        startScroll = true;
                    }

                    if (startScroll) {
                        setScrollState(1);
                    }
                }

                if (mScrollState == 1) {
                    mLastTouchX = x - mScrollOffset[0];
                    if (scrollByInternal(dx, 0, vtev)) {
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
                mInitialTouchX = mLastTouchX = (int)(ev.getX(actionIndex) + 0.5F);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(ev);
                break;
        }

        vtev.recycle();
        mLastX = ev.getX();
        mLastY = ev.getY();
        boolean intercept = super.onInterceptTouchEvent(ev);
        //Log.d("SpringScrollView", "intercept " + intercept + " mScrollState " + mScrollState);
        return intercept;
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

        //Log.d("SpringHorizontalScrollView", "onTouchEvent  " + action);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchX = mLastTouchX = (int)(ev.getX() + 0.5F);
                //int childcount = getChildCount();
                //nt lastChildEnd = 0;
                //if (childcount > 0)
                //    lastChildEnd = getChildAt(childcount-1).getRight();
                //if (mInitialTouchX > lastChildEnd || mInitialTouchX < 0 || mInitialTouchX > getWidth())
                //    return false;

                nestedScrollAxis = 0;

                nestedScrollAxis |= 2;
                startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(vtev);
                eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
                float xvel = -mVelocityTracker.getXVelocity(mScrollPointerId);

                //Log.d("SpringListView", "onTouchEvent ;  yvel " + yvel + " mMaxFlingVelocity  " + mMaxFlingVelocity + "  Top " + getFirstVisiblePosition());
                if (xvel == 0.0F) {
                    setScrollState(0);
                } else {
                    //fling((int)xvel);
                    //Log.d("SpringHScrollView", "mGlowing " + mGlowing);
                    //if (!mGlowing) {
                        //pullGlows(ev.getX(), -xvel/6, ev.getY(), 0);
                    mLastXVel = xvel;
                }

                resetTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                nestedScrollAxis = ev.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e("SpringHorizontalScrollView", "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    vtev.recycle();
                    return false;
                }

                int x = (int)(ev.getX(nestedScrollAxis) + 0.5F);
                int y = (int)(ev.getY(nestedScrollAxis) + 0.5F);
                int dx = mLastTouchX - x;

                //Log.d("SpringListView", "onTouchEvent ;  dy " + dy + " mScrollState " + mScrollState);

                if (dispatchNestedPreScroll(dx, 0, mScrollConsumed, mScrollOffset)) {
                    dx -= mScrollConsumed[0];
                    //dy -= mScrollConsumed[1];
                    vtev.offsetLocation((float)mScrollOffset[0], (float)mScrollOffset[1]);
                    int[] var10000 = mNestedOffsets;
                    var10000[0] += mScrollOffset[0];
                    var10000 = mNestedOffsets;
                    var10000[1] += mScrollOffset[1];
                }

                if (mScrollState != 1) {
                    boolean startScroll = false;
                    if (Math.abs(dx) > mTouchSlop) {
                        if (dx > 0) {
                            dx -= mTouchSlop;
                        } else {
                            dx += mTouchSlop;
                        }

                        startScroll = true;
                    }

                    if (startScroll) {
                        setScrollState(1);
                    }
                }

                if (mScrollState == 1) {
                    mLastTouchX = x - mScrollOffset[0];
                    if (scrollByInternal(dx, 0, vtev)) {
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
                mInitialTouchX = mLastTouchX = (int)(ev.getX(actionIndex) + 0.5F);
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

    public void setEdgeEffectFactory(@NonNull SEdgeEffectFactory edgeEffectFactory) {
        mEdgeEffectFactory = edgeEffectFactory;
        invalidateGlows();
    }

    void invalidateGlows() {
        mLeftGlow = mRightGlow = null;
    }

    private void releaseGlows() {
        boolean needsInvalidate = false;
        if (mLeftGlow != null) {
            mLeftGlow.onRelease();
            mGlowing = false;
            needsInvalidate |= mLeftGlow.isFinished();
        }

        if (mRightGlow != null) {
            mRightGlow.onRelease();
            mGlowing = false;
            needsInvalidate |= mRightGlow.isFinished();
        }

        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }

        releaseGlows();
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    void setScrollState(int state) {
        if (state != mScrollState) {
            mScrollState = state;
            //if (state != 2) {
            //    stopScrollersInternal();
            //}
        }
    }

    private void onPointerUp(MotionEvent e) {
        int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == this.mScrollPointerId) {
            int newIndex = actionIndex == 0 ? 1 : 0;
            mScrollPointerId = e.getPointerId(newIndex);
            mInitialTouchX = this.mLastTouchX = (int)(e.getX(newIndex) + 0.5F);
        }

    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        boolean ovs = super.overScrollBy(deltaX, 0, scrollX, 0, scrollRangeX, 0, 0,
                0, isTouchEvent);
        mScrollRangeX = scrollRangeX;
        return ovs;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Log.d("SpringHorizontalScrollView", "onScrollChanged " + l + " " + oldl + " " + canScrollHorizontally(-1) + " " + canScrollHorizontally(1));
        if (!canScrollHorizontally(-1) && t < oldl) {
            if (!mGlowing) {
                float xvel = mLastXVel;
                if (xvel >= 0) {
                    xvel = computeVelocity();
                }
                pullGlows(mLastX, (float) xvel / 20, mLastY, 0);
                //ensureTopGlow();
                if (mLeftGlow != null) {
                    mLeftGlow.onAbsorb((int) (xvel / 20));
                }
            }
        }

        if (!canScrollHorizontally(1) && l > oldl){
            if (!mGlowing) {
                float xvel = mLastXVel;
                if (xvel <= 0) {
                    xvel = computeVelocity();
                }
                pullGlows(mLastX, (float) xvel / 20, mLastY, 0);
                //ensureTopGlow();
                if (mRightGlow != null) {
                    mRightGlow.onAbsorb((int) (xvel / 20));
                }
            }
        }
    }

    void considerReleasingGlowsOnScroll(int dx, int dy) {
        boolean needsInvalidate = false;
        if (mLeftGlow != null && !mLeftGlow.isFinished() && dx > 0) {
            mLeftGlow.onRelease();
            needsInvalidate |= mLeftGlow.isFinished();
        }

        if (mRightGlow != null && !mRightGlow.isFinished() && dx < 0) {
            mRightGlow.onRelease();
            needsInvalidate |= mRightGlow.isFinished();
        }

        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }

    }

    private boolean isReadyToOverScroll(boolean isPullRight) {

        if (getChildCount() <= 0) {
            return false;
        } else {

            if (isPullRight) {
                //View firstVisibleChild = getChildAt(0);
                //if (firstVisibleChild != null) {
                //    return (getScrollX() == 0);
                //}
                return !canScrollHorizontally(-1);
            } else if (!isPullRight) {
                /*
                View lastVisibleChild = getChildAt(getChildCount() - 1);

                if (lastVisibleChild != null) {
                    //return lastVisibleChild.getBottom() <= getHeight()
                    //        - getPaddingBottom();
                    int diff = lastVisibleChild.getRight() - (getWidth() + getScrollX());
                    //Log.d("SpringScrollView", "lastVisibleChild " + lastVisibleChild + " diff " + diff + " height " + getHeight());

                    return (diff <= 0);
                }
                 */
                return !canScrollHorizontally(1);
            }
        }
        return false;
    }

    void ensureLeftGlow() {
        if (mEdgeEffectFactory == null) {
            throw new IllegalStateException("setEdgeEffectFactory first, please!");
        }

        if (mLeftGlow == null) {
            mLeftGlow = mEdgeEffectFactory.createEdgeEffect(this, SEdgeEffectFactory.DIRECTION_LEFT);
            if (getClipToPadding()) {
                mLeftGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
            } else {
                mLeftGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }

        }
    }

    void ensureRightGlow() {
        if (mEdgeEffectFactory == null) {
            throw new IllegalStateException("setEdgeEffectFactory first, please!");
        }

        if (mRightGlow == null) {
            mRightGlow = mEdgeEffectFactory.createEdgeEffect(this, SEdgeEffectFactory.DIRECTION_RIGHT);
            if (getClipToPadding()) {
                mRightGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
            } else {
                mRightGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }

        }
    }

    private void pullGlows(float x, float overscrollX, float y, float overscrollY) {

        boolean invalidate = false;

        if (x > getWidth() || x < 0) {
            return;
        }

        float xRatio = x/getWidth();
        if (overscrollX < 0.0F && xRatio < mPullGrowRight && xRatio > mPullGrowLeft) {
            ensureLeftGlow();
            //Log.d("SpringScrollView", " x " + x + " overscrollY " + overscrollY + " y " + y);
            mLeftGlow.onPull(-overscrollX / (float)getWidth(), y / (float)getHeight());
            mGlowing = true;
            invalidate = true;
        } else if (overscrollX > 0.0F && xRatio > mPullGrowLeft && xRatio < mPullGrowRight) {
            ensureRightGlow();
            //Log.d("SpringScrollView", " overscrollY " + overscrollY + " y " + y);
            mRightGlow.onPull(overscrollX / (float)getWidth(), 1.0F - y / (float)getHeight());
            mGlowing = true;
            invalidate = true;
        }

        if (invalidate || overscrollX != 0.0F || overscrollY != 0.0F) {
            postInvalidateOnAnimation();
        }
    }

    boolean scrollByInternal(int x, int y, MotionEvent ev) {
        boolean readyToGo = isReadyToOverScroll(x < 0);
        //Log.d("SpringScrollView", "scrollByInternal y " + y + " readyToGo " + readyToGo);
        if (!readyToGo) {
            if (mSpringLayout == null) {
                ViewGroup vg = (ViewGroup) getParent();
                if (vg instanceof SpringRelativeLayout) {
                    mSpringLayout = (SpringRelativeLayout) vg;
                }
            }
            if (mSpringLayout != null && x != 0) {
                mSpringLayout.onRecyclerViewScrolled();
            }

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

    public void onScrolled(@Px int dx, @Px int dy) {
    }

    void dispatchOnScrolled(int hresult, int vresult) {
        ++mDispatchScrollCounter;
        int scrollX = this.getScrollX();
        int scrollY = this.getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(hresult, vresult);

        --mDispatchScrollCounter;
    }

    void scrollStep(int dx, int dy, @Nullable int[] consumed) {
        int consumedX = 0;

        if (dx != 0 && !mOverScrollNested) {
            //scrollBy(dx, 0); //Bugfix, not recoverable space after continuous scrolling up/down
            //consumedY = this.mLayout.scrollVerticallyBy(dy, this.mRecycler, this.mState);
        }

        if (consumed != null) {
            consumed[0] = consumedX;
        }

    }

    float computeVelocity() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
        return -mVelocityTracker.getXVelocity(mScrollPointerId);
    }

}
