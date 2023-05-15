package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lib.effect.effect.widget.SpringListView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.MyAdapter

class PullToRefreshActivity : Activity() {
    //var mSpringLayout: SpringRelativeLayout? = null
    //var mRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_to_refresh)
        var mRefreshLayout = findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        var mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        mSpringLayout!!.addSpringView(R.id.listview)
        val listView = findViewById<SpringListView>(R.id.listview)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        val adapter = MyAdapter(this, values)
        listView.adapter = adapter
        listView.setEdgeEffectFactory(mSpringLayout!!.createViewEdgeEffectFactory())
        listView.setOverScrollNested(true)
        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val toast =
                Toast.makeText(this@PullToRefreshActivity, "click item $i", Toast.LENGTH_SHORT)
            toast.show()
        }
        val item = this.layoutInflater.inflate(R.layout.text_row_item, null, false)
        val headerText = item.findViewById<TextView>(R.id.textView)
        listView.addHeaderView(item)
        headerText.setOnClickListener {
            val toast =
                Toast.makeText(this@PullToRefreshActivity, "click header text", Toast.LENGTH_SHORT)
            toast.show()
            Log.d("PullToRefreshActivity", "header textview onClick")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listView.setEdgeEffectColor(0xffffff)
        }
        mRefreshLayout!!.setOnRefreshListener {
            mRefreshLayout!!.isRefreshing = true
            Handler().postDelayed({ mRefreshLayout!!.isRefreshing = false }, 300)
        }
    }

    var values = arrayOf(
        "List item 1",
        "List item 2",
        "List item 3",
        "List item 4",
        "List item 5",
        "List item 6",
        "List item 1",
        "List item 2",
        "List item 3",
        "List item 4",
        "List item 5",
        "List item 6", "List item 1",
        "List item 2",
        "List item 3",
        "List item 4",
        "List item 5",
        "List item 6", "List item 1",
        "List item 2",
        "List item 3",
        "List item 4",
        "List item 5",
        "List item 6",
        "List item 7"
    )
}