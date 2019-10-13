package com.kwart.tracking.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kwart.tracking.R;
import com.kwart.tracking.fragments.HistoryFragment;
import com.kwart.tracking.fragments.SettingsFragment;
import com.kwart.tracking.fragments.StatisticFragment;
import com.kwart.tracking.fragments.TrainFragment;
import com.kwart.tracking.utils.ColorUtil;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.FragmentPageAdapter;
import com.kwart.tracking.utils.PreferenceManager;
import com.kwart.tracking.utils.ThemeManager;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private PreferenceManager preferenceManager;
    private ViewPager viewPager;
    private ImageButton statBtn;
    private ImageButton historyBtn;
    private ImageButton trainBtn;
    private ImageButton settingsBtn;
    private TextView viewpagerDescription;
    private Drawable coloredBG, grayBG;
    private String[] pagesDescriptions = new String[]{
            "Статистика",
            "История",
            "Тренировка",
            "Настройки"
    };

    private boolean allGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager themeManager = new ThemeManager(this);
        themeManager.initTheme(this);
        preferenceManager = PreferenceManager.getInstance(this);
        setContentView(R.layout.activity_main);
        String[] permissions = new String[]{Manifest.permission.BODY_SENSORS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                allGranted = true;
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                for(String item:deniedPermissions){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{item}, 111);
                }
            }
        });

        statBtn = findViewById(R.id.statBtn);
        historyBtn = findViewById(R.id.historyBtn);
        trainBtn = findViewById(R.id.trainBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        viewpagerDescription = findViewById(R.id.viewpager_description);
        coloredBG = getDrawable(R.drawable.background_shape);
        assert coloredBG != null;
        coloredBG.setTint(ColorUtil.getAttrColor(MainActivity.this,R.attr.colorPrimary));
        grayBG = getDrawable(R.drawable.background_shape);
        assert grayBG != null;
        grayBG.setTint(Color.parseColor("#777777"));
        setUpViewPager();
        statBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });
        trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(3);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    private void setUpViewPager() {
        viewPager = findViewById(R.id.viewpager);
        FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager(), this);
        adapter.addPage(StatisticFragment.class);
        adapter.addPage(HistoryFragment.class);
        adapter.addPage(TrainFragment.class);
        adapter.addPage(SettingsFragment.class);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(2);
        displayPosition(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                displayPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void displayPosition(int pos){
        switch (pos){
            case 0:
                statBtn.setBackground(coloredBG);
                historyBtn.setBackground(grayBG);
                trainBtn.setBackground(grayBG);
                settingsBtn.setBackground(grayBG);
                viewpagerDescription.setText(pagesDescriptions[0]);
                break;
            case 1:
                statBtn.setBackground(grayBG);
                historyBtn.setBackground(coloredBG);
                trainBtn.setBackground(grayBG);
                settingsBtn.setBackground(grayBG);
                viewpagerDescription.setText(pagesDescriptions[1]);
                break;
            case 2:
                statBtn.setBackground(grayBG);
                historyBtn.setBackground(grayBG);
                trainBtn.setBackground(coloredBG);
                settingsBtn.setBackground(grayBG);
                viewpagerDescription.setText(pagesDescriptions[2]);
                break;
            case 3:
                statBtn.setBackground(grayBG);
                historyBtn.setBackground(grayBG);
                trainBtn.setBackground(grayBG);
                settingsBtn.setBackground(coloredBG);
                viewpagerDescription.setText(pagesDescriptions[3]);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(preferenceManager.getBoolean(Constants.IS_WORKOUT_RUNNING_KEY, false)){
           Intent i = new Intent(this, WorkoutActivity.class);
           i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
           startActivity(i);
        }
    }
}
