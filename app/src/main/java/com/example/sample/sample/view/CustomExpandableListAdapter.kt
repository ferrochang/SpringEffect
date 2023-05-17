package com.example.sample.sample.view

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.sample.myapplication.R

class CustomExpandableListAdapter(
    private val mContext: Context, private val expandableListTitle: List<String>,
    private val expandableListDetail: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {
    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return expandableListDetail[expandableListTitle[listPosition]]!!
            .get(expandedListPosition)
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var refView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (refView == null) {
            val layoutInflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            refView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val expandedListTextView = refView!!
            .findViewById<View>(R.id.expandedListItem) as TextView
        expandedListTextView.text = expandedListText
        return refView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return expandableListDetail[expandableListTitle[listPosition]]
            ?.size ?: 0
    }

    override fun getGroup(listPosition: Int): Any {
        return expandableListTitle[listPosition]
    }

    override fun getGroupCount(): Int {
        return expandableListTitle.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var refView = convertView
        val listTitle = getGroup(listPosition) as String
        if (refView == null) {
            val layoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            refView = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = refView!!
            .findViewById<View>(R.id.listTitle) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
        return refView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}