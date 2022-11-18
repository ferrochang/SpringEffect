package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lib.effect.effect.widget.SpringScrollView2;
import com.example.sample.myapplication.R;


/**
 * PulltoRefresh without SpringRelativeLayout
 */
public class RefreshScrollViewActivity extends Activity {
    SwipeRefreshLayout mRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_scrollview);

        SpringScrollView2 scrollView = findViewById(R.id.scrollview);
        scrollView.setEdgeEffectColor(0x00D6D9D6);
        mRefreshLayout = findViewById(R.id.swipe_container);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                Log.d("RefreshScrollViewActivity", "onRefresh");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 300);
            }
        });

        Button button = findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RefreshScrollViewActivity", "onClick");
            }
        });
    }
}
