package com.example.sample.sample.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sample.myapplication.R;


public class MyAdapter extends BaseAdapter {
    private LayoutInflater mLayInf;
    //List<Map<String, Object>> mItemList;
    String[] mStr = null;
    Context mContext;
    public MyAdapter(Context context, String[] strs)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mStr = strs;
    }

    @Override
    public int getCount()
    {
        return mStr.length;
        //return 1000;
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View v;
        if (convertView == null) {
            v = mLayInf.inflate(R.layout.text_row_item, parent, false);
        } else
            v = convertView;


        TextView txtView = (TextView) v.findViewById(R.id.textView);
        txtView.setTag("pos " + position);

        if (convertView == null) {
            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object tag = view.getTag();
                    Toast toast = Toast.makeText(mContext, "click " + tag, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }


        txtView.setText(mStr[position]);
        //txtView.setText(" text " + position);

        return v;
    }
}
