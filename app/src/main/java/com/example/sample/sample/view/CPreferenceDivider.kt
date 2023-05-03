package com.example.sample.sample.view

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceCategory
import com.example.sample.myapplication.R

class CPreferenceDivider : PreferenceCategory {
    constructor(context: Context) : super(context) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initViews(context)
    }

    private fun initViews(context: Context) {
        layoutResource = R.layout.op_ctrl_preference_divider
    }
}