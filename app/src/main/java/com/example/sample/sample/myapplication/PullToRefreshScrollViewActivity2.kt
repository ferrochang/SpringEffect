package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.lib.effect.effect.widget.SpringRefreshLayout
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.lib.effect.effect.widget.SpringScrollView
import com.example.lib.effect.effect.widget.SpringScrollView.scrollingChangeListener
import com.example.sample.myapplication.R

class PullToRefreshScrollViewActivity2 : Activity() {
    //var mSpringLayout: SpringRelativeLayout? = null
    //var mRefreshLayout: SpringRefreshLayout? = null
    //var mScrollView: SpringScrollView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_to_refresh_scrollview2)
        var mRefreshLayout = findViewById<View>(R.id.swipe_container) as SpringRefreshLayout
        var mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        var mScrollView = findViewById<View>(R.id.scrollview) as SpringScrollView
        mSpringLayout!!.addSpringView(R.id.scrollview)
        val scrollView = findViewById<View>(R.id.scrollview) as SpringScrollView
        scrollView.setEdgeEffectFactory(mSpringLayout!!.createViewEdgeEffectFactory())
        //scrollView.setOverScrollNested(true);
        mRefreshLayout!!.setOverScrollChild(scrollView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.setEdgeEffectColor(0x00D6D9D6)
        }
        mRefreshLayout!!.setOnRefreshListener {
            mRefreshLayout!!.isRefreshing = true
            Log.d("SpringScrollView", "onRefresh")
            Handler().postDelayed({ mRefreshLayout!!.isRefreshing = false }, 300)
        }
        mScrollView.setScrollingChangeListener(object : scrollingChangeListener {
            override fun getDir(): Int {
                return -1 //listening edge top;
            }

            override fun onAllowed(i: Int) {
                mRefreshLayout!!.isEnabled = false
            }

            override fun onStop(i: Int) {
                mRefreshLayout!!.isEnabled = true
            }
        })
    }
}