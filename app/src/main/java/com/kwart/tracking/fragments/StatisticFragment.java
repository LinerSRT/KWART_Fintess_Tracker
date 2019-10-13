package com.kwart.tracking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kwart.tracking.R;
import com.kwart.tracking.utils.workout.WorkoutFileManager;
import com.kwart.tracking.utils.workout.WorkoutItem;

import java.util.ArrayList;
import java.util.List;


public class StatisticFragment extends Fragment {
    private WorkoutFileManager workoutFileManager;
    private TextView totalWorkouts;
    private TextView totalWorkoutsTime;
    private TextView totalDistance;
    private TextView totalCalories;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.statistic_fragment, container, false);
        workoutFileManager = new WorkoutFileManager(getContext());
        totalWorkouts = view.findViewById(R.id.statistic_total_workouts);
        totalWorkoutsTime = view.findViewById(R.id.statistic_total_workouts_time);
        totalDistance = view.findViewById(R.id.statistic_total_distance);
        totalCalories = view.findViewById(R.id.statistic_total_calories);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(recyclerUpdateReceiver, new IntentFilter("NEED_UPDATE_RECYCLER"));
        updateData();
        return view;
    }

    private static String getSringTime(long hours, long minutes, long seconds){
        StringBuilder stringBuilder = new StringBuilder();
        if(hours < 10){
            stringBuilder.append("0"+hours);
        } else {
            stringBuilder.append(hours);
        }
        stringBuilder.append(":");
        if(minutes < 10){
            stringBuilder.append("0"+minutes);
        } else {
            stringBuilder.append(minutes);
        }
        stringBuilder.append(":");
        if(seconds < 10){
            stringBuilder.append("0"+seconds);
        } else {
            stringBuilder.append(seconds);
        }
        return stringBuilder.toString();
    }

    private void updateData(){
        float totalDistanceW = 0;
        float totalCaloriesW = 0;
        int totalTimeInSec = 0;
        if(workoutFileManager.getWorkoutCount() != 0){
            Log.d("StatTAG", "Workouts total: "+workoutFileManager.getWorkoutCount());
            for(String worckoutDate:workoutFileManager.getWorkoutDates()){
                List<WorkoutItem> workoutItemList = workoutFileManager.getWorkoutData(worckoutDate);
                totalDistanceW += workoutItemList.get(workoutItemList.size()-1).getDistance();
                totalCaloriesW += workoutItemList.get(workoutItemList.size()-1).getCalories();
                totalTimeInSec += workoutItemList.get(workoutItemList.size()-1).getWorckoutTimeRun();
            }
        }
        long hours = totalTimeInSec / 3600;
        long minutes = (totalTimeInSec % 3600) / 60;
        long seconds = totalTimeInSec % 60;
        totalWorkouts.setText(String.valueOf(workoutFileManager.getWorkoutCount()));
        totalCalories.setText(String.valueOf(totalCaloriesW));
        totalDistance.setText(String.valueOf(totalDistanceW));
        totalWorkoutsTime.setText(getSringTime(hours, minutes,seconds));
    }


    private BroadcastReceiver recyclerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }
    };

}
