package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.lib.effect.effect.widget.SpringRecyclerView
import com.example.lib.effect.effect.widget.SpringRefreshLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.CustomAdapter

class PullToRefreshRecyclerViewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_refresh_recyclerview)
        var mLayoutManager = LinearLayoutManager(this)
        //mAdapter = CustomAdapter(mDataset)
        //mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        //mSpringLayout.addSpringView(R.id.recyclerView);
        var mRecyclerView = findViewById<View>(R.id.recyclerView) as SpringRecyclerView
        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.adapter = CustomAdapter(Array<String?>(DATASET_COUNT) {"This is element #$it"})
        //mRecyclerView.setEdgeEffectFactory(mSpringLayout.createEdgeEffectFactory());
        var mRefreshLayout = findViewById<View>(R.id.swipe_container) as SpringRefreshLayout
        mRefreshLayout.setOverScrollChild(mRecyclerView)
        mRefreshLayout.setOnRefreshListener(OnRefreshListener {
            mRefreshLayout.setRefreshing(true)
            Handler().postDelayed({ mRefreshLayout.setRefreshing(false) }, 300)
        })
    }

    companion object {
        private const val DATASET_COUNT = 16
    }
}