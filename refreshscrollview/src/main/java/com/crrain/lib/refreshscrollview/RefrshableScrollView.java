package com.crrain.lib.refreshscrollview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 有弹性的ScrollView 实现下拉和上拉，支持自定义的顶部和底部视图
 */
public class RefrshableScrollView extends ScrollView {

    private static final String TAG          = "RefrshableScrollView";

    // 移动因子, 是一个百分比(0.5), 比如手指移动了100px, 那么View就只移动50px
    // 目的是达到一个延迟的效果
    private static final float  MOVE_FACTOR  = 0.65f;

    // 松开手指后, 界面回到正常位置需要的动画时间
    private static final int    ANIM_TIME    = 300;

    // ScrollView的子View， 也是ScrollView的唯一一个子View
    private LinearLayout        contentView;

    // 手指按下时的Y值, 用于在移动时计算移动距离
    // 如果按下时不能上拉和下拉， 会在手指移动时更新为当前手指的Y值
    private float               startY;
    // 用于记录正常的布局位置
    private Rect                originalRect = new Rect();
    // 手指按下时记录是否可以继续下拉
    private boolean             canPullDown  = false;
    // 手指按下时记录是否可以继续上拉
    private boolean             canPullUp    = false;
    // 在手指滑动的过程中记录是否移动了布局
    private boolean             isMoved      = false;

    private TextView            tvFill;

    private HeaderRefreshView   headerRefreshView;
    private FooterRefreshView   footerRefreshView;

    /**
     * 设定顶部刷新视图
     * @param refreshView
     */
    public void setHeaderRefreshView(HeaderRefreshView refreshView) {
        if (headerRefreshView != null) {
            contentView.removeView(headerRefreshView);
        }
        headerRefreshView = refreshView;
        if (refreshView == null) {
            return;
        }
        headerRefreshView.setHeight(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.addView(headerRefreshView, 0, lp);
    }

    /**
     * 设定底部刷新视图
     * @param refreshView
     */
    public void setFooterRefreshView(FooterRefreshView refreshView) {
        if (footerRefreshView != null) {
            contentView.removeView(footerRefreshView);
        }
        this.footerRefreshView = refreshView;
        if (refreshView == null) {
            return;
        }
        footerRefreshView.setHeight(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.addView(footerRefreshView, lp);
    }

    public RefrshableScrollView(Context context) {
        super(context);
    }

    public RefrshableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentView = (LinearLayout) getChildAt(0);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvFill = new TextView(getContext());
            tvFill.setHeight(0);
            contentView.addView(tvFill, lp);
            addJuestFillView();
        }
    }

