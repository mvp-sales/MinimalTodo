package com.example.avjindersinghsekhon.minimaltodo.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.utility.PreferenceKeys

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    private lateinit var app: AnalyticsApplication

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_layout, rootKey)
        app = requireActivity().application as AnalyticsApplication
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        val preferenceKeys = PreferenceKeys(resources)
        if (key == preferenceKeys.night_mode_pref_key) {
            val themePreferences = requireActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE)
            val themeEditor = themePreferences.edit()
            //We tell our MainLayout to recreate itself because mode has changed
            themeEditor.putBoolean(MainFragment.RECREATE_ACTIVITY, true)
            val checkBoxPreference = findPreference(preferenceKeys.night_mode_pref_key) as? CheckBoxPreference
            if (checkBoxPreference != null && checkBoxPreference.isChecked) {
                //Comment out this line if not using Google Analytics
                app.send("this", "Settings", "Night Mode used")
                themeEditor.putString(MainFragment.THEME_SAVED, MainFragment.DARKTHEME)
            } else {
                themeEditor.putString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
            }
            themeEditor.apply()
            requireActivity().recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
}
