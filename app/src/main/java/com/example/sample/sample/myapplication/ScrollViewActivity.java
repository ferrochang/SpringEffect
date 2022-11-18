package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.lib.effect.effect.widget.SpringScrollView;
import com.example.sample.myapplication.R;


public class ScrollViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollview_layout);

        SpringRelativeLayout springRelativeLayout_scrollView = (SpringRelativeLayout) findViewById(R.id.scrollview_spring_layout);
        springRelativeLayout_scrollView.addSpringView(R.id.scrollview);
        SpringScrollView scrollView = (SpringScrollView) findViewById(R.id.scrollview);
        scrollView.setEdgeEffectFactory(springRelativeLayout_scrollView.createViewEdgeEffectFactory());
        scrollView.setEdgeEffectColor(0xffffff);

        TextView view = (TextView) findViewById(R.id.textView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ScrollActivity", "onClick");
            }
        });
    }
}
