package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample.sample.view.CustomAdapter;
import com.example.lib.effect.effect.widget.SpringRecyclerView;
import com.example.sample.myapplication.R;


public class SpringRecyclerActivity extends Activity {
    private static final int DATASET_COUNT = 12;
    protected String[] mDataset;
    protected CustomAdapter mAdapter;
    private SpringRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spring_recyclerview);
        mRecyclerView = findViewById(R.id.recyclerView);
        initDataset();
        mAdapter = new CustomAdapter(mDataset);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHandleTouch(false);
    }

    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }
}
