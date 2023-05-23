package com.example.sample.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lib.effect.effect.widget.SpringListView
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.sample.sample.view.MyAdapter

class ListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_list, container, false)
        val springLayout = viewRoot.findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        springLayout.addSpringView(R.id.listview)
        val listView = viewRoot.findViewById<SpringListView>(R.id.listview)
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.adapter = context?.let { MyAdapter(it, values) }
        listView.setEdgeEffectFactory(springLayout.createViewEdgeEffectFactory())
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val toast = Toast.makeText(context, "click item $i", Toast.LENGTH_SHORT)
            toast.show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listView.setEdgeEffectColor(0xffffff)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
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