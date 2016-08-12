package com.crrain.refreshablescrollview;

import android.app.Activity;
import android.os.Bundle;

import com.crrain.lib.refreshscrollview.RefrshableScrollView;
import com.crrain.refreshablescrollview.views.MyFooterRefreshView;
import com.crrain.refreshablescrollview.views.MyHeaderRefreshView;

public class MainActivity extends Activity {

    private RefrshableScrollView myScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myScrollView = (RefrshableScrollView) findViewById(R.id.myScrollView);

        myScrollView.setHeaderRefreshView(new MyHeaderRefreshView(this));
        myScrollView.setFooterRefreshView(new MyFooterRefreshView(this));
    }
}
