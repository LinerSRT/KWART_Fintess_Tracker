package com.kwart.tracking.location;

import android.location.Location;

public interface GPSInterface {
    void permissionAccessState(boolean isGranted);

    void onAccuracyChanged(float accuracy);
    void onLatLonChanged(double lat, double lon);
    void onAltitudeChanged(double altitude);
    void avilableSatelites(int count);
    void onSpeedChanged(float speed);
    void debug(String data);
}
