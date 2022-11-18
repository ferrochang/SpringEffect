package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.sample.sample.view.MyAdapter;
import com.example.lib.effect.effect.widget.SpringListView;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.sample.myapplication.R;


public class ListActivity extends Activity {
    SpringRelativeLayout mSpringLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        mSpringLayout.addSpringView(R.id.listview);
        SpringListView listView = findViewById(R.id.listview);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        MyAdapter adapter = new MyAdapter(this, values);
        listView.setAdapter(adapter);
        listView.setEdgeEffectFactory(mSpringLayout.createViewEdgeEffectFactory());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(ListActivity.this, "click item " + i, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        listView.setEdgeEffectColor(0xffffff);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                Log.d("ListActivity", "original onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Log.d("ListActivity", "original onScroll");
            }
        });
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

    String[] values = new String[] {
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
    };
}
