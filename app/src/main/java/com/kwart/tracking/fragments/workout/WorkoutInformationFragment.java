package com.kwart.tracking.fragments.workout;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kwart.tracking.R;
import com.kwart.tracking.fragments.StatisticFragment;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.PreferenceManager;
import com.kwart.tracking.utils.workout.WorkoutFileManager;
import com.kwart.tracking.utils.workout.WorkoutItem;
import com.kwart.tracking.utils.workout.WorkoutListener;
import com.kwart.tracking.utils.workout.WorkoutManager;
import com.kwart.tracking.utils.workout.WorkoutMapManager;
import com.kwart.tracking.utils.workout.WorkoutMapPath;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class WorkoutInformationFragment extends Fragment {


    private PreferenceManager preferenceManager;
    public static WorkoutManager workoutManager;
    private WorkoutMapManager workoutMapManager;
    private WorkoutFileManager workoutFileManager;

    private TextView currentTimeView, maxSpeedView, distanceView, avgSpeedView, caloriesView, heartrateView, stepsView;
    private Chronometer elapsedTimeView;
    private long timeWhenStopped = 0;
    private boolean elapsedViewVisible = true;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;


    private List<WorkoutMapPath> workoutMapPaths;
    private List<WorkoutItem> workoutItemList;

    private String workoutDateTime;
    private String workoutDataDateTime;


    @Override
    public void onDestroy() {
        try {
            getContext().unregisterReceiver(timeChangedReceiver);
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.workout_information_fragment, container, false);
        preferenceManager = PreferenceManager.getInstance(getContext());
        currentTimeView = view.findViewById(R.id.workout_current_time);
        elapsedTimeView = view.findViewById(R.id.workout_elapsed_time);
        maxSpeedView = view.findViewById(R.id.workout_max_speed);
        distanceView = view.findViewById(R.id.workout_distance);
        avgSpeedView = view.findViewById(R.id.workout_avg_speed);
        caloriesView = view.findViewById(R.id.workout_calories);
        heartrateView = view.findViewById(R.id.workout_heartrate);
        stepsView = view.findViewById(R.id.workout_steps);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
        String currentDateandTime = time_format.format(new Date());
        currentTimeView.setText(currentDateandTime);
        getContext().registerReceiver(timeChangedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        workoutMapManager = WorkoutMapFragment.getWorkoutMapManager();
        workoutFileManager = new WorkoutFileManager(getContext());
        workoutManager = new WorkoutManager(getContext(), new WorkoutListener() {
            @Override
            public void dataChanged(WorkoutItem workoutItem) {
                heartrateView.setText(String.valueOf(workoutItem.getAvgPulse()));
                caloriesView.setText(String.valueOf(workoutItem.getCalories()));
                distanceView.setText(String.valueOf(workoutItem.getDistance()));
                stepsView.setText(String.valueOf(workoutItem.getStepCount()));
                avgSpeedView.setText(String.valueOf(workoutItem.getAvgSpeed()));
                maxSpeedView.setText(String.valueOf(workoutItem.getMaxSpeed()));

                WorkoutItem toData = new WorkoutItem();
                toData.setWorkoutType(workoutItem.getWorkoutType());
                toData.setAvgPulse(workoutItem.getAvgPulse());
                toData.setAvgSpeed(workoutItem.getAvgSpeed());
                toData.setCalories(workoutItem.getCalories());
                toData.setDistance(workoutItem.getDistance());
                toData.setLatitude(workoutItem.getLatitude());
                toData.setLongtitude(workoutItem.getLongtitude());
                toData.setStepCount(workoutItem.getStepCount());
                toData.setStepPerSec(workoutItem.getStepPerSec());
                toData.setMinPulse(workoutItem.getMinPulse());
                toData.setMaxPulse(workoutItem.getMaxPulse());
                toData.setAvgPulse(workoutItem.getAvgPulse());
                toData.setMaxSpeed(workoutItem.getMaxSpeed());
                toData.setWorckoutTimeRun(workoutItem.getWorckoutTimeRun());
                toData.setDate(workoutDataDateTime);
                workoutItemList.add(toData);
            }

            @Override
            public void onStart() {
                elapsedTimeView.start();
                workoutMapPaths = new ArrayList<>();
                workoutItemList = new ArrayList<>();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("HH_mm_dd_MM_yyyy");
                workoutDateTime = time_format.format(new Date());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm\ndd.MM.yyyy");
                workoutDataDateTime = format.format(new Date());
            }

            @Override
            public void onStop(WorkoutItem workoutItem) {
                elapsedTimeView.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                workoutItemList.get(workoutItemList.size()-1).setFilename(workoutDateTime);
                workoutFileManager.saveWorkout(workoutItemList, workoutDateTime);
                elapsedTimeView.stop();
                Intent intent = new Intent("NEED_UPDATE_RECYCLER");
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }

            @Override
            public void onPause() {
                timeWhenStopped = elapsedTimeView.getBase() - SystemClock.elapsedRealtime();
                elapsedTimeView.stop();
                mTimer = new Timer();   //recreate new
                mTimer.scheduleAtFixedRate(new blinkTimer(), 0, 500);
            }

            @Override
            public void onResume() {
                elapsedTimeView.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                elapsedTimeView.start();
                if(mTimer != null)
                    mTimer.cancel();
                    elapsedTimeView.setVisibility(View.VISIBLE);
                    elapsedViewVisible = true;
            }

            @Override
            public void permissionAccessState(boolean isGranted) {

            }

            @Override
            public void onAccuracyChanged(float accuracy) {

            }

            @Override
            public void onLatLonChanged(double lat, double lon) {
                    if(preferenceManager.getBoolean(Constants.SETTINGS_REALTIME_MAP_KEY, false)){
                        if(workoutMapManager != null){
                            if(workoutMapManager.isMapInitialised()) {
                                workoutMapPaths.add(new WorkoutMapPath(lat, lon));
                                workoutFileManager.addMapPath(workoutMapPaths, workoutDateTime);
                                workoutMapManager.drawPath(workoutMapPaths.get(workoutMapPaths.size()-1), Color.GREEN, 4);
                            }
                        }
                    }
            }

            @Override
            public void onAltitudeChanged(double altitude) {

            }

            @Override
            public void avilableSatelites(int count) {

            }

            @Override
            public void onSpeedChanged(float speed) {

            }

            @Override
            public void debug(String data) {

            }
        });
        workoutManager.setGPSUsing(preferenceManager.getBoolean(Constants.SETTINGS_USE_GPS_KEY, false), LocationManager.GPS_PROVIDER);
        workoutManager.startManager(Objects.requireNonNull(getActivity()).getIntent().getIntExtra("mode", 0));
        return view;
    }

    public static WorkoutManager getWorkoutManager(){
        return workoutManager;
    }

    private class blinkTimer extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(elapsedViewVisible){
                        elapsedTimeView.setVisibility(View.INVISIBLE);
                        elapsedViewVisible = false;
                    } else {
                        elapsedTimeView.setVisibility(View.VISIBLE);
                        elapsedViewVisible = true;
                    }
                }
            });

        }
    }
    private BroadcastReceiver timeChangedReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0)
            {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
                String currentDateandTime = time_format.format(new Date());
                currentTimeView.setText(currentDateandTime);
            }

        }
    };
}
