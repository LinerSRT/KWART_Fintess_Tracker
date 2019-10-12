package com.kwart.tracking.utils.workout;

public interface WorkoutListener {
    void dataChanged(WorkoutItem workoutItem);
    void onStart();
    void onStop();
    void onPause();
    void onResume();

    void permissionAccessState(boolean isGranted);
    void onAccuracyChanged(float accuracy);
    void onLatLonChanged(double lat, double lon);
    void onAltitudeChanged(double altitude);
    void avilableSatelites(int count);
    void onSpeedChanged(float speed);
    void debug(String data);
}
