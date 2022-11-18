package com.example.sample.sample.myapplication;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.sample.myapplication.R;

public class MySettingsActivity extends FragmentActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //final boolean topLevel = getIntent().getBooleanExtra(
        //        UIIntents.UI_INTENT_EXTRA_TOP_LEVEL_SETTINGS, false);
        //if (topLevel) {
        getActionBar().setTitle(getString(R.string.title));
        //}

        // Display the fragment as the main content.
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new MyPreferenceFragment())
                    .commit();
        }

    }
}
