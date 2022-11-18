package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sample.sample.view.MyAdapter;
import com.example.lib.effect.effect.widget.SpringListView;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.sample.myapplication.R;


public class PullToRefreshActivity extends Activity {
    SpringRelativeLayout mSpringLayout;
    SwipeRefreshLayout mRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_to_refresh);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        mSpringLayout.addSpringView(R.id.listview);

        final SpringListView listView = findViewById(R.id.listview);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        MyAdapter adapter = new MyAdapter(this, values);
        listView.setAdapter(adapter);

        listView.setEdgeEffectFactory(mSpringLayout.createViewEdgeEffectFactory());
        listView.setOverScrollNested(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(PullToRefreshActivity.this, "click item " + i, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        View item = this.getLayoutInflater().inflate(R.layout.text_row_item, null, false);
        TextView headerText = item.findViewById(R.id.textView);
        listView.addHeaderView(item);
        headerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(PullToRefreshActivity.this, "click header text", Toast.LENGTH_SHORT);
                toast.show();
                Log.d("PullToRefreshActivity", "header textview onClick");
            }
        });
        listView.setEdgeEffectColor(0xffffff);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 300);
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
            "List item 6",            "List item 1",
            "List item 2",
            "List item 3",
            "List item 4",
            "List item 5",
            "List item 6",            "List item 1",
            "List item 2",
            "List item 3",
            "List item 4",
            "List item 5",
            "List item 6",
            "List item 7"
    };

}
