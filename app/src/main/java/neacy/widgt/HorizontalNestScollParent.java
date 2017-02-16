package neacy.widgt;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import neacy.horiazontaldragview.R;

/**
 * NestedScrollParent
 * Created by jayuchou on 17/2/16.
 */
public class HorizontalNestScollParent extends LinearLayout implements NestedScrollingParent {

    public HorizontalNestScollParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    private View mDeleteView;
    private View mContentView;
    private int mDeleteWidth;
    private int mToTalWidth;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDeleteView = findViewById(R.id.drag_action);
        mContentView = findViewById(R.id.drag_content);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDeleteWidth = mDeleteView.getMeasuredWidth();
        mToTalWidth = getMeasuredWidth();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return target instanceof NestedScrollingChild && nestedScrollAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.w("Jayuchou", "=== parent.onNestedScroll.target === " + target.toString());
        Log.w("Jayuchou", "=== parent.onNestedScroll.dxConsumed === " + dxConsumed);
        Log.w("Jayuchou", "=== parent.onNestedScroll.dxUnconsumed === " + dxUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.w("Jayuchou", "=== parent.onNestedPreScroll.dx === " + dx);
        Log.w("Jayuchou", "=== parent.onNestedPreScroll.consumed === " + consumed[0]);
        if (dx == 0) return;
        if (dx < 0) {// <-
            if (getScrollX() + Math.abs(dx) < mDeleteWidth) {
                scrollBy(-dx, 0);
                consumed[0] = dx;
            } else {
                scrollTo(mDeleteWidth, 0);
            }
        } else if (dx > 0) {// ->
            if (getScrollX() - Math.abs(dx) > 0) {
                scrollBy(-dx, 0);
                consumed[0] = dx;
            } else {
                scrollTo(0, 0);
            }
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }
}
