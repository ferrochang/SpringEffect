package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample.sample.view.CustomAdapter;
import com.example.lib.effect.effect.widget.SpringRecyclerView;
import com.example.sample.myapplication.R;


public class HorizontalRecyclerViewActivity extends Activity {

    private static final int DATASET_COUNT = 15;

    //private SpringRelativeLayout mSpringLayout;
    //private RecyclerView mRecyclerView;
    private SpringRecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;
    protected CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.horizontal_recycler_view);
        initDataset();

        //mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        //mSpringLayout.addSpringView(R.id.recyclerView);
        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CustomAdapter(mDataset);
        mAdapter.setHorizontal();
        mRecyclerView.setHorizontalOverScroll();
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setEdgeEffectFactory(mSpringLayout.createEdgeEffectFactory(true));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int state = RecyclerView.SCROLL_STATE_IDLE;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //super.onScrollStateChanged(recyclerView, newState);
                state = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING && dx != 0) {
                    //mSpringLayout.onRecyclerViewScrolled();
                }

                //super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }
}
