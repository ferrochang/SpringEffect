package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.effect.effect.widget.SpringRecyclerView
import com.example.sample.myapplication.R
import com.example.sample.sample.view.CustomAdapter

class HorizontalRecyclerViewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.horizontal_recycler_view)

        //mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        //mSpringLayout.addSpringView(R.id.recyclerView);
        val mRecyclerView = findViewById<View>(R.id.recyclerView) as SpringRecyclerView
        //var mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val mAdapter = CustomAdapter(Array<String?>(DATASET_COUNT) { "This is element #$it" })
        mAdapter.setHorizontal()
        mRecyclerView.setHorizontalOverScroll()
        mRecyclerView.adapter = mAdapter
        //mRecyclerView.setEdgeEffectFactory(mSpringLayout.createEdgeEffectFactory(true));
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var state = RecyclerView.SCROLL_STATE_IDLE
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //super.onScrollStateChanged(recyclerView, newState);
                state = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING && dx != 0) {
                    //mSpringLayout.onRecyclerViewScrolled();
                }

                //super.onScrolled(recyclerView, dx, dy);
            }
        })
    }

    companion object {
        private const val DATASET_COUNT = 15
    }
}