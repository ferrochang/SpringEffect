package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import android.widget.Toast
import com.example.lib.effect.effect.widget.SpringListView2
import com.example.sample.myapplication.R
import com.example.sample.sample.view.MyAdapter

class ListViewActivity2 : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listview_layout_2)
        val listView = findViewById<SpringListView2>(R.id.listview)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        val adapter = MyAdapter(this, values)
        listView.adapter = adapter
        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val toast = Toast.makeText(this@ListViewActivity2, "click item $i", Toast.LENGTH_SHORT)
            toast.show()
        }
        val item = this.layoutInflater.inflate(android.R.layout.simple_list_item_1, null, false)
        val headerText = item.findViewById<TextView>(android.R.id.text1)
        headerText.setText("Header")
        listView.addHeaderView(item)
        headerText.setOnClickListener {
            val toast =
                Toast.makeText(this@ListViewActivity2, "click header text", Toast.LENGTH_SHORT)
            toast.show()
            Log.d("ListViewAct", "header textview onClick")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listView.setEdgeEffectColor(0xffffff)
        }
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //Log.d("ListView", "onScrollStateChage IDEL " + mSpringLayout.isSpringAnimation());
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
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
        "List item 6",
        "List item 7"
    )
}