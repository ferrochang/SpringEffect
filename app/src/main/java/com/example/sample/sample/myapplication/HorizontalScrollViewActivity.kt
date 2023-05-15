package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.lib.effect.effect.widget.SpringHorizontalScrollView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.R

class HorizontalScrollViewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.horizontal_scrollview)
        val springRelativeLayout_scrollView =
            findViewById<View>(R.id.scrollview_spring_layout) as SpringRelativeLayout
        springRelativeLayout_scrollView.addSpringView(R.id.scrollview)
        val scrollView = findViewById<View>(R.id.scrollview) as SpringHorizontalScrollView
        scrollView.setEdgeEffectFactory(
            springRelativeLayout_scrollView.createViewEdgeEffectFactory(
                true
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.setEdgeEffectColor(0xffffff)
        }
        val textView = findViewById<View>(R.id.textView) as TextView
        textView.setOnClickListener {
            val toast =
                Toast.makeText(this@HorizontalScrollViewActivity, "click item ", Toast.LENGTH_SHORT)
            toast.show()
            Log.d("HorizontalScrollViewAct", "item onClick")
        }
    }
}