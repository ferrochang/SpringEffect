package com.example.sample.sample.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.lib.effect.effect.widget.SpringScrollView
import com.example.sample.myapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PullToRefreshScrollViewActivity : AppCompatActivity() {
    //var mSpringLayout: SpringRelativeLayout? = null
    //var mRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_to_refresh_scrollview)
        val mRefreshLayout = findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        val mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        mSpringLayout.addSpringView(R.id.scrollview)
        val scrollView = findViewById<View>(R.id.scrollview) as SpringScrollView
        scrollView.setEdgeEffectFactory(mSpringLayout!!.createViewEdgeEffectFactory())
        scrollView.setOverScrollNested(true)
        scrollView.isNestedScrollingEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.setEdgeEffectColor(0xffffff)
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
    }
}