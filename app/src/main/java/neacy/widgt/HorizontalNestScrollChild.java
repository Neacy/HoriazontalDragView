package neacy.widgt;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * NestedScrollChild
 * Created by jayuchou on 17/2/16.
 */
public class HorizontalNestScrollChild extends TextView implements NestedScrollingChild {

    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private float mLastX;
    private final int[] offset = new int[2];
    private final int[] consumed = new int[2];

    public HorizontalNestScrollChild(Context context, AttributeSet attrs) {
        super(context, attrs);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingChildHelper.setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float nowX = event.getRawX();
                float dx = event.getRawX() - mLastX;
                mLastX = nowX;
                Log.i("Jayuchou", "=== dx === " + dx);
                if (startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL)
                        && dispatchNestedPreScroll((int) dx, 0, consumed, offset)) {// consumed中存放了parent消费了多少
//                    Log.i("Jayuchou", "=== consumed === " + consumed[0]);
//                    int remain = (int) (dx - consumed[0]);
//                    Log.i("Jayuchou", "=== remain === " + remain);
//                    if (remain != 0) {
//                        scrollBy(-remain, 0);
//                    }
                } else {
//                    scrollBy((int) -dx, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
