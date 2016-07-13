package neacy.widgt;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 模仿QQ滑动提示删除,采用NestedScrolling实现.
 * Created by jayuchou on 16/7/8.
 */
public class HorizontalNestedScrollLayout extends BaseDragLayout implements NestedScrollingParent, NestedScrollingChild {

    private static final String TAG = "HorizontalNestedScrollLayout";

    public HorizontalNestedScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setNestedScrollingEnabled(true);
    }

    private NestedScrollingParentHelper mParentHelper;


    private NestedScrollingChildHelper mChildHelper;
    private int mTouchSlop;
    private float mLastX;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL);
                break;
            case MotionEvent.ACTION_MOVE:
                final float moveX = event.getX();
                float offsetX = moveX - mLastX;
                if (Math.abs(offsetX) < mTouchSlop) {
                    return super.onInterceptTouchEvent(event);
                }
                boolean isScroll = dispatchNestedPreScroll((int) offsetX, 0, mScrollConsumed, mScrollOffset);
                Log.w(TAG, "-- dispatchNestedPreScroll = " + isScroll);
                if (isScroll) {
                    offsetX -= mScrollConsumed[0];
                }
                if (getScrollX() >= 0 && getScrollX() <= mDragMaxWidth) {
                    offsetLeftAndRight((int) offsetX);
                }
                return true;
            case MotionEvent.ACTION_UP:
                stopNestedScroll();
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    // NestedScrollingChild
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        mParentHelper.onStopNestedScroll(child);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.i(TAG, "dx -- " + dx);
        Log.i(TAG, "consumed[0] -- " + consumed[0]);
        Log.i(TAG, "consumed[1] -- " + consumed[1]);
        if (dx > 0) {// 向左滑动
            if (getScrollX() < mDragMaxWidth) {
                dx = getScrollX();
                offsetLeftAndRight(dx);
                consumed[0] += dx;
            }
        } else {// 向右滑动
            if (getScrollX() > 0) {
                dx = getScrollX() - dx;
                offsetLeftAndRight(dx);
                consumed[0] += dx;
            }
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }
}
