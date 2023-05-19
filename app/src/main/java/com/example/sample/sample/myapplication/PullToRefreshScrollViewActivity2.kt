package com.example.sample.sample.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lib.effect.effect.widget.SpringRefreshLayout
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.lib.effect.effect.widget.SpringScrollView
import com.example.lib.effect.effect.widget.SpringScrollView.scrollingChangeListener
import com.example.sample.myapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PullToRefreshScrollViewActivity2 : AppCompatActivity() {
    //var mSpringLayout: SpringRelativeLayout? = null
    //var mRefreshLayout: SpringRefreshLayout? = null
    //var mScrollView: SpringScrollView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_to_refresh_scrollview2)
        val mRefreshLayout = findViewById<View>(R.id.swipe_container) as SpringRefreshLayout
        val mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        mSpringLayout.addSpringView(R.id.scrollview)
        val scrollView = findViewById<View>(R.id.scrollview) as SpringScrollView
        scrollView.setEdgeEffectFactory(mSpringLayout.createViewEdgeEffectFactory())
        //scrollView.setOverScrollNested(true);
        mRefreshLayout.setOverScrollChild(scrollView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.setEdgeEffectColor(0x00D6D9D6)
        }
        mRefreshLayout.setOnRefreshListener {
            mRefreshLayout.isRefreshing = true
            Log.d("SpringScrollView", "onRefresh")

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
            //Handler().postDelayed({ mRefreshLayout!!.isRefreshing = false }, 300)
        }
        scrollView.setScrollingChangeListener(object : scrollingChangeListener {
            override fun getDir(): Int {
                return -1 //listening edge top;
            }

            override fun onAllowed(i: Int) {
                mRefreshLayout.isEnabled = false
            }

            override fun onStop(i: Int) {
                mRefreshLayout.isEnabled = true
            }
        })
    }
}