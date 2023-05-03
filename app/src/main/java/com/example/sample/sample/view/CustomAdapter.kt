package com.example.sample.sample.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.myapplication.R
import java.util.Arrays

class CustomAdapter(dataSet: Array<String?>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    //private String[] mDataSet;
    val mDataList: ArrayList<String?>? = ArrayList(Arrays.asList(*dataSet))
    private var mHorizontal = false
    private var mNewItemIndex = 0
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View, listener: MyItemClickListener?) : RecyclerView.ViewHolder(v) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { v ->
                Log.d(TAG, "Element $adapterPosition clicked.")
                listener?.onListItemClicked(v)
            }
            textView = v.findViewById<View>(R.id.textView) as TextView
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    /*
    init {
        //mDataSet = dataSet;
        //mDataList = Arrays.asList(dataSet);
        mDataList = ArrayList(Arrays.asList(*dataSet))
    }

     */

    constructor(dataSet: Array<String?>, listener: MyItemClickListener?) : this(dataSet) {
        mItemClickListener = listener
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(
                if (mHorizontal) R.layout.text_column_item else R.layout.text_row_item,
                viewGroup,
                false
            )
        return ViewHolder(v, mItemClickListener)
    }

    // END_INCLUDE(recyclerViewOnCreateViewHolder)
    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.textView.text = mDataList!![position]
    }

    // END_INCLUDE(recyclerViewOnBindViewHolder)
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataList!!.size
    }

    fun setHorizontal() {
        mHorizontal = true
    }

    fun removeItemAtPosition(position: Int) {
        mDataList!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItemAtPosition(position: Int) {
        mDataList!!.add(position, "New Item " + mNewItemIndex++)
        notifyItemInserted(position)
    }

    companion object {
        private const val TAG = "CustomAdapter"
        private var mItemClickListener: MyItemClickListener? = null
    }
}