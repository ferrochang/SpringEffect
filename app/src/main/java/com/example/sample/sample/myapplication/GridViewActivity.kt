package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import com.example.lib.effect.effect.widget.SpringGridView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.MyAdapter

class GridViewActivity : Activity() {
    //var mSpringLayout: SpringRelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gridview_layout)
        var mSpringLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        mSpringLayout!!.addSpringView(R.id.gridview)
        val gridView = findViewById<SpringGridView>(R.id.gridview)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        val adapter = MyAdapter(this, values)
        gridView.adapter = adapter
        gridView.numColumns = 2
        gridView.setEdgeEffectFactory(mSpringLayout!!.createViewEdgeEffectFactory())
        gridView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val toast = Toast.makeText(this@GridViewActivity, "click item $i", Toast.LENGTH_SHORT)
            toast.show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            gridView.setEdgeEffectColor(0xffffff)
        }
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