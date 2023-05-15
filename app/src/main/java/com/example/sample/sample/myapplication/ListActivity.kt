package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import com.example.lib.effect.effect.widget.SpringListView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.MyAdapter

class ListActivity : Activity() {
    //var mSpringLayout: SpringRelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listview)
        var mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        mSpringLayout!!.addSpringView(R.id.listview)
        val listView = findViewById<SpringListView>(R.id.listview)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        val adapter = MyAdapter(this, values)
        listView.adapter = adapter
        listView.setEdgeEffectFactory(mSpringLayout!!.createViewEdgeEffectFactory())
        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val toast = Toast.makeText(this@ListActivity, "click item $i", Toast.LENGTH_SHORT)
            toast.show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listView.setEdgeEffectColor(0xffffff)
        }
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                Log.d("ListActivity", "original onScrollStateChanged")
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {
                Log.d("ListActivity", "original onScroll")
            }
        })
        /*
        TextView t = new TextView(this);
        t.setText("header");
        listView.addHeaderView(t);
        TextView b = new TextView(this);
        b.setText("footer");
        listView.addFooterView(b);

         */
        //listView.setEdgeEffectColor(this.getColor(android.R.color.holo_green_dark));
    }

    var values = arrayOf(
        "List item 1",
        "List item 2",
        "List item 3",
        "List item 4",
        "List item 5",
        "List item 6",
        "List item 7",
        "List item 8",
        "List item 9",
        "List item 1",
        "List item 7"
    )
}