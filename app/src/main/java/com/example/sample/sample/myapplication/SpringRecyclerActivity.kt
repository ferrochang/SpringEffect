package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lib.effect.effect.widget.SpringRecyclerView
import com.example.sample.myapplication.R
import com.example.sample.sample.view.CustomAdapter

class SpringRecyclerActivity : Activity() {
    //protected val mDataset: Array<String?> = initDataset()
    //private var mLayoutManager: RecyclerView.LayoutManager? = null
    //val mAdapter = CustomAdapter(initDataset())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spring_recyclerview)
        var mRecyclerView : SpringRecyclerView = findViewById(R.id.recyclerView)
        //initDataset()
        //mAdapter = CustomAdapter(mDataset)

        //mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(LinearLayoutManager(this))
        mRecyclerView.setAdapter(CustomAdapter(Array<String?>(DATASET_COUNT) { "This is element $it" }))
        mRecyclerView.setHandleTouch(false)
    }

    companion object {
        private const val DATASET_COUNT = 12
    }
}