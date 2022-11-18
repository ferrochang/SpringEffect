package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.sample.sample.view.MyAdapter;
import com.example.lib.effect.effect.widget.SpringGridView;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.sample.myapplication.R;


public class GridViewActivity extends Activity {
    SpringRelativeLayout mSpringLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_layout);

        mSpringLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        mSpringLayout.addSpringView(R.id.gridview);

        SpringGridView gridView = findViewById(R.id.gridview);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        MyAdapter adapter = new MyAdapter(this, values);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(2);
        gridView.setEdgeEffectFactory(mSpringLayout.createViewEdgeEffectFactory());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(GridViewActivity.this, "click item " + i, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        gridView.setEdgeEffectColor(0xffffff);
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
