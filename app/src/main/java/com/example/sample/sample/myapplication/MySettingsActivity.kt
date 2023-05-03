package com.example.sample.sample.myapplication

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.sample.myapplication.R

class MySettingsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        //final boolean topLevel = getIntent().getBooleanExtra(
        //        UIIntents.UI_INTENT_EXTRA_TOP_LEVEL_SETTINGS, false);
        //if (topLevel) {
        actionBar!!.title = getString(R.string.title)
        //}

        // Display the fragment as the main content.
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, MyPreferenceFragment())
                .commit()
        }
    }
}