package com.kwart.tracking.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.kwart.tracking.R;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.PreferenceManager;


public class SettingsFragment extends Fragment {
    private PreferenceManager preferenceManager;
    private CheckBox useGPS, showRealTimeMap, autoSave, backgroundWork, alwaysOn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.settings_fragment, container, false);
        useGPS = view.findViewById(R.id.settings_use_gps);
        showRealTimeMap = view.findViewById(R.id.settings_show_realtime_map);
        autoSave = view.findViewById(R.id.settings_autosave);
        backgroundWork = view.findViewById(R.id.settings_work_in_background);
        alwaysOn = view.findViewById(R.id.settings_always_on_screen);
        preferenceManager = PreferenceManager.getInstance(getContext());
        initValues();
        useGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager.saveBoolean(Constants.SETTINGS_USE_GPS_KEY, useGPS.isChecked());
                initValues();
            }
        });
        showRealTimeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager.saveBoolean(Constants.SETTINGS_REALTIME_MAP_KEY, showRealTimeMap.isChecked());
                initValues();
            }
        });
        autoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager.saveBoolean(Constants.SETTINGS_AUTOSAVE_KEY, autoSave.isChecked());
                initValues();
            }
        });
        backgroundWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager.saveBoolean(Constants.SETTINGS_BACKGROUND_WORK_KEY, backgroundWork.isChecked());
                initValues();
            }
        });
        alwaysOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager.saveBoolean(Constants.SETTINGS_ALWAYS_ON_KEY, alwaysOn.isChecked());
                initValues();
            }
        });
        return view;
    }

    private void initValues(){
        useGPS.setChecked(preferenceManager.getBoolean(Constants.SETTINGS_USE_GPS_KEY, true));
        showRealTimeMap.setChecked(preferenceManager.getBoolean(Constants.SETTINGS_REALTIME_MAP_KEY, true));
        autoSave.setChecked(preferenceManager.getBoolean(Constants.SETTINGS_AUTOSAVE_KEY, false));
        backgroundWork.setChecked(preferenceManager.getBoolean(Constants.SETTINGS_BACKGROUND_WORK_KEY, false));
        alwaysOn.setChecked(preferenceManager.getBoolean(Constants.SETTINGS_ALWAYS_ON_KEY, false));
    }
}
