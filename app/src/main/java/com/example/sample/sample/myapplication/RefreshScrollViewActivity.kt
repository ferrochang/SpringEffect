package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.lib.effect.effect.widget.SpringScrollView2
import com.example.sample.myapplication.R

/**
 * PulltoRefresh without SpringRelativeLayout
 */
class RefreshScrollViewActivity : Activity() {
    //var mRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refresh_scrollview)
        val scrollView = findViewById<SpringScrollView2>(R.id.scrollview)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.setEdgeEffectColor(0x00D6D9D6)
        }
        val mRefreshLayout : SwipeRefreshLayout = findViewById(R.id.swipe_container)
        /*
        mRefreshLayout.setOnRefreshListener(OnRefreshListener {
            mRefreshLayout.setRefreshing(true)
            Log.d("RefreshScrollViewActivity", "onRefresh")
            Handler().postDelayed({ mRefreshLayout.setRefreshing(false) }, 300)
        })

         */
        val button = findViewById<Button>(R.id.textView)
        button.setOnClickListener { Log.d("RefreshScrollViewActivity", "onClick") }
    }
}