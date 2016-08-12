package com.crrain.refreshablescrollview.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crrain.lib.refreshscrollview.HeaderRefreshView;
import com.crrain.lib.refreshscrollview.RefreshableScrollView;
import com.crrain.refreshablescrollview.R;

/**
 * Created by Administrator on 2016/8/10.
 */
public class MyHeaderRefreshView extends HeaderRefreshView {
    private View      contentView;
    private ImageView ivArrow, ivBatMan, ivSuperMan;
    private TextView  tv_tips;
    private int       mHeight;

    private int       screenWidth, screenHeight;

    private int       maxOffset;

    private String    refreshType = "";

    public MyHeaderRefreshView(Context context) {
        super(context);
        initView();
    }

    public MyHeaderRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyHeaderRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh_header_view,
            null, false);
        ivArrow = (ImageView) contentView.findViewById(R.id.iv_arrow);
        ivBatMan = (ImageView) contentView.findViewById(R.id.ivBatMan);
        ivSuperMan = (ImageView) contentView.findViewById(R.id.ivSuperMan);

        tv_tips = (TextView) contentView.findViewById(R.id.tv_tips);

        addView(contentView);
    }

    /**
     * 开始下拉刷新
     * @param scrollView
     */
    @Override
    public void onRefresh(final RefreshableScrollView scrollView) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.finishRefresh();
                if ("1".equals(refreshType)) {
                    Toast.makeText(getContext(), "刷新逻辑", Toast.LENGTH_LONG).show();
                } else if ("2".equals(refreshType)) {
                    Toast.makeText(getContext(), "执行神秘操作逻辑", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "未知逻辑，refreshType=" + refreshType,
                        Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);
    }

    @Override
    public void pullStart() {
        mHeight = contentView.getHeight();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        maxOffset = screenWidth - ivSuperMan.getMeasuredWidth() - ivBatMan.getMeasuredWidth();
    }

    @Override
    public void onMove(int move) {
        ivArrow.setRotationY(move / (float) mHeight * 360);
        float moveOffset = move * .7f;
        ivBatMan.setTranslationX(moveOffset > maxOffset ? maxOffset : moveOffset);

        if (move < mHeight) {
            tv_tips.setText("继续下拉有惊喜~");
            refreshType = "0";
        } else if (move < mHeight * 1.5) {
            tv_tips.setText("松开即可刷新~");
            refreshType = "1";
        } else if (move > mHeight * 1.5) {
            tv_tips.setText("松开立即执行神秘操作~");
            refreshType = "2";
        }
    }

    @Override
    public void onRelease() {
        ivArrow.setRotationY(0);
    }

    @Override
    public void reset() {
        ivArrow.setRotationY(0);
    }
}
