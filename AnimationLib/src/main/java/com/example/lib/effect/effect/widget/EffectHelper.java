package com.example.lib.effect.effect.widget;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EffectHelper {
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    private VelocityTracker mVelocityTracker;
    private int mScrollPointerId;
    private int mInitialTouchY;
    private int mLastTouchY;
    private int mScrollState = SCROLL_STATE_IDLE;
    private int mTouchSlop;
    private int[] mNestedOffsets;
    private float mLastX, mLastY;
    private AbsListView absListView;
    private ViewGroup viewGroup;
    private ISprintView sprintView;
    private SEdgeEffectFactory mEdgeEffectFactory;
    private EdgeEffect mTopGlow;
    private EdgeEffect mBottomGlow;
    int[] mScrollStepConsumed;
    private int[] mScrollOffset;
    boolean mOverScrollNested = false;

    private SpringRelativeLayout mSpringLayout = null;
    private boolean mGlowing = false;

    float mPullGrowTop = 0.1f;
    float mPullGrowBottom = 0.9f;

    private int mMaxFlingVelocity;
    int[] mScrollConsumed;
    int mLastChildBottom;

    private float mLastYVel = 0f;

    AbsListView.OnScrollListener mGivenOnScrollListener;
    OnScrollListenerWrapper mOnScrollListenerWrapper = new OnScrollListenerWrapper();

    public EffectHelper(ViewGroup vg) {
        viewGroup = vg;
        if (vg instanceof AbsListView)
            absListView = (AbsListView) vg;
        if (vg instanceof ISprintView)
            sprintView = (ISprintView) vg;

        ViewConfiguration vc = ViewConfiguration.get(vg.getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mScrollStepConsumed = new int[2];
        mScrollOffset = new int[2];
        mNestedOffsets = new int[2];
        mScrollConsumed = new int[2];
        setOnScrollListener(mOnScrollListenerWrapper);
        //mMinimumVelocity = vc.getScaledMinimumFlingVelocity();
    }
    public int onInterceptTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        //Log.d("SpringListView", "onInterceptTouchEvent  " + action);
        int index;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                index = ev.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    //return false;
                    return -1;
                }
                boolean checkPullup = (absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1);
                boolean readytoOs = isReadyToOverScroll(!checkPullup, 0);
                //Log.d("SpringListView", "onInterceptTouchEvent " + readytoOs + " mScrollState " + mScrollState);

                if (!readytoOs) {
                    return 0;
                    //return super.onInterceptTouchEvent(ev);
                }
                mInitialTouchY = mLastTouchY = (int)(ev.getY() + 0.5F);
                if (mScrollState == 2) {
                    viewGroup.getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                }

                mNestedOffsets[0] = mNestedOffsets[1] = 0;
                int nestedScrollAxis = 0;
                nestedScrollAxis |= 2;
                viewGroup.startNestedScroll(nestedScrollAxis);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.clear();
                viewGroup.stopNestedScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                index = ev.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    //return false;
                    return -1;
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
        return 0;
    }

    public int onTouchEvent(MotionEvent ev) {
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

        //Log.d("SpringListView", "onTouchEvent  " + action + " 1st " + getFirstVisiblePosition());
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int)(ev.getY() + 0.5F);
                int childcount = viewGroup.getChildCount();
                if (childcount > 0)
                    mLastChildBottom = viewGroup.getChildAt(childcount-1).getBottom();
                else
                    mLastChildBottom = 0;
                //if (mInitialTouchY > mLastChildBottom || mInitialTouchY < 0 || mInitialTouchY > getHeight())
                //    return false;

                nestedScrollAxis = 0;

                nestedScrollAxis |= 2;
                viewGroup.startNestedScroll(nestedScrollAxis);
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
                    //return false;
                    return -1;
                }

                int x = (int)(ev.getX(nestedScrollAxis) + 0.5F);
                int y = (int)(ev.getY(nestedScrollAxis) + 0.5F);
                int dy = mLastTouchY - y;

                //Log.d("SpringListView", "onTouchEvent ;  dy " + dy + " mScrollState " + mScrollState);

                if (viewGroup.dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffset)) {
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

                //mYdiff = dy;
                if (mScrollState == 1) {
                    mLastTouchY = y - mScrollOffset[1];
                    if (scrollByInternal(0, dy, vtev)) {
                        viewGroup.getParent().requestDisallowInterceptTouchEvent(true);
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
        //return super.onTouchEvent(ev);
        return 0;
    }

    private boolean isReadyToOverScroll(boolean isPullDown, int y) {
        final Adapter adapter = absListView.getAdapter();

        if ((null == adapter || adapter.isEmpty()) && (sprintView.SVgetFooterViewsCount() == 0 && sprintView.SVgetHeaderViewsCount() == 0)) {
            return false;
        } else {
            //View evaChild = getChildAt(0);
            //Log.d("SpringListView", " y " + y + " " + evaChild.getHeight() * (getFirstVisiblePosition()) + " top " + evaChild.getTop());
            if (isPullDown && absListView.getFirstVisiblePosition() == 0) {
                View firstVisibleChild =  absListView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= absListView.getListPaddingTop();
                }
            } else if (!isPullDown && null != adapter && absListView.getLastVisiblePosition() == adapter.getCount() - 1) {
                View lastVisibleChild = absListView.getChildAt(absListView.getChildCount() - 1);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= absListView.getHeight()
                            - absListView.getListPaddingBottom();
                }
            }
        }
        return false;
    }

    void setScrollState(int state) {
        if (state != mScrollState) {
            mScrollState = state;
            //if (state != 2) {
            //    stopScrollersInternal();
            //}
        }
    }

    private void cancelScroll() {
        resetTouch();
        setScrollState(SCROLL_STATE_IDLE);
        //mFirstChildTop = Integer.MAX_VALUE;
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
            viewGroup.postInvalidateOnAnimation();
        }
    }

    boolean scrollByInternal(int x, int y, MotionEvent ev) {
        boolean readyToGo = isReadyToOverScroll(y < 0, y);
        //Log.d("SpringListView", "scrollByInternal y " + y + "  1st visible pos " + getFirstVisiblePosition());
        if (!readyToGo)
            return false;

        int unconsumedX = 0;
        int unconsumedY = 0;
        int consumedX = 0;
        int consumedY = 0;
        //consumePendingUpdateOperations();
        if (absListView.getAdapter() != null) {
            scrollStep(x, y, mScrollStepConsumed);
            consumedX = mScrollStepConsumed[0];
            consumedY = mScrollStepConsumed[1];
            unconsumedX = x - consumedX;
            unconsumedY = y - consumedY;
        }

        //if (!this.mItemDecorations.isEmpty()) {
        viewGroup.invalidate();
        //}

        boolean nestedScrollresult = viewGroup.dispatchNestedScroll(consumedX, consumedY, unconsumedX, unconsumedY, mScrollOffset);
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
            if (viewGroup.getOverScrollMode() != 2) {
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

        if (!sprintView.SVawakenScrollBars()) {
            viewGroup.invalidate();
        }

        return consumedX != 0 || consumedY != 0;
    }

    private void onPointerUp(MotionEvent e) {
        int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == this.mScrollPointerId) {
            int newIndex = actionIndex == 0 ? 1 : 0;
            mScrollPointerId = e.getPointerId(newIndex);
            mInitialTouchY = this.mLastTouchY = (int)(e.getY(newIndex) + 0.5F);
        }

    }

    void scrollStep(int dx, int dy, @Nullable int[] consumed) {
        int consumedY = 0;
        if (consumed != null) {
            consumed[1] = consumedY;
        }

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
            viewGroup.postInvalidateOnAnimation();
        }

    }

    void dispatchOnScrolled(int hresult, int vresult) {
        int scrollX = viewGroup.getScrollX();
        int scrollY = viewGroup.getScrollY();
        sprintView.SVScrollChanged(scrollX, scrollY, scrollX, scrollY);
        //onScrolled(hresult, vresult);
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
    }

    private void pullGlows(float x, float overscrollX, float y, float overscrollY) {

        boolean invalidate = false;

        if (y > viewGroup.getHeight() || y < 0) {
            return;
        }

        float yRatio = y/viewGroup.getHeight();
        if (overscrollY < 0.0F && yRatio < mPullGrowBottom && yRatio > mPullGrowTop) {
            ensureTopGlow();
            if (mTopGlow != null) {
                mTopGlow.onPull(-overscrollY / (float) viewGroup.getHeight(), x / (float) viewGroup.getWidth());
                mGlowing = true;
                invalidate = true;
            }
        } else if (overscrollY > 0.0F && yRatio > mPullGrowTop && yRatio < mPullGrowBottom) {
            ensureBottomGlow();
            if (mBottomGlow != null) {
                mBottomGlow.onPull(overscrollY / (float) viewGroup.getHeight(), 1.0F - x / (float) viewGroup.getWidth());
                mGlowing = true;
                invalidate = true;
            }
        }

        if (invalidate || overscrollX != 0.0F || overscrollY != 0.0F) {
            viewGroup.postInvalidateOnAnimation();
        }
    }

    void ensureTopGlow() {
        if (mEdgeEffectFactory == null) {
            //throw new IllegalStateException("setEdgeEffectFactory first, please!");
            Log.e("SpringListView", "setEdgeEffectFactory first, please!");
            return;
        }

        if (mTopGlow == null) {
            mTopGlow = mEdgeEffectFactory.createEdgeEffect(viewGroup, 1);
            if (viewGroup.getClipToPadding()) {
                mTopGlow.setSize(viewGroup.getMeasuredWidth() - viewGroup.getPaddingLeft() - viewGroup.getPaddingRight(), viewGroup.getMeasuredHeight() - viewGroup.getPaddingTop() - viewGroup.getPaddingBottom());
            } else {
                mTopGlow.setSize(viewGroup.getMeasuredWidth(), viewGroup.getMeasuredHeight());
            }

        }
    }

    void ensureBottomGlow() {
        if (mEdgeEffectFactory == null) {
            //throw new IllegalStateException("setEdgeEffectFactory first, please!");
            Log.e("SpringListView", "setEdgeEffectFactory first, please!");
            return;
        }

        if (mBottomGlow == null) {
            mBottomGlow = mEdgeEffectFactory.createEdgeEffect(viewGroup, 3);
            if (viewGroup.getClipToPadding()) {
                mBottomGlow.setSize(viewGroup.getMeasuredWidth() - viewGroup.getPaddingLeft() - viewGroup.getPaddingRight(), viewGroup.getMeasuredHeight() - viewGroup.getPaddingTop() - viewGroup.getPaddingBottom());
            } else {
                mBottomGlow.setSize(viewGroup.getMeasuredWidth(), viewGroup.getMeasuredHeight());
            }

        }
    }

    float computeVelocity() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.computeCurrentVelocity(1000, (float)mMaxFlingVelocity);
        return -mVelocityTracker.getYVelocity(mScrollPointerId);
    }

    private void resetScroll() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
        viewGroup.stopNestedScroll();
        releaseGlows();
        //mScrollingDown = false;
        //mFirstChildTop = Integer.MAX_VALUE;
    }

    class OnScrollListenerWrapper implements AbsListView.OnScrollListener {
        int state = SCROLL_STATE_IDLE;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //mIsFlinging = scrollState == OnScrollListener.SCROLL_STATE_FLING;
            state = scrollState;
            if(mGivenOnScrollListener != null) {
                mGivenOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if(mGivenOnScrollListener != null) {
                mGivenOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }

            if (state == SCROLL_STATE_DRAGGING/* && mYdiff != 0*/) {

                if (mSpringLayout == null) {
                    ViewGroup vg = (ViewGroup) viewGroup.getParent();
                    if (vg instanceof SpringRelativeLayout) {
                        mSpringLayout = (SpringRelativeLayout) vg;
                    }
                }

                if (mSpringLayout != null) {
                    mSpringLayout.onRecyclerViewScrolled();
                }
            }

            //if (mSkipSpringAnimationOnce) {
            //    mSkipSpringAnimationOnce = false;
            //    return;
            //}

            if (state != SCROLL_STATE_SETTLING && state != SCROLL_STATE_DRAGGING) {
                return;
            }

            //Log.d("SpringListView", "onScroll " + canScrollVertically(-1) + " " + canScrollVertically(1));
            //EdgeEffect by fling
            if (!viewGroup.canScrollVertically(-1)) {
                if (!mGlowing) {
                    float yvel = mLastYVel;
                    if (yvel >= 0) {
                        yvel = computeVelocity();
                    }
                    //Log.d("SpringListView", "should go " + yvel);
                    pullGlows(mLastX, (float) 0, mLastY, yvel / 20);
                    //ensureTopGlow();
                    if (mTopGlow != null) {
                        mTopGlow.onAbsorb((int) (yvel / 20));
                    }
                }
            }

            if (!viewGroup.canScrollVertically(1)) {
                if (!mGlowing) {
                    //Log.d("SpringListView", "should go");
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

    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        if(isUserOnScrollListener(l))
            mGivenOnScrollListener = l;
        else
            sprintView.SVsetOnScrollListener(l);

    }

    boolean isUserOnScrollListener(AbsListView.OnScrollListener l) {
        return l != mOnScrollListenerWrapper;
    }

    public void setEdgeEffectFactory(@NonNull SEdgeEffectFactory edgeEffectFactory) {
        mEdgeEffectFactory = edgeEffectFactory;
        invalidateGlows();
    }

    void invalidateGlows() {
        mTopGlow = mBottomGlow = null;
    }

    public void setOverScrollNested(boolean b) {
        mOverScrollNested = b;
    }

}
