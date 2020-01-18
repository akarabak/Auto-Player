package com.example.dexel.autoplayer.views

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.dexel.autoplayer.R


class Settings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_general, rootKey)
    }
}
