package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EdgeEffect;

import androidx.annotation.Nullable;
import androidx.annotation.Px;

public class SpringWebView extends WebView {
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
    boolean mAllowTopGlow = false;
    boolean mAllowBottomGlow = false;
    private SpringRelativeLayout mSpringLayout;
    private boolean mGlowing = false;
    //private float mScrollRangeY;
    private float mLastYVel;
    private float mLastX, mLastY;

    public SpringWebView(Context context) {
        super(context);
        init();
    }

    public SpringWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpringWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    public void setEdgeEffectFactory(SEdgeEffectFactory edgeEffectFactory) {
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
        MotionEvent vtev = MotionEvent.obtain(ev);

        //Log.d("SpringScrollView", "onInterceptTouchEvent  " + action);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int)(ev.getY() + 0.5F);
                //int childcount = getChildCount();
                //int lastChildBottom = 0;
                //if (childcount > 0)
                //    lastChildBottom = getChildAt(childcount-1).getBottom();
                //Log.d("SpringScrollView", "onInterceptTouchEvent  mInitialTouchY " + mInitialTouchY + " lastChildBottom " + lastChildBottom + " height " + getHeight());
                //if (mInitialTouchY > lastChildBottom || mInitialTouchY < 0 || mInitialTouchY > getHeight())
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
                float yvel = -mVelocityTracker.getYVelocity(mScrollPointerId);

                //Log.d("SpringListView", "onTouchEvent ;  yvel " + yvel + " mMaxFlingVelocity  " + mMaxFlingVelocity + "  Top " + getFirstVisiblePosition());
                if (yvel == 0.0F) {
                    setScrollState(0);
                } else {
                    //fling((int)yvel);
                    //flingScroll(0, (int)yvel);
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
        //Log.d("SpringWebView", "onTouchEvent  " + action);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int) (ev.getY() + 0.5F);

                //int height = (int) Math.floor(getContentHeight() * getScale());
                //int webViewHeight = getHeight();
                //int yPos = getScrollY();
                //Log.d("SpringWebView", "height " + height + " webViewHeight " + webViewHeight + " yPos " + yPos);
                //if (mInitialTouchY > lastChildBottom || mInitialTouchY < 0 || mInitialTouchY > getHeight())
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
                    setScrollState(0);
                } else {
                    //fling((int)yvel);
                    //flingScroll(0, (int)yvel);
                    //Log.d("SpringWebView", "mGlowing " + mGlowing);
                    //if (!mGlowing)
                    //    pullGlows(ev.getX(), (float) 0, ev.getY(), -yvel/6);
                    mLastYVel = yvel;
                }

                resetTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                nestedScrollAxis = ev.findPointerIndex(mScrollPointerId);
                if (nestedScrollAxis < 0) {
                    Log.e("SpringWebView", "Error processing scroll; pointer index for id " + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
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
        return super.onTouchEvent(ev);
    }

    void ensureTopGlow() {
        if (mEdgeEffectFactory == null) {
            //throw new IllegalStateException("setEdgeEffectFactory first, please!");
            Log.e("SpringWebView", "setEdgeEffectFactory first, please!");
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
            Log.e("SpringWebView", "setEdgeEffectFactory first, please!");
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
                mGlowing = true;
                invalidate = true;
            }
        } else if (overscrollY > 0.0F && yRatio > mPullGrowTop && yRatio < mPullGrowBottom) {
            ensureBottomGlow();
            //Log.d("SpringWebView", " overscrollY " + overscrollY + " y " + y);
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

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //Log.d("SpringWebView", "overScrollBy " + scrollRangeY + " " + scrollY);
        mAllowTopGlow = mAllowBottomGlow = false;
        if (scrollY == 0)
            mAllowTopGlow = true;
        if (scrollY == scrollRangeY) {
            mAllowBottomGlow = true;
        }
        boolean ovs = super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0,
                0, isTouchEvent);
        return ovs;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Log.d("SpringWebView", "onScrollChanged " + t + " oldt " + oldt + " " + canScrollVertically(1));
        if (!canScrollVertically(-1) && t < oldt) {
            if (!mGlowing) {
                float yvel = mLastYVel;
                if (yvel >= 0) {
                    /**
                     * overscroll-by-fling happened before MotionEvent.ACTION_UP
                     */
                    yvel = computeVelocity();
                }

                pullGlows(mLastX, (float) 0, mLastY, yvel/20);
                //ensureTopGlow();
                if (mTopGlow != null) {
                    mTopGlow.onAbsorb((int) (yvel / 20));
                }
            }
        }

        if (!canScrollVertically(1) && t > oldt) {
            if (!mGlowing) {
                float yvel = mLastYVel;
                if (yvel <= 0) {
                    /**
                     * overscroll-by-fling happened before MotionEvent.ACTION_UP
                     */
                    yvel = computeVelocity();
                }

                pullGlows(mLastX, (float) 0, mLastY, yvel / 20);
                //ensureTopGlow();
                if (mBottomGlow != null) {
                    mBottomGlow.onAbsorb((int) (yvel / 20));
                }
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

    public void onScrolled(@Px int dx, @Px int dy) {
    }

    boolean scrollByInternal(int x, int y, MotionEvent ev) {
        boolean readyToGo = isReadyToOverScroll(y < 0);
        //Log.d("SpringWebView", "scrollByInternal y " + y + " readyToGo " + readyToGo);
        if (!readyToGo) {
            if (mSpringLayout == null) {
                ViewGroup vg = (ViewGroup) getParent();
                if (vg instanceof SpringRelativeLayout) {
                    mSpringLayout = (SpringRelativeLayout) vg;
                }
            }
            if (mSpringLayout != null && y != 0) {
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

            if (isPullDown) {
                int yPos = getScrollY();
                return yPos == 0;
            } else if (!isPullDown) {
                int height = (int) Math.floor(getContentHeight() * getScale());
                //int webViewHeight = getHeight();
                int webViewHeight = getMeasuredHeight();
                int yPos = getScrollY();
                //Log.d("SpringWebView", "yPos " + yPos + " webViewHeight " + webViewHeight + " height " + height + " " + mAllowBottomGlow);

                if (yPos + webViewHeight >= height)
                    return true;
                else if (mAllowBottomGlow)
                    return true;
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

    /**
     * control pullGlow & ScrollBy in scroll nested
     * @param b:true work with pullToRefresh;
     *         b:false default mode
     */
    public void setOverScrollNested(boolean b) {
        mOverScrollNested = b;
    }

    float computeVelocity() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
        return -mVelocityTracker.getYVelocity(mScrollPointerId);
    }
}
