package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;


import com.example.sample.sample.view.CustomExpandableListAdapter;
import com.example.sample.sample.view.ExpandableListDataPump;
import com.example.lib.effect.effect.widget.SpringExpandableListView;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.sample.myapplication.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDemo extends Activity {
    SpringExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    SpringRelativeLayout springLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandablelist_layout);
        springLayout = findViewById(R.id.spring_layout);
        springLayout.addSpringView(R.id.expandableListView);
        expandableListView =  findViewById(R.id.expandableListView);
        expandableListView.setEdgeEffectFactory(springLayout.createViewEdgeEffectFactory());
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
        expandableListView.setEdgeEffectColor(0xffffff);
    }

}
