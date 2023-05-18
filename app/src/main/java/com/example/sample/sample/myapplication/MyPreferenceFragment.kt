package com.example.sample.sample.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceRecyclerViewAccessibilityDelegate
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.myapplication.R

class MyPreferenceFragment : PreferenceFragmentCompat() {
    //var mTestingCategory: PreferenceCategory? = null
    //var mList = ArrayList<Preference>()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //setPreferencesFromResource(R.xml.preferences, rootKey);
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setDivider(null) // better animation result in adding items;
        return view
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater, parent: ViewGroup,
        savedInstanceState: Bundle?
    ): RecyclerView {
        if (context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            val recyclerView = parent.findViewById<RecyclerView>(R.id.recycler_view)
            if (recyclerView != null) {
                return recyclerView
            }
        }
        val recyclerView = inflater
            .inflate(R.layout.spring_preference_recyclerview, parent, false) as RecyclerView
        recyclerView.layoutManager = onCreateLayoutManager()
        recyclerView.setAccessibilityDelegateCompat(
            PreferenceRecyclerViewAccessibilityDelegate(recyclerView)
        )
        return recyclerView
    }

    private var keyIndex = 0
    override fun onCreateAdapter(preferenceScreen: PreferenceScreen): RecyclerView.Adapter<*> {
        //RecyclerView.Adapter adapter = super.onCreateAdapter(preferenceScreen);
        val mTestingCategory = preferenceScreen.getPreference(0) as PreferenceCategory
        val testingPrefAdd = mTestingCategory.getPreference(0)
        var mList = ArrayList<Preference>()
        testingPrefAdd.onPreferenceClickListener =
            Preference.OnPreferenceClickListener { preference ->
                val aNewPref = Preference(preference.context)
                aNewPref.key = "key_new $keyIndex"
                aNewPref.title = "title$keyIndex"
                aNewPref.setIcon(R.drawable.ic_wifi_signal_4)
                mList.add(keyIndex, aNewPref)
                mTestingCategory!!.addPreference(aNewPref)
                keyIndex++
                false
            }
        val testingPrefDel = mTestingCategory.getPreference(1)
        testingPrefDel.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (mList.isNotEmpty()) {
                val candidateIndex = (Math.random() * (mList.size - 1)).toInt()
                //mTestingCategory.removePreference(mList.get(keyIndex - 1));
                val candidate = mList[candidateIndex]
                val result = mTestingCategory!!.removePreference(candidate)
                if (result) {
                    mList.remove(candidate)
                    keyIndex--
                }
            }
            false
        }
        return super.onCreateAdapter(preferenceScreen)
    }
}