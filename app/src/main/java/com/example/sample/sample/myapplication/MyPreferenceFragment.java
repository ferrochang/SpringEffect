package com.example.sample.sample.myapplication;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceRecyclerViewAccessibilityDelegate;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample.myapplication.R;

import java.util.ArrayList;

public class MyPreferenceFragment extends PreferenceFragmentCompat {
    PreferenceCategory mTestingCategory;
    ArrayList<Preference> mList = new ArrayList<>();
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //setPreferencesFromResource(R.xml.preferences, rootKey);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setDivider(null); // better animation result in adding items;
        return view;
    }

    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
                                             Bundle savedInstanceState) {

        if (getContext().getPackageManager().hasSystemFeature(PackageManager
                .FEATURE_AUTOMOTIVE)) {
            RecyclerView recyclerView = parent.findViewById(R.id.recycler_view);
            if (recyclerView != null) {
                return recyclerView;
            }
        }
        RecyclerView recyclerView = (RecyclerView) inflater
                .inflate(R.layout.spring_preference_recyclerview, parent, false);

        recyclerView.setLayoutManager(onCreateLayoutManager());
        recyclerView.setAccessibilityDelegateCompat(
                new PreferenceRecyclerViewAccessibilityDelegate(recyclerView));

        return recyclerView;
    }

    private int keyIndex = 0;
    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        //RecyclerView.Adapter adapter = super.onCreateAdapter(preferenceScreen);
        mTestingCategory = (PreferenceCategory) preferenceScreen.getPreference(0);
        Preference testingPrefAdd = mTestingCategory.getPreference(0);
        testingPrefAdd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Preference aNewPref = new Preference(preference.getContext());
                aNewPref.setKey("key_new "+keyIndex);
                aNewPref.setTitle("title" + keyIndex);
                aNewPref.setIcon(R.drawable.ic_wifi_signal_4);
                mList.add(keyIndex, aNewPref);
                mTestingCategory.addPreference(aNewPref);
                keyIndex++;
                return false;
            }
        });

        Preference testingPrefDel = mTestingCategory.getPreference(1);
        testingPrefDel.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!mList.isEmpty()) {
                    int candidateIndex = (int)(Math.random()* (mList.size() - 1));
                    //mTestingCategory.removePreference(mList.get(keyIndex - 1));
                    Preference candidate = mList.get(candidateIndex);
                    boolean result = mTestingCategory.removePreference(candidate);
                    if (result) {
                        mList.remove(candidate);
                        keyIndex--;
                    }
                }
                return false;
            }
        });

        return super.onCreateAdapter(preferenceScreen);

    }

}
