package com.example.sample.sample.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.sample.myapplication.R

class MyAdapter(context: Context, strs: Array<String>?) : BaseAdapter() {
    private val mLayInf: LayoutInflater

    //List<Map<String, Object>> mItemList;
    var mStr: Array<String>? = null
    var mContext: Context

    init {
        mLayInf = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mContext = context
        mStr = strs
    }

    override fun getCount(): Int {
        return mStr!!.size
        //return 1000;
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val v: View
        v = convertView ?: mLayInf.inflate(R.layout.text_row_item, parent, false)
        val txtView = v.findViewById<View>(R.id.textView) as TextView
        txtView.tag = "pos $position"
        if (convertView == null) {
            txtView.setOnClickListener { view ->
                val tag = view.tag
                val toast = Toast.makeText(mContext, "click $tag", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        txtView.text = mStr!![position]
        //txtView.setText(" text " + position);
        return v
    }
}