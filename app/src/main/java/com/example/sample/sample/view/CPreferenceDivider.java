package com.example.sample.sample.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.PreferenceCategory;

import com.example.sample.myapplication.R;

public class CPreferenceDivider extends PreferenceCategory {

    public CPreferenceDivider(Context context) {
        super(context);
        initViews(context);
    }

    public CPreferenceDivider(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CPreferenceDivider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(R.layout.op_ctrl_preference_divider);
    }
}
