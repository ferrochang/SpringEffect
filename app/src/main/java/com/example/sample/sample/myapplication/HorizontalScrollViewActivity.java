package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lib.effect.effect.widget.SpringHorizontalScrollView;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.sample.myapplication.R;


public class HorizontalScrollViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horizontal_scrollview);

        SpringRelativeLayout springRelativeLayout_scrollView = (SpringRelativeLayout) findViewById(R.id.scrollview_spring_layout);
        springRelativeLayout_scrollView.addSpringView(R.id.scrollview);
        SpringHorizontalScrollView scrollView = (SpringHorizontalScrollView) findViewById(R.id.scrollview);
        scrollView.setEdgeEffectFactory(springRelativeLayout_scrollView.createViewEdgeEffectFactory(true));
        scrollView.setEdgeEffectColor(0xffffff);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(HorizontalScrollViewActivity.this, "click item ", Toast.LENGTH_SHORT);
                toast.show();
                Log.d("HorizontalScrollViewAct", "item onClick");
            }
        });

    }
}
