package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.lib.effect.effect.widget.SpringScrollView2
import com.example.sample.myapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        mRefreshLayout.setOnRefreshListener(OnRefreshListener {
            mRefreshLayout.isRefreshing = true
            Log.d("Ferro", "onRefresh ${Thread.currentThread()}")
            //Handler().postDelayed({ mRefreshLayout.setRefreshing(false) }, 300)

            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)

                mRefreshLayout.isRefreshing = false

                android.util.Log.d("Ferro", "Thread: ${Thread.currentThread()}")
            }

            //Log.d("Ferro", "after that ${Thread.currentThread()}")

            /*
                Thread {
                    Thread.sleep(1000)

                    android.util.Log.d("Ferro", "Thread: ${Thread.currentThread()}")
                    mRefreshLayout.isRefreshing = false
                }.start()

             */

        })

        val button = findViewById<Button>(R.id.textView)
        button.setOnClickListener { Log.d("RefreshScrollViewActivity", "onClick") }
    }
}