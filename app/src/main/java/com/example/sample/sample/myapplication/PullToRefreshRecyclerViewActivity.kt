package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.lib.effect.effect.widget.SpringRecyclerView
import com.example.lib.effect.effect.widget.SpringRefreshLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.CustomAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PullToRefreshRecyclerViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_refresh_recyclerview)
        //val mLayoutManager = LinearLayoutManager(this)
        //mAdapter = CustomAdapter(mDataset)
        //mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        //mSpringLayout.addSpringView(R.id.recyclerView);
        val mRecyclerView = findViewById<View>(R.id.recyclerView) as SpringRecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = CustomAdapter(Array<String?>(DATASET_COUNT) {"This is element #$it"})
        //mRecyclerView.setEdgeEffectFactory(mSpringLayout.createEdgeEffectFactory());
        val mRefreshLayout = findViewById<View>(R.id.swipe_container) as SpringRefreshLayout
        mRefreshLayout.setOverScrollChild(mRecyclerView)
        mRefreshLayout.setOnRefreshListener(OnRefreshListener {
            mRefreshLayout.isRefreshing = true

            lifecycleScope.launch {
                delay(1000)
                mRefreshLayout.isRefreshing = false
            }
            /*
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                mRefreshLayout.isRefreshing = false
            }
             */
            //Handler().postDelayed({ mRefreshLayout.setRefreshing(false) }, 300)
        })
    }

    companion object {
        private const val DATASET_COUNT = 16
    }
}