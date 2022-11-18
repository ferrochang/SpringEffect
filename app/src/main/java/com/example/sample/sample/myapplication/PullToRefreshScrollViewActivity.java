package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.lib.effect.effect.widget.SpringScrollView;
import com.example.sample.myapplication.R;


public class PullToRefreshScrollViewActivity extends Activity {
    SpringRelativeLayout mSpringLayout;
    SwipeRefreshLayout mRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_to_refresh_scrollview);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);

        mSpringLayout.addSpringView(R.id.scrollview);
        SpringScrollView scrollView = (SpringScrollView) findViewById(R.id.scrollview);
        scrollView.setEdgeEffectFactory(mSpringLayout.createViewEdgeEffectFactory());
        scrollView.setOverScrollNested(true);
        scrollView.setNestedScrollingEnabled(true);
        scrollView.setEdgeEffectColor(0xffffff);


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                Log.d("SpringScrollView", "onRefresh");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 300);
            }
        });
    }
}
