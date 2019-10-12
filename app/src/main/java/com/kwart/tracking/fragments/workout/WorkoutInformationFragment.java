package com.kwart.tracking.fragments.workout;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.kwart.tracking.R;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.PreferenceManager;
import com.kwart.tracking.utils.workout.WorkoutItem;
import com.kwart.tracking.utils.workout.WorkoutListener;
import com.kwart.tracking.utils.workout.WorkoutManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class WorkoutInformationFragment extends Fragment {
    private PreferenceManager preferenceManager;
    public static WorkoutManager workoutManager;

    private TextView currentTimeView, maxSpeedView, distanceView, avgSpeedView, caloriesView, heartrateView, stepsView;
    private Chronometer elapsedTimeView;
    private long timeWhenStopped = 0;
    private boolean elapsedViewVisible = true;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;


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
        workoutManager = new WorkoutManager(getContext(), new WorkoutListener() {
            @Override
            public void dataChanged(WorkoutItem workoutItem) {
                heartrateView.setText(String.valueOf(workoutItem.getAvgPulse()));
                caloriesView.setText(String.valueOf(workoutItem.getCalories()));
                distanceView.setText(String.valueOf(workoutItem.getDistance()));
                stepsView.setText(String.valueOf(workoutItem.getStepCount()));
                avgSpeedView.setText(String.valueOf(workoutItem.getAvgSpeed()));
                maxSpeedView.setText(String.valueOf(workoutItem.getMaxSpeed()));
                //Log.d(Constants.APP_TAG, workoutItem.toString());
            }

            @Override
            public void onStart() {
                elapsedTimeView.start();
            }

            @Override
            public void onStop() {
                elapsedTimeView.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                elapsedTimeView.stop();
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
