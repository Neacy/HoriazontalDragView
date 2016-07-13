package neacy.widgt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import neacy.horiazontaldragview.R;

/**
 * ViewGroup布局和测绘,我们暂时只提供两个View供操作.
 * Created by jayuchou on 16/7/6.
 */
public class BaseDragLayout extends ViewGroup {

    public BaseDragLayout(Context context) {
        super(context);
    }

    public BaseDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int mDragMaxWidth;// 滑动的最大距离等于右边出现的控件宽度

    public View mContentView;// 内容view
    public View mDeleteView;// 菜单view

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int width = 0;
        int height = 0;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            width += view.getMeasuredWidth();
            height = Math.max(height, view.getMeasuredHeight());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int left = 0;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            view.layout(left, 0, left + view.getMeasuredWidth(), view.getMeasuredHeight());
            left += view.getMeasuredWidth();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 暂时没有考虑那么多
        mContentView = findViewById(R.id.drag_content);
        mDeleteView = findViewById(R.id.drag_action);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDragMaxWidth = getChildAt(1).getMeasuredWidth();// 滑动的最大距离
    }
}
