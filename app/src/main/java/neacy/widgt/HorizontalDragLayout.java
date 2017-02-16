package neacy.widgt;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 模仿QQ滑动提示删除,采用ViewDragHelper实现.
 * 参考博客:
 * 1.http://wuxiaolong.me/2015/12/04/ViewDragHelper/
 * 2.http://blog.csdn.net/lzyzsd/article/details/41492783
 * Created by jayuchou on 15/7/14.
 */
public class HorizontalDragLayout extends BaseDragLayout {

    private static final String TAG = "HorizontalDragLayout";

    private ViewDragHelper mDragHlper;
    private int minVelocity;// 最小滑动速度
    private int draggedX;// 当前滑动的位置

    public HorizontalDragLayout(Context context) {
        super(context);
    }

    public HorizontalDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        minVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mDragHlper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
    }

    /**
     * ViewDragHelper滑动回调
     */
    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {// 是否触发手势拦截的可能
            return child == mContentView || child == mDeleteView;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // 根据滑动的速度和滑动的距离来显示菜单按钮<手指放开的那一瞬间>
            super.onViewReleased(releasedChild, xvel, yvel);
            boolean isScroll = false;
            if (xvel > minVelocity) {// 向右滑动
                isScroll = false;
            } else if (xvel < -minVelocity) {// 向左滑动
                isScroll = true;
            } else if (draggedX <= -mDragMaxWidth / 2) {// 向左滑动
                isScroll = true;
            } else if (draggedX > -mDragMaxWidth / 2) {// 向右滑动
                isScroll = false;
            }

            Log.i(TAG, "-- draggedX == " + draggedX);
            Log.i(TAG, "-- xvel == " + xvel);
            Log.i(TAG, "-- minVelocity == " + minVelocity);

            final int offset = isScroll ? -mDragMaxWidth : 0;
            mDragHlper.smoothSlideViewTo(mContentView, offset, 0);
            ViewCompat.postInvalidateOnAnimation(HorizontalDragLayout.this);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 限制水平滑动
            draggedX = left;
            final int leftPadding = getPaddingLeft();
            final int rightPadding = getPaddingRight();
            if (child == mContentView) {// 这里面的left为负值
                final int maxLeft = -leftPadding - mDragMaxWidth;
                return Math.min(Math.max(maxLeft, left), 0);
            } else if (child == mDeleteView) {
                final int minLeft = leftPadding + mContentView.getMeasuredWidth() - mDragMaxWidth;
                final int maxLeft = leftPadding + rightPadding + mContentView.getMeasuredWidth();
                return Math.min(Math.max(left, minLeft), maxLeft);
            }
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // 如果View的位置发生改变那么相对应的View要移动
            if (changedView == mContentView) {
                mDeleteView.offsetLeftAndRight(dx);
            } else if (changedView == mDeleteView) {
                mContentView.offsetLeftAndRight(dx);
            }
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {// 如果当子view设置了onClick时间的时候需要重写这个方法
            return mDragMaxWidth;// getViewHorizontalDragRange返回的是水平滑动的范围
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragHlper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHlper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHlper.continueSettling(true)) {
            invalidate();
        }
    }
}
