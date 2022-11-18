package com.example.sample.sample.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sample.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    //private String[] mDataSet;
    private ArrayList<String> mDataList = null;

    private boolean mHorizontal = false;

    static private MyItemClickListener mItemClickListener = null;

    private int mNewItemIndex = 0;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v, final MyItemClickListener listener) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    if (listener != null)
                        listener.onListItemClicked(v);
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(String[] dataSet) {
        //mDataSet = dataSet;
        //mDataList = Arrays.asList(dataSet);
        mDataList = new ArrayList<>(Arrays.asList(dataSet));
    }

    public CustomAdapter(String[] dataSet, MyItemClickListener listener) {
        this(dataSet);
        mItemClickListener = listener;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(mHorizontal?R.layout.text_column_item:R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v, mItemClickListener);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(mDataList.get(position));
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setHorizontal() {
        mHorizontal = true;
    }

    public void removeItemAtPosition(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItemAtPosition(int position) {
        mDataList.add(position, "New Item " + mNewItemIndex++);
        notifyItemInserted(position);
    }
}
