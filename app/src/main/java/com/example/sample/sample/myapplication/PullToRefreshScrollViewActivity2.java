package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lib.effect.effect.widget.SpringRefreshLayout;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.lib.effect.effect.widget.SpringScrollView;
import com.example.sample.myapplication.R;


public class PullToRefreshScrollViewActivity2 extends Activity {
    SpringRelativeLayout mSpringLayout;
    SpringRefreshLayout mRefreshLayout;
    SpringScrollView mScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_to_refresh_scrollview2);

        mRefreshLayout = (SpringRefreshLayout) findViewById(R.id.swipe_container);
        mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        mScrollView = findViewById(R.id.scrollview);

        mSpringLayout.addSpringView(R.id.scrollview);
        SpringScrollView scrollView = (SpringScrollView) findViewById(R.id.scrollview);
        scrollView.setEdgeEffectFactory(mSpringLayout.createViewEdgeEffectFactory());
        //scrollView.setOverScrollNested(true);
        mRefreshLayout.setOverScrollChild(scrollView);
        scrollView.setEdgeEffectColor(0x00D6D9D6);


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

        mScrollView.setScrollingChangeListener(new SpringScrollView.scrollingChangeListener() {
            @Override
            public int getDir() {
                return -1;//listening edge top;
            }

            @Override
            public void onAllowed(int i) {
                mRefreshLayout.setEnabled(false);
            }

            @Override
            public void onStop(int i) {
                mRefreshLayout.setEnabled(true);
            }
        });
    }
}
