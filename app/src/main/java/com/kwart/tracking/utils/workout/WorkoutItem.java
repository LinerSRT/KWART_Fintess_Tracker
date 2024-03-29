package com.kwart.tracking.utils.workout;

public class WorkoutItem {
    private double latitude, longtitude;
    private int stepCount, stepPerSec, avgPulse, minPulse, maxPulse, workoutType, worckoutTimeRun;
    private float distance, maxSpeed, avgSpeed, calories;
    private String date, filename;

    //GPS
    private boolean isFirstGPSLocation;

    public WorkoutItem(){
        this.workoutType = 0;
        this.avgPulse = 0;
        this.avgSpeed = 0;
        this.calories = 0;
        this.distance = 0;
        this.latitude = 0;
        this.longtitude = 0;
        this.stepCount = 0;
        this.stepPerSec = 0;
        this.minPulse = 0;
        this.maxPulse = 0;
        this.maxPulse = 0;
        this.worckoutTimeRun = 0;
        this.date = "";
        this.filename = "";
        this.isFirstGPSLocation = true;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setWorkoutType(int workoutType) {
        this.workoutType = workoutType;
    }

    public void setAvgPulse(int avgPulse) {
        this.avgPulse = avgPulse;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public void setMaxPulse(int maxPulse) {
        this.maxPulse = maxPulse;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public void setMinPulse(int minPulse) {
        this.minPulse = minPulse;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public void setStepPerSec(int stepPerSec) {
        this.stepPerSec = stepPerSec;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public int getWorkoutType() {
        return workoutType;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public float getCalories() {
        return calories;
    }

    public float getDistance() {
        return distance;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public int getAvgPulse() {
        return avgPulse;
    }

    public int getMaxPulse() {
        return maxPulse;
    }

    public int getMinPulse() {
        return minPulse;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getStepPerSec() {
        return stepPerSec;
    }

    public boolean isFirstGPSLocation() {
        return isFirstGPSLocation;
    }

    public void setFirstGPSLocation(boolean firstGPSLocation) {
        isFirstGPSLocation = firstGPSLocation;
    }

    public void addDistance(float newD){
        this.distance = distance + newD;
    }

    public void setWorckoutTimeRun(int worckoutTimeRun) {
        this.worckoutTimeRun = worckoutTimeRun;
    }

    public int getWorckoutTimeRun() {
        return worckoutTimeRun;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getWorkoutStringType(){
        switch (getWorkoutType()){
            case 0:
                return "Бег";
            case 1:
                return "Ходьба";
            case 2:
                return "Велопоездка";
            case 3:
                return "Марафон";
            case 4:
                return "Беговая дорожка";
                default:
                    return "Бег";
        }
    }

    @Override
    public String toString() {
        return "WorkoutItem\n" +
                "Type: " + getWorkoutStringType()+"\n"+
                "Steps: "+getStepCount()+"\n" +
                "Distance: "+getDistance()+"\n" +
                "Calories: "+getCalories()+"\n" +
                "HeartRate (AVG): "+getAvgPulse()+"\n" +
                "HeartRate (MIN): "+getMinPulse()+"\n" +
                "HeartRate (MAX): "+getMaxPulse()+"\n" +
                "Speed (AVG): "+getAvgSpeed()+"\n" +
                "Speed (MAX): "+getMaxSpeed()+"\n" +
                "GPS (lat|lon): "+getLatitude()+"|"+getLongtitude()+"\n";
    }
}
