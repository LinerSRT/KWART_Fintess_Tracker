package com.kwart.tracking.utils.workout;

public interface WorkoutListener {
    void dataChanged(WorkoutItem workoutItem);
    void onStart();
    void onStop();
    void onPause();
    void onResume();
}
