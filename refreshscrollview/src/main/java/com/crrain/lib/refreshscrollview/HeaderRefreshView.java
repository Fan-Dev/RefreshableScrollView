package com.crrain.lib.refreshscrollview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/8/10.
 */
public abstract class HeaderRefreshView extends RefreshLayout {
    public HeaderRefreshView(Context context) {
        super(context);
    }

    public HeaderRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开始下拉刷新
     * @param scrollView
     */
    public abstract void onRefresh(RefreshableScrollView scrollView);
}
