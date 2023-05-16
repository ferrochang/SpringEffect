package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupCollapseListener
import android.widget.ExpandableListView.OnGroupExpandListener
import android.widget.Toast
import com.example.lib.effect.effect.widget.SpringExpandableListView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.myapplication.R
import com.example.sample.sample.view.CustomExpandableListAdapter
import com.example.sample.sample.view.ExpandableListDataPump

class ExpandableListDemo : Activity() {
    //var expandableListView: SpringExpandableListView? = null
    //var expandableListAdapter: ExpandableListAdapter? = null
    //var expandableListTitle: List<String>? = null
    //var expandableListDetail: HashMap<String, List<String>>? = null
    //var springLayout: SpringRelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expandablelist_layout)
        var springLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        springLayout.addSpringView(R.id.expandableListView)
        var expandableListView = findViewById<View>(R.id.expandableListView) as SpringExpandableListView
        expandableListView.setEdgeEffectFactory(springLayout.createViewEdgeEffectFactory())
        var expandableListDetail = ExpandableListDataPump.data
        var expandableListTitle = ArrayList(expandableListDetail.keys)
        var expandableListAdapter =
            CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail)
        expandableListView.setAdapter(expandableListAdapter)
        expandableListView.setOnGroupExpandListener(OnGroupExpandListener { groupPosition ->
            Toast.makeText(
                applicationContext,
                expandableListTitle.get(groupPosition) + " List Expanded.",
                Toast.LENGTH_SHORT
            ).show()
        })
        expandableListView.setOnGroupCollapseListener(OnGroupCollapseListener { groupPosition ->
            Toast.makeText(
                applicationContext,
                expandableListTitle.get(groupPosition) + " List Collapsed.",
                Toast.LENGTH_SHORT
            ).show()
        })
        expandableListView.setOnChildClickListener(OnChildClickListener { parent, v, groupPosition, childPosition, id ->
            Toast.makeText(
                applicationContext,
                expandableListTitle.get(groupPosition)
                        + " -> "
                        + expandableListDetail.get(
                    expandableListTitle.get(groupPosition)
                )!![childPosition], Toast.LENGTH_SHORT
            ).show()
            false
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            expandableListView.setEdgeEffectColor(0xffffff)
        }
    }
}