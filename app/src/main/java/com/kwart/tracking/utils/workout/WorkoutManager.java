package com.kwart.tracking.utils.workout;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.kwart.tracking.sensor.SensorUtil;
import com.kwart.tracking.utils.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkoutManager implements LocationListener, SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private Sensor stepSensor, heartRateSensor;
    private WorkoutListener workoutListener;
    private WorkoutItem workoutItem;

    private Handler mainThread = new Handler();
    private Runnable runnableThread;
    boolean stopMainThread = false;


    private boolean SR_havePedometerSensor;
    private boolean SR_isRunning = false;
    private boolean SR_isPaused = false;
    private int SR_stepCount, SR_avgHeartRate, SR_minHeartRate, SR_maxHeartRate, SR_stepPerSec;
    private float distance, calories;

    private List<Float> speedList;

    private List<Integer> SR_pulseList;

    private float userStepSize;
    private int userWeight;


    public WorkoutManager(Context context, final WorkoutListener workoutListener){
        this.context = context;
        userStepSize = WorkOutHelper.getUserStepSize(context);
        userWeight = WorkOutHelper.getUserWeight(context);
        SR_havePedometerSensor = SensorUtil.hasPedometerSensor(context);
        workoutItem = new WorkoutItem();
        runnableThread = new Runnable() {
            @Override
            public void run() {
                workoutItem.setStepPerSec(SR_stepPerSec);
                float maxSpeed = calculateSpeedBySensor(SR_stepPerSec);
                if(workoutItem.getMaxSpeed() < maxSpeed){
                    workoutItem.setMaxSpeed(maxSpeed);
                }
                SR_stepPerSec = 0;
                notifyListener();
                if (!stopMainThread) {
                    mainThread.postDelayed(this, 1000);
                }
            }
        };

        this.workoutListener = workoutListener;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(SR_havePedometerSensor){
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }


    public void startSensorRecognition(int workoutType){
        if (!SR_isRunning && !SR_isPaused) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            SR_pulseList = new ArrayList<>();
            speedList = new ArrayList<>();
            SR_isRunning = true;
            SR_isPaused = false;
            SR_avgHeartRate = 0;
            SR_minHeartRate = 0;
            SR_maxHeartRate = 0;
            SR_stepCount = -1;
            SR_stepPerSec = -1;
            workoutItem.setWorkoutType(workoutType);
            stopMainThread = false;
            mainThread.post(runnableThread);
            workoutListener.onStart();
        }
    }

    public void stopSensorRecognition(){
        if(SR_isRunning) {
            sensorManager.unregisterListener(this);
            stopMainThread = true;
            workoutListener.onStop();
        }
    }

    public void pauseSensorRecognition(){
        if(SR_isRunning) {
            SR_isPaused = true;
            sensorManager.unregisterListener(this, heartRateSensor);
            workoutListener.onPause();
            stopMainThread = true;
        }
    }

    public void resumeSensorRecognition(){
        if(SR_isRunning) {
            SR_isPaused = false;
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
            workoutListener.onResume();
            stopMainThread = false;
            mainThread.post(runnableThread);
        }
    }







    private void processSensorData(SensorEvent sensorEvent){
            if(sensorEvent.sensor == heartRateSensor) {
                if(SensorManager.SENSOR_STATUS_NO_CONTACT != sensorEvent.accuracy) {
                    int sensorValue = (int) sensorEvent.values[0];
                    if (sensorValue != 0) {
                        if (SR_minHeartRate == 0) {
                            SR_minHeartRate = sensorValue;
                        } else if (sensorValue < SR_minHeartRate) {
                            SR_minHeartRate = sensorValue;
                        }
                        if (SR_maxHeartRate == 0) {
                            SR_maxHeartRate = sensorValue;
                        } else if (sensorValue > SR_maxHeartRate) {
                            SR_maxHeartRate = sensorValue;
                        }
                        SR_pulseList.add(sensorValue);
                        if (SR_pulseList.size() > 2) {
                            int average = 0;
                            for (int av_pulse : SR_pulseList) {
                                average += av_pulse;
                                SR_avgHeartRate = average / SR_pulseList.size();
                            }
                        }
                        workoutItem.setAvgPulse(SR_avgHeartRate);
                        workoutItem.setMaxPulse(SR_maxHeartRate);
                        workoutItem.setMinPulse(SR_minHeartRate);
                    }
                }
            }
        if(sensorEvent.sensor == stepSensor){
            SR_stepCount++;
            SR_stepPerSec++;
            workoutItem.setStepCount(SR_stepCount);
            calculateCalories(SR_stepCount);
            calculateDistanceBySensor(SR_stepCount);
        }
    }

    private void calculateDistanceBySensor(int stepCount){
        distance = (((float)stepCount * userStepSize))/1000; //im meter
        workoutItem.setDistance(roundFloat(distance, 2));
    }

    private void calculateDistanceByGPS(){
        distance = 0;
        workoutItem.setDistance(distance);
    }

    private float calculateSpeedBySensor(int stepPerSecond){
        float kmH = roundFloat((userStepSize*stepPerSecond)*3.6f, 2);
        speedList.add(kmH);
        int itemsCount = 0;
        float tmpSpeed = 0;
        for(float item:speedList){
            itemsCount++;
            tmpSpeed = tmpSpeed+item;
        }
        float averageSpeed = tmpSpeed/itemsCount;
        workoutItem.setAvgSpeed(roundFloat(averageSpeed, 2));
        return kmH;
    }

    private void calculateCalories(int stepCount){
        calories = (float)((stepCount*userStepSize)*0.5*userWeight)/1000;
        workoutItem.setCalories(roundFloat(calories, 1));
    }














    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(!SR_isPaused)
            processSensorData(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void notifyListener(){
        workoutListener.dataChanged(workoutItem);
    }

    private float roundFloat(double value, int decimalPlaces){
        double shift = Math.pow(10,decimalPlaces);
        return Float.parseFloat(Double.toString(Math.round(value*shift)/shift));
    }
}
