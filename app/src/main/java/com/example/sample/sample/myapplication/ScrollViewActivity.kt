package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.lib.effect.effect.widget.SpringScrollView
import com.example.sample.myapplication.R

class ScrollViewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scrollview_layout)
        val springRelativeLayout_scrollView =
            findViewById<View>(R.id.scrollview_spring_layout) as SpringRelativeLayout
        springRelativeLayout_scrollView.addSpringView(R.id.scrollview)
        val scrollView = findViewById<View>(R.id.scrollview) as SpringScrollView
        scrollView.setEdgeEffectFactory(springRelativeLayout_scrollView.createViewEdgeEffectFactory())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.setEdgeEffectColor(0xffffff)
        }
        val view = findViewById<View>(R.id.textView) as TextView
        view.setOnClickListener { Log.d("ScrollActivity", "onClick") }
    }
}