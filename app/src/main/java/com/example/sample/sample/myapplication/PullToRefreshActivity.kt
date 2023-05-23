package com.example.sample.sample.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lib.effect.effect.widget.SpringListView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.MyAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PullToRefreshActivity : AppCompatActivity() {
    val TAG = localClassName
    //var mSpringLayout: SpringRelativeLayout? = null
    //var mRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pull_to_refresh)
        val mRefreshLayout = findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        val mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        mSpringLayout.addSpringView(R.id.listview)
        val listView = findViewById<SpringListView>(R.id.listview)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        //val adapter = MyAdapter(this, values)
        listView.adapter = MyAdapter(this, values)
        listView.setEdgeEffectFactory(mSpringLayout.createViewEdgeEffectFactory())
        listView.setOverScrollNested(true)
        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val toast =
                Toast.makeText(this@PullToRefreshActivity, "click item $i", Toast.LENGTH_SHORT)
            toast.show()
        }
        val item = this.layoutInflater.inflate(android.R.layout.simple_list_item_1, null, false)
        val headerText = item.findViewById<TextView>(android.R.id.text1)
        headerText.text = "Header"
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
        mRefreshLayout.setOnRefreshListener {
            mRefreshLayout.isRefreshing = true

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