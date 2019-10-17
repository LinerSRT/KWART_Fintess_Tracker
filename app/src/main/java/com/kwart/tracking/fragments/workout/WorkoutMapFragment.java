package com.kwart.tracking.fragments.workout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.kwart.tracking.R;
import com.kwart.tracking.utils.workout.WorkoutMapInterface;
import com.kwart.tracking.utils.workout.WorkoutMapManager;


public class WorkoutMapFragment extends Fragment {
    public static WorkoutMapManager workoutMapManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workoutMapManager = new WorkoutMapManager(getContext(), new WorkoutMapInterface() {
            @Override
            public void onMapLoaded(GoogleMap googleMap) {
                workoutMapManager.setMapType(4);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.workout_map_fragment, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        workoutMapManager.initMapManager(map);
        return view;
    }

    public static WorkoutMapManager getWorkoutMapManager(){
        return workoutMapManager;
    }
}
