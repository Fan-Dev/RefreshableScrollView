package com.crrain.lib.refreshscrollview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/8/10.
 */
public abstract class FooterRefreshView extends RefreshLayout {

    public FooterRefreshView(Context context) {
        super(context);
    }

    public FooterRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FooterRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     *开始上拉加载更多
     * @param scrollView
     */
    public abstract void onLoadMore(RefrshableScrollView scrollView);
}
