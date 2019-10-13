package com.kwart.tracking.utils.workout;

public class WorkoutMapPath {
    private double latitude, longitude;

    public WorkoutMapPath(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Longitude: "+longitude+" Latitude: "+latitude;
    }
}
