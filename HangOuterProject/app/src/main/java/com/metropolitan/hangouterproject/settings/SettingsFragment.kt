package com.metropolitan.hangouterproject.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.MyApplication
import javax.inject.Inject


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        (activity?.application as MyApplication).getSettingsComponent().inject(this)
        setPreferencesFromResource(R.xml.preferences, p1)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals("themeKey") || key.equals("languageKey"))
            activity?.recreate()
    }

    override fun onResume() {
        super.onResume()
        preferencesSettings.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferencesSettings.unregisterOnSharedPreferenceChangeListener(this)
    }
}