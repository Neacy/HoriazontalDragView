package neacy.horiazontaldragview;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * an Horizontal Scroll View like IOS.QQ scroll to choice delete.
 * Created by jayuchou on 15/7/14.
 */
public class HorizontalDragLayout extends LinearLayout {

    private static final String TAG = "HorizontalDragLayout";

    private View mContentView;
    private View mDeleteView;
    private ViewDragHelper mDragHlper;

    private Point point;

    public HorizontalDragLayout(Context context) {
        super(context);
    }

    public HorizontalDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDragHlper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
        point = new Point();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        point.x = mContentView.getLeft();
        point.y = mContentView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = findViewById(R.id.drag_content);
        mDeleteView = findViewById(R.id.drag_action);
    }

    /**--- ViewDragHelper.CallBack for MotionEvent ---*/
    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int i) {
            return view == mContentView || view == mDeleteView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.w(TAG, "clampViewPositionHorizontal = " + left + "/" + dx);
            int realLeft = left;
            if (child == mContentView) {
                if (left > 0) {// right scroll
                    realLeft = left > mDeleteView.getWidth() ? mDeleteView.getWidth() : left;
                } else if (left < 0) {// left scroll
                    realLeft = Math.abs(left) > mDeleteView.getWidth() ? -mDeleteView.getWidth() : left;
                }
            }
            return realLeft;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.w(TAG, "--- scroll finish when finger up ---");
            Log.w(TAG, "onViewReleased = " + xvel + "/" + yvel);
            if (releasedChild == mContentView) {
                if (xvel > 0) {
                    mDragHlper.settleCapturedViewAt(point.x, point.y);
                } else {
                    mDragHlper.settleCapturedViewAt(point.x - mDeleteView.getWidth(), point.y);
                }
                invalidate();
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.w(TAG, "--- when view position changed = " + left + "/" + dx);
            if (changedView == mContentView) {
                mDeleteView.offsetLeftAndRight(dx);
            } else if (changedView == mDeleteView) {
                mContentView.offsetLeftAndRight(dx);
            }
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return false;
        }
        return mDragHlper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHlper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll()
    {
        if(mDragHlper.continueSettling(true))
        {
            invalidate();
        }
    }
}
