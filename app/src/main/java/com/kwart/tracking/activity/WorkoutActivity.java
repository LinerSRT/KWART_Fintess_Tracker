package com.kwart.tracking.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kwart.tracking.R;
import com.kwart.tracking.fragments.HistoryFragment;
import com.kwart.tracking.fragments.SettingsFragment;
import com.kwart.tracking.fragments.StatisticFragment;
import com.kwart.tracking.fragments.TrainFragment;
import com.kwart.tracking.fragments.workout.WorkoutInformationFragment;
import com.kwart.tracking.fragments.workout.WorkoutMapFragment;
import com.kwart.tracking.fragments.workout.WorkoutStopFragment;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.FragmentPageAdapter;
import com.kwart.tracking.utils.PreferenceManager;
import com.kwart.tracking.utils.ThemeManager;

public class WorkoutActivity extends FragmentActivity {
    private PreferenceManager preferenceManager;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager themeManager = new ThemeManager(this);
        themeManager.initTheme(this);
        setContentView(R.layout.workout_composite_fragment);
        preferenceManager = PreferenceManager.getInstance(this);
        setupPager();
        init();



    }

    private void init(){
        Intent intent = getIntent();
        int workout_type = intent.getIntExtra("mode", 0);
        Log.d("TAGTAG", "Type: "+workout_type);
    }


    private void setupPager(){
        viewPager = findViewById(R.id.workout_viewpager);
        FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager(), this);
        adapter.addPage(WorkoutStopFragment.class);
        adapter.addPage(WorkoutInformationFragment.class);
        if(preferenceManager.getBoolean(Constants.SETTINGS_REALTIME_MAP_KEY, false))
            adapter.addPage(WorkoutMapFragment.class);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {
    }

}
