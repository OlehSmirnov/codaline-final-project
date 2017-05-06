package com.olegsmirnov.codalinefinalproject;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

public class SettingsFragment extends PreferenceFragment {

    SwitchPreference switchPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        switchPreference = (SwitchPreference) findPreference(getString(R.string.prefs_light_style));
        if (switchPreference.isChecked()) {
            switchPreference.setTitle(getString(R.string.light_style));
        }
        else {
            switchPreference.setTitle(getString(R.string.dark_style));
        }
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (!switchPreference.isChecked()) {
                    switchPreference.setTitle(getString(R.string.light_style));
                }
                else {
                    switchPreference.setTitle(getString(R.string.dark_style));
                }
                return true;
            }
        });
    }
}
