package com.example.lib.effect.effect.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.lib.effect.effect.widget.SpringListView;
import com.example.lib.effect.effect.widget.SpringListView2;
import com.example.lib.effect.effect.widget.SpringRelativeLayout;

public class Replacement {
    public static void enableSpringEdgeEffect(LayoutInflater inflater, View view, ViewGroup container, int layout_id) {

        View list = view.findViewById(android.R.id.list);
        //android.util.Log.d("MyPref", "list " + list);
        if (list == null)
            return;
        android.view.ViewParent parent = list.getParent();
        //Log.d("mMsSetting", "pa " + parent);
        if (parent == null)
            return;
        //Log.d("mMsSetting", " container " + container);
        //if (container == null)
        //    return;

        ((ViewGroup) parent).removeView(list);

        SpringRelativeLayout springRelativeLayout = new SpringRelativeLayout(view.getContext());
        //ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        //        ViewGroup.LayoutParams.MATCH_PARENT);
        //springRelativeLayout.setLayoutParams(p);
        springRelativeLayout.setFocusable(true);
        springRelativeLayout.setFocusableInTouchMode(true);
        springRelativeLayout.setSaveEnabled(false);
        //android.view.ContextThemeWrapper newContext = new ContextThemeWrapper(view.getContext(), R.style.OneplusPreferenceLayoutStyle);
        //SpringListView springListView = new SpringListView(newContext);
        SpringListView springListView = (SpringListView) inflater.inflate(layout_id, null, false);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        springListView.setLayoutParams(p);
        springRelativeLayout.addView(springListView);
        springRelativeLayout.addSpringView(android.R.id.list);
        springListView.setEdgeEffectFactory(springRelativeLayout.createViewEdgeEffectFactory());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        //springListView.setEdgeEffectColor(0x111111);
        ((ViewGroup) parent).addView(springRelativeLayout,0, lp);
    }

    public void enableSpringEdgeEffect(LayoutInflater inflater, ListView list, int layout_id) {

        if (list == null)
            return;
        android.view.ViewParent parent = list.getParent();
        if (parent == null)
            return;

        SpringListView2 springList = (SpringListView2) inflater.inflate(layout_id, null, false);

        springList.setId(android.R.id.list);

        ((ViewGroup) parent).removeView(list);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        ((ViewGroup) parent).addView(springList, 0, p);
        springList.setEdgeEffectColor(0xffffff); //optional; according to the background color;

    }
}
