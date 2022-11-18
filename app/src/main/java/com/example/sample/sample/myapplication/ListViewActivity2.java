package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sample.sample.view.MyAdapter;
import com.example.lib.effect.effect.widget.SpringListView2;
import com.example.sample.myapplication.R;


public class ListViewActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout_2);


        SpringListView2 listView = findViewById(R.id.listview);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        final MyAdapter adapter = new MyAdapter(this, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(ListViewActivity2.this, "click item " + i, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        View item = this.getLayoutInflater().inflate(R.layout.text_row_item, null, false);
        TextView headerText = item.findViewById(R.id.textView);
        listView.addHeaderView(item);
        headerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(ListViewActivity2.this, "click header text", Toast.LENGTH_SHORT);
                toast.show();
                Log.d("ListViewAct", "header textview onClick");
            }
        });
        listView.setEdgeEffectColor(0xffffff);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i ==  AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //Log.d("ListView", "onScrollStateChage IDEL " + mSpringLayout.isSpringAnimation());
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

    }

    String[] values = new String[] {
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
    };
}
