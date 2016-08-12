package com.crrain.lib.refreshscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/8/10.
 */
public abstract class RefreshLayout extends ViewGroup {

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * LayoutParams of RefreshLayout
     */
    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        if (childCount > 0) {
            int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            View view = getChildAt(0);
            measureChildWithMargins(view, widthMeasureSpec, 0, childHeightSpec, 0);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    private void layoutChildren() {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddintBottom = getPaddingBottom();

        int childCount = getChildCount();

        if (childCount > 0) {
            View child = getChildAt(0);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            MarginLayoutParams marginLp = (MarginLayoutParams) child.getLayoutParams();

            int childLeft = paddingLeft + marginLp.leftMargin;
            int childTop = paddingTop + marginLp.topMargin - (childHeight - height);
            int childRight = childLeft + childWidth;
            int childBottom = childTop + childHeight;

            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    /**
     * 设定高度
     * @param pixels
     */
    public void setHeight(int pixels) {
        setMinimumHeight(pixels);
        invalidate();
    }

    /**
     * 滑动开始
     */
    public abstract void pullStart();

    /**
     * 拖动中
     * @param move  滑动量
     */
    public abstract void onMove(int move);

    /**
     * 当手放开
     */
    public abstract void onRelease();

    /**
     * 控件重置
     */
    public abstract void reset();
}