package com.kwart.tracking.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.kwart.tracking.R;
import com.kwart.tracking.fragments.workout.WorkoutInformationFragment;
import com.kwart.tracking.fragments.workout.WorkoutMapFragment;
import com.kwart.tracking.fragments.workout.WorkoutStopFragment;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.FragmentPageAdapter;
import com.kwart.tracking.utils.PreferenceManager;
import com.kwart.tracking.utils.ThemeManager;
import com.kwart.tracking.views.DialogView;

public class WorkoutActivity extends FragmentActivity {
    private PreferenceManager preferenceManager;
    private ViewPager viewPager;
    private DialogView dialogView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager themeManager = new ThemeManager(this);
        themeManager.initTheme(this);
        setContentView(R.layout.workout_composite_fragment);
        preferenceManager = PreferenceManager.getInstance(this);

        setupPager();
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
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2){
                    dialogView = new DialogView(WorkoutActivity.this);
                    dialogView.createNewDialog();
                    dialogView.setDialogTitle("Внимание!");
                    dialogView.setDialogText("Что бы выйти из режима\n просмотра карты, нажмите кнопку \"Назад\"");
                    dialogView.setCancel(View.GONE, " ", null);
                    dialogView.setOk(View.VISIBLE, "Ок", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogView.close();
                        }
                    });
                    dialogView.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        viewPager.setCurrentItem(1);
    }
}
