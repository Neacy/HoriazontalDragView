package neacy.widgt;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * 模仿QQ滑动提示删除,采用Scroller实现.
 * Created by jayuchou on 16/7/5.
 */
public class HorizontalScrollerLayout extends BaseDragLayout {

    private static final String TAG = "HorizontalDragLayout";

    public HorizontalScrollerLayout(Context context) {
        super(context);
        init(context);
    }

    public HorizontalScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;// 最小滑动距离.
    private int mMinVelocity;// 最小滑动速度.
    private int mLastX;// 上一次手势的位置.
    private int mLastDownX;// 记录上一次Down的位置

    private void init(Context context) {
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMinVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        Log.i(TAG, "mTouchSlop == " + mTouchSlop);
        Log.i(TAG, "mMinVelocity == " + mMinVelocity);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onInterceptTouchEvent - Down");
                mLastX = (int) ev.getX();
                mLastDownX = mLastX;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                Log.i(TAG, "onInterceptTouchEvent mDragMaxWidth == " + mDragMaxWidth);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onInterceptTouchEvent - MOVE");
                int offsetX = (int) ev.getX() - mLastX;
                Log.i(TAG, "onInterceptTouchEvent offsetX == " + offsetX);
                Log.i(TAG, "onInterceptTouchEvent getScrollX == " + getScrollX());
                if (Math.abs(offsetX) >= mTouchSlop) {// 手势满足滑动的最小距离.
                    return getScrollX() >= 0 && getScrollX() <= mDragMaxWidth;
                }
//                mLastX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onInterceptTouchEvent - up");
                mLastDownX = 0;
                mLastX = 0;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent - Down");
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = (int) event.getX();
                mLastDownX = mLastX;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent - move");
                int offsetX = (int) (event.getX() - mLastX);
                if (Math.abs(offsetX) >= mTouchSlop) {
                    Log.w(TAG, "onTouchEvent getScrollX = " + getScrollX());
                    if (offsetX < 0) {//右->左
                        if (getScrollX() + Math.abs(offsetX) > mDragMaxWidth) {
                            scrollTo(mDragMaxWidth, 0);
                            return true;
                        }
                    } else {// 左->右
                        if (getScrollX() - offsetX < 0) {
                            scrollTo(0, 0);
                            return true;
                        }
                    }
                    scrollBy(-offsetX, 0);
                }
                mLastX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent - up");
                mVelocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                Log.d(TAG, "velocityX === " + velocityX);
                if (Math.abs(velocityX) >= mMinVelocity) {// 如果滑动速度够快的时候
                    if (velocityX < 0) {// 从右到左滑动.
                        startScroll(getScrollX(), mDragMaxWidth - getScrollX());
                    } else if (velocityX > 0) {// 从左到右滑动.
                        startScroll(getScrollX(), -getScrollX());
                    }
                } else {// 如果滑动太慢抬手的时候进行默认滑动
                    int offsetUpX = (int) (event.getX() - mLastDownX);
                    if (offsetUpX < 0) {//右->左
                        startScroll(getScrollX(), mDragMaxWidth - getScrollX());
                    } else {// 左->右
                        startScroll(getScrollX(), -getScrollX());
                    }
                }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mLastDownX = 0;
                mLastX = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startScroll(int starX, int endX) {
        mScroller.startScroll(starX, 0, endX, 0, 400);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
