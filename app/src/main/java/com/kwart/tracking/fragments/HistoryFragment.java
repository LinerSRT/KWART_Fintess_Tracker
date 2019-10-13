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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kwart.tracking.R;
import com.kwart.tracking.utils.workout.WorkoutFileManager;
import com.kwart.tracking.utils.workout.WorkoutItem;
import com.kwart.tracking.utils.workout.WorkoutRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {
    private RecyclerView workoutRecycler;
    private List<List<WorkoutItem>> workoutList;
    private WorkoutFileManager workoutFileManager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.history_fragment, container, false);
        workoutRecycler = view.findViewById(R.id.workout_recycler);
        workoutFileManager = new WorkoutFileManager(getContext());
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(recyclerUpdateReceiver, new IntentFilter("NEED_UPDATE_RECYCLER"));


        updateRecycler();
        return view;
    }

    public void updateRecycler(){
        workoutList = new ArrayList<>();
        if(workoutFileManager.getWorkoutCount() != 0){
            for(String worckoutDate:workoutFileManager.getWorkoutDates()){
                List<WorkoutItem> workoutItemList = workoutFileManager.getWorkoutData(worckoutDate);
                workoutList.add(workoutItemList);
            }
        }
        if(workoutList.size() != 0){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            workoutRecycler.setLayoutManager(layoutManager);
            layoutManager.setSmoothScrollbarEnabled(true);
            WorkoutRecyclerViewAdapter workoutRecyclerViewAdapter = new WorkoutRecyclerViewAdapter(workoutList, getContext(), getActivity());
            workoutRecycler.setAdapter(workoutRecyclerViewAdapter);
        }
    }

    private BroadcastReceiver recyclerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateRecycler();
        }
    };

}
