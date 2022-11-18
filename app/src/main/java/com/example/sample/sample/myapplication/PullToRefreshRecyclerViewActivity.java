package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sample.sample.view.CustomAdapter;
import com.example.lib.effect.effect.widget.SpringRecyclerView;
import com.example.lib.effect.effect.widget.SpringRefreshLayout;
import com.example.sample.myapplication.R;


public class PullToRefreshRecyclerViewActivity extends Activity {
    SpringRefreshLayout mRefreshLayout;
    //SwipeRefreshLayout mRefreshLayout;
    SpringRecyclerView mRecyclerView;
    protected String[] mDataset;
    private static final int DATASET_COUNT = 16;
    private LinearLayoutManager mLayoutManager;

    protected CustomAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_refresh_recyclerview);
        initDataset();
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new CustomAdapter(mDataset);
        //mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        //mSpringLayout.addSpringView(R.id.recyclerView);
        mRecyclerView = (SpringRecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setEdgeEffectFactory(mSpringLayout.createEdgeEffectFactory());

        mRefreshLayout = findViewById(R.id.swipe_container);
        mRefreshLayout.setOverScrollChild(mRecyclerView);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 300);
            }
        });

    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }
}