    //调整填充布局(用于页面内容不足时，高度上自动补满)
    private void addJuestFillView() {
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getHeight() == 0) {
                    addJuestFillView();
                } else {
                    tvFill.setHeight(getHeight() - contentView.getHeight());
                    postInvalidate();
                }
            }
        }, 100);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (contentView == null)
            return;

        // ScrollView中的唯一子控件的位置信息, 这个位置信息在整个控件的生命周期中保持不变
        originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(),
            contentView.getBottom());
    }

    /**
     * 在触摸事件中, 处理上拉和下拉的逻辑
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (contentView == null) {
            return super.dispatchTouchEvent(ev);
        }

        // 手指是否移动到了当前ScrollView控件之外
        boolean isTouchOutOfScrollView = ev.getY() >= this.getHeight() || ev.getY() <= 0;
        if (isTouchOutOfScrollView) { // 如果移动到了当前ScrollView控件之外
            if (isMoved) // 如果当前contentView已经被移动, 首先把布局移到原位置, 然后消费点这个事件
                boundBack();
            return true;
        }

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 判断是否可以上拉和下拉
                canPullDown = isMeetTop();
                canPullUp = isMeetBottom();

                // 记录按下时的Y值
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                boundBack();
                break;
            case MotionEvent.ACTION_MOVE:
                // 在移动的过程中， 既没有滚动到可以上拉的程度， 也没有滚动到可以下拉的程度
                if (!canPullDown && !canPullUp) {
                    startY = ev.getY();
                    canPullDown = isMeetTop();
                    canPullUp = isMeetBottom();
                    break;
                }

                // 计算手指移动的距离
                float nowY = ev.getY();
                int deltaY = (int) (nowY - startY);

                // 是否应该移动布局
                boolean shouldMove = (canPullDown && deltaY > 0) // 可以下拉， 并且手指向下移动
                                     || (canPullUp && deltaY < 0) // 可以上拉， 并且手指向上移动
                                     || (canPullUp && canPullDown); // 既可以上拉也可以下拉（这种情况出现在ScrollView包裹的控件比ScrollView还小）

                if (shouldMove) {
                    // 计算偏移量
                    int offset = (int) (deltaY * MOVE_FACTOR);
                    // 随着手指的移动而移动布局
                    if (isMeetBottom() && isMeetTop()) {
                        if (headerRefreshView != null && headerRefreshView.getHeight() == 0
                            && offset < 0) {
                            doBottomRefresh(offset);
                        } else {
                            doTopRefresh(offset);
                        }
                    } else if (isMeetBottom()) {
                        doBottomRefresh(offset);
                    } else if (isMeetTop()) {
                        doTopRefresh(offset);
                    }

                    isMoved = true; // 记录移动了布局
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 顶部刷新的处理
     * @param offset
     */
    private void doTopRefresh(int offset) {
        if (headerRefreshView == null) {
            return;
        }
        if (headerRefreshView.getHeight() == 0) {
            headerRefreshView.pullStart();
        }
        headerRefreshView.setHeight(offset);

        if (footerRefreshView != null) {
            footerRefreshView.setHeight(0);
        }

        headerRefreshView.onMove(offset);
    }

    /**
     * 底部刷新的处理
     * @param offset
     */
    private void doBottomRefresh(int offset) {
        if (footerRefreshView == null) {
            return;
        }
        if (footerRefreshView.getHeight() == 0) {
            footerRefreshView.pullStart();
        }
        footerRefreshView.setHeight(-offset);

        if (headerRefreshView != null) {
            headerRefreshView.setHeight(0);
        }

        footerRefreshView.onMove(-offset);
    }

    /**
     * 将内容布局移动到原位置 可以在UP事件中调用, 也可以在其他需要的地方调用, 如手指移动到当前ScrollView外时
     */
    private void boundBack() {
        if (!isMoved) {
            return; // 如果没有移动布局， 则跳过执行
        }

        resetView(-1);
    }

    /**
     * 重置View
     * @param leftHeight    重置后的保留高度，-1则自动计算内容高度并且作为保留高度（即包裹）
     */
    private void resetView(int leftHeight) {
        final RefreshLayout targetView;
        if (footerRefreshView != null && footerRefreshView.getHeight() != 0) {
            targetView = footerRefreshView;
        } else {
            targetView = headerRefreshView;
        }
        if (targetView == null) {
            return;
        }

        int contentHeight = 0;
        if (leftHeight < 0) {
            if (targetView.getChildCount() > 0) {
                contentHeight = targetView.getChildAt(0).getHeight();
            }
        } else {
            contentHeight = leftHeight;
        }

        //内容尚未完全划出
        if (contentHeight > targetView.getHeight()) {
            contentHeight = 0;
        }

        ObjectAnimator valueAnimator = ObjectAnimator.ofInt(targetView, "height", contentHeight);
        valueAnimator.setDuration(ANIM_TIME);

        //如果变化后保留高度为0，则认为全部关闭，重置页面，不走刷新逻辑
        if (contentHeight == 0) {
            targetView.reset();
        } else {
            targetView.onRelease();
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (targetView instanceof FooterRefreshView) {
                        ((FooterRefreshView) targetView).onLoadMore(RefrshableScrollView.this);
                    } else if (targetView instanceof HeaderRefreshView) {
                        ((HeaderRefreshView) targetView).onRefresh(RefrshableScrollView.this);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

        valueAnimator.start();

        // 设置回到正常的布局位置
        contentView.layout(originalRect.left, originalRect.top, originalRect.right,
            originalRect.bottom);
        if (contentHeight == 0) {
            //将标志位设回false
            canPullDown = false;
            canPullUp = false;
            isMoved = false;
        }
    }

    /**
     * 结束刷新
     */
    public void finishRefresh() {
        resetView(0);
    }

    /**
     * 判断是否滚动到顶部
     */
    private boolean isMeetTop() {
        if (headerRefreshView == null) {
            return false;
        }
        if (headerRefreshView.getHeight() != 0) {
            headerRefreshView.setHeight(headerRefreshView.getHeight() - getScrollY());
            scrollTo(0, 0);
            return getScrollY() <= headerRefreshView.getHeight();
        } else {
            return getScrollY() == 0;
        }
    }

    /**
     * 获得内容的高度
     * @return
     */
    private int getContentHeight() {
        return contentView.getHeight()
               - (footerRefreshView == null ? 0 : footerRefreshView.getHeight())
               - (headerRefreshView == null ? 0 : headerRefreshView.getHeight());
    }

    /**
     * 判断是否滚动到底部
     */
    private boolean isMeetBottom() {
        if (footerRefreshView == null) {
            return false;
        }
        if (footerRefreshView.getHeight() != 0) {
            scrollTo(0, getHeight() + getScrollY());
            return true;
        } else {
            return getContentHeight() <= getHeight() + getScrollY();
        }
    }
}