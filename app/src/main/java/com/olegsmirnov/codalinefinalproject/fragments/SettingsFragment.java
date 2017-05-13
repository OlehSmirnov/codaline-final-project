package com.olegsmirnov.codalinefinalproject.fragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import com.olegsmirnov.codalinefinalproject.R;

public class SettingsFragment extends PreferenceFragment {

    SwitchPreference switchPreference;

    LinearLayout layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = (LinearLayout) getActivity().findViewById(R.id.settings_layout);
        addPreferencesFromResource(R.xml.settings);
        switchPreference = (SwitchPreference) findPreference(getString(R.string.prefs_light_style));
        if (switchPreference.isChecked()) {
            switchPreference.setTitle(getString(R.string.light_style));
            layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        }
        else {
            switchPreference.setTitle(getString(R.string.dark_style));
            layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGray));
        }
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (!switchPreference.isChecked()) {
                    switchPreference.setTitle(getString(R.string.light_style));
                    layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                }
                else {
                    switchPreference.setTitle(getString(R.string.dark_style));
                    layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGray));
                }
                return true;
            }
        });
    }

}
