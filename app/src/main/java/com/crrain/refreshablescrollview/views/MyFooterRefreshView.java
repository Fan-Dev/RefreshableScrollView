package com.crrain.refreshablescrollview.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crrain.lib.refreshscrollview.FooterRefreshView;
import com.crrain.lib.refreshscrollview.RefrshableScrollView;
import com.crrain.refreshablescrollview.R;

/**
 * Created by Administrator on 2016/8/10.
 */
public class MyFooterRefreshView extends FooterRefreshView {

    private View     contentView;
    private TextView tv_tips;

    public MyFooterRefreshView(Context context) {
        super(context);
        initView();
    }

    public MyFooterRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyFooterRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh_footer_view,
            null, false);
        tv_tips = (TextView) contentView.findViewById(R.id.tv_tips);

        addView(contentView);
    }

    /**
     *开始上拉加载更多
     * @param scrollView
     */
    public void onLoadMore(final RefrshableScrollView scrollView) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.finishRefresh();
                Toast.makeText(getContext(), "加载更多服务", Toast.LENGTH_LONG).show();
            }
        }, 1000);
    }

    @Override
    public void pullStart() {
        Log.d("222", "FooterRefreshView->pullStart");
    }

    @Override
    public void onMove(int move) {
        Log.d("222", "FooterRefreshView->onMove=" + move);
    }

    @Override
    public void onRelease() {
        Log.d("222", "FooterRefreshView->onRelease");
    }

    @Override
    public void reset() {
        Log.d("222", "FooterRefreshView->reset");
    }
}
