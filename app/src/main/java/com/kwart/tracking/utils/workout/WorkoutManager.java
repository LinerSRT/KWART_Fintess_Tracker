package com.kwart.tracking.utils.workout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.kwart.tracking.sensor.SensorUtil;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.PreferenceManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkoutManager implements LocationListener, SensorEventListener, GpsStatus.Listener {
    private PreferenceManager preferenceManager;
    private Context context;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private Sensor stepSensor, heartRateSensor;
    private WorkoutListener workoutListener;
    private WorkoutItem workoutItem;

    private Handler mainThread = new Handler();
    private Runnable runnableThread;

    //GPS part
    private boolean gpsManagerEnabled = false;
    private int GPS_UPDATE_TIMEOUT = 1000; //ms (10 sec)
    private int GPS_UPDATE_DISTANCE = 1; // per 3 meter
    private String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
    private boolean isGPSRecognitionRunning = false;
    private Location lastlocation = new Location("last");
    private double currentLon=0 ;
    private double currentLat=0 ;
    private double lastLon = 0;
    private double lastLat = 0;


    //Manager part
    private boolean isManagerRunning = false;
    private boolean isManagerIsPaused = false;
    private boolean isMainThreadStopped = false;

    //Sensor part
    private boolean havePerometer = false;
    private boolean isSensorRecognitionRunning = false;
    private boolean isSensorRecognitionPaused = false;

    private List<Integer> pulseArray;
    private int pulseAVG, pulseMin, pulseMax;

    private float reachedDistance, burnedCalories;
    private List<Float> speedArray;
    private int stepPerSec, stepCount;

    //User part
    private float userStepSize;
    private int userWeight;


    public WorkoutManager(Context context, final WorkoutListener workoutListener){
        this.context = context;
        preferenceManager = PreferenceManager.getInstance(context);
        userStepSize = WorkOutHelper.getUserStepSize(context);
        userWeight = WorkOutHelper.getUserWeight(context);
        havePerometer = SensorUtil.hasPedometerSensor(context);
        workoutItem = new WorkoutItem();
        runnableThread = new Runnable() {
            @Override
            public void run() {
                workoutItem.setStepPerSec(stepPerSec);
                if(!gpsManagerEnabled) {
                    float maxSpeed = calculateSpeedBySensor(stepPerSec);
                    if (workoutItem.getMaxSpeed() < maxSpeed) {
                        workoutItem.setMaxSpeed(maxSpeed);
                    }
                    stepPerSec = 0;
                }
                notifyListener();
                if (!isMainThreadStopped) {
                    mainThread.postDelayed(this, 1000);
                }
            }
        };
        this.workoutListener = workoutListener;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(havePerometer){
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }


    public void setGPSUsing(boolean enabled, String GPS_PROVIDER){
        this.gpsManagerEnabled = enabled;
        this.GPS_PROVIDER = GPS_PROVIDER;
    }

    @SuppressLint("MissingPermission")
    public void startManager(int workoutType){
        if(!isManagerRunning && !isManagerIsPaused) {
            if (gpsManagerEnabled && !isGPSRecognitionRunning) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIMEOUT, GPS_UPDATE_DISTANCE, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATE_TIMEOUT, GPS_UPDATE_DISTANCE,this);
                isGPSRecognitionRunning = true;

            } else if (gpsManagerEnabled){
                locationManager.removeUpdates(this);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIMEOUT, GPS_UPDATE_DISTANCE, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATE_TIMEOUT, GPS_UPDATE_DISTANCE,this);
                isGPSRecognitionRunning = true;
            }

            if (!isSensorRecognitionRunning && !isSensorRecognitionPaused) {
                workoutItem.setWorkoutType(workoutType);
                sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
                pulseArray = new ArrayList<>();
                speedArray = new ArrayList<>();
                pulseAVG = 0;
                pulseMin = 0;
                pulseMax = 0;
                stepCount = -1;
                stepPerSec = -1;
                isManagerRunning = true;
                isManagerIsPaused = false;
                isMainThreadStopped = false;
                isSensorRecognitionRunning = true;
                isSensorRecognitionPaused = false;
                mainThread.post(runnableThread);
                preferenceManager.saveBoolean(Constants.IS_WORKOUT_RUNNING_KEY, true);
                workoutListener.onStart();
            }
        }
        //Log.d(Constants.APP_TAG, "Starting manager... IsRunning: "+isManagerRunning+", IsPaused: "+isManagerIsPaused+"\n" +
        //        "GPS Manager enabled: "+gpsManagerEnabled+"\n" +
        //        "GPS Recognition running: "+isGPSRecognitionRunning+"\n" +
        //        "Sensor recognition running: "+isSensorRecognitionRunning+"\n" +
        //        "Sensor recognition paused: "+isSensorRecognitionPaused);
    }

    public void stopManager(){
        if(gpsManagerEnabled){
            if(isGPSRecognitionRunning){
                locationManager.removeUpdates(this);
                isGPSRecognitionRunning = false;
            }
        }
        if(isSensorRecognitionRunning){
            sensorManager.unregisterListener(this, stepSensor);
            sensorManager.unregisterListener(this, heartRateSensor);
        }
        isMainThreadStopped = true;
        isManagerRunning = false;
        isSensorRecognitionRunning = false;
        isGPSRecognitionRunning = false;
        isSensorRecognitionPaused = false;
        //Log.d(Constants.APP_TAG, "Stopping manager... IsRunning: "+isManagerRunning+", IsPaused: "+isManagerIsPaused+"\n" +
        //        "GPS Manager enabled: "+gpsManagerEnabled+"\n" +
        //        "GPS Recognition running: "+isGPSRecognitionRunning+"\n" +
        //        "Sensor recognition running: "+isSensorRecognitionRunning+"\n" +
        //        "Sensor recognition paused: "+isSensorRecognitionPaused);

        preferenceManager.saveBoolean(Constants.IS_WORKOUT_RUNNING_KEY, false);
        workoutListener.onStop();

    }

    public void pauseManager(){
        if(gpsManagerEnabled) {
            if (isGPSRecognitionRunning) {
                locationManager.removeUpdates(this);
                isGPSRecognitionRunning = false;
            }
        }
        if(isSensorRecognitionRunning && !isSensorRecognitionPaused){
            isSensorRecognitionPaused = true;
            sensorManager.unregisterListener(this);
            isMainThreadStopped = true;
            workoutListener.onPause();
        }
        isManagerIsPaused = true;
        //Log.d(Constants.APP_TAG, "Pause manager... IsRunning: "+isManagerRunning+", IsPaused: "+isManagerIsPaused+"\n" +
        //        "GPS Manager enabled: "+gpsManagerEnabled+"\n" +
        //        "GPS Recognition running: "+isGPSRecognitionRunning+"\n" +
        //        "Sensor recognition running: "+isSensorRecognitionRunning+"\n" +
        //        "Sensor recognition paused: "+isSensorRecognitionPaused);


    }

    @SuppressLint("MissingPermission")
    public void resumeManager(){
        if(gpsManagerEnabled){
            if(!isGPSRecognitionRunning) {
                locationManager.requestLocationUpdates(GPS_PROVIDER, GPS_UPDATE_TIMEOUT, GPS_UPDATE_DISTANCE, this);
                isGPSRecognitionRunning = true;
            }
        }
        if(isSensorRecognitionPaused){
            isSensorRecognitionPaused = false;
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isMainThreadStopped = false;
            mainThread.post(runnableThread);
        }
        isManagerIsPaused = false;
        workoutListener.onResume();
        //Log.d(Constants.APP_TAG, "Resume manager... IsRunning: "+isManagerRunning+", IsPaused: "+isManagerIsPaused+"\n" +
        //        "GPS Manager enabled: "+gpsManagerEnabled+"\n" +
        //        "GPS Recognition running: "+isGPSRecognitionRunning+"\n" +
        //        "Sensor recognition running: "+isSensorRecognitionRunning+"\n" +
        //        "Sensor recognition paused: "+isSensorRecognitionPaused);
    }



    private void processGPSData(Location location){
        workoutListener.avilableSatelites(location.getExtras().getInt("satellites"));
        workoutListener.onLatLonChanged(location.getLatitude(), location.getLongitude());
        calculateSpeedByGPS(location.getSpeed());
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        if (workoutItem.isFirstGPSLocation()){
            lastLat = currentLat;
            lastLon = currentLon;
            workoutItem.setFirstGPSLocation(false);
        }
        lastlocation.setLatitude(lastLat);
        lastlocation.setLongitude(lastLon);
        double distance = lastlocation.distanceTo(location);
        if (location.getAccuracy() < distance){
            workoutItem.addDistance(roundFloat((float)distance/1000, 2));
            lastLat = currentLat;
            lastLon = currentLon;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GPSDATA:\n");
        stringBuilder.append("Accuracy: "+location.getAccuracy()+"\n");
        stringBuilder.append("Altitude: "+location.getAltitude()+"\n");
        stringBuilder.append("LAT-LON: "+location.getLatitude()+"|"+location.getLongitude()+"\n");
        stringBuilder.append("Speed: "+location.getSpeed()+"\n");
        stringBuilder.append("Sats: "+location.getExtras().getInt("satellites")+"\n");
        workoutListener.debug(stringBuilder.toString());
    }


    private void processSensorData(SensorEvent sensorEvent){
            if(sensorEvent.sensor == heartRateSensor) {
                if(SensorManager.SENSOR_STATUS_NO_CONTACT != sensorEvent.accuracy) {
                    int sensorValue = (int) sensorEvent.values[0];
                    if (sensorValue != 0) {
                        if (pulseMin == 0) {
                            pulseMin = sensorValue;
                        } else if (sensorValue < pulseMin) {
                            pulseMin = sensorValue;
                        }
                        if (pulseMax == 0) {
                            pulseMax = sensorValue;
                        } else if (sensorValue > pulseMax) {
                            pulseMax = sensorValue;
                        }
                        pulseArray.add(sensorValue);
                        if (pulseArray.size() > 2) {
                            int average = 0;
                            for (int av_pulse : pulseArray) {
                                average += av_pulse;
                                pulseAVG = average / pulseArray.size();
                            }
                        }
                        workoutItem.setAvgPulse(pulseAVG);
                        workoutItem.setMaxPulse(pulseMax);
                        workoutItem.setMinPulse(pulseMin);
                    }
                }
            }
        if(sensorEvent.sensor == stepSensor){
            stepCount++;
            stepPerSec++;
            workoutItem.setStepCount(stepCount);
            calculateCalories(stepCount);
            if(!gpsManagerEnabled)
                calculateDistanceBySensor(stepCount);
        }
    }

    private void calculateDistanceBySensor(int stepCount){
        reachedDistance = (((float)stepCount * userStepSize))/1000; //im meter
        workoutItem.setDistance(roundFloat(reachedDistance, 2));
    }

    private void calculateDistanceByGPS(){
        reachedDistance = 0;
        workoutItem.setDistance(reachedDistance);
    }

    private float calculateSpeedBySensor(int stepPerSecond){
        float kmH = roundFloat((userStepSize*stepPerSecond)*3.6f, 2);
        speedArray.add(kmH);
        int itemsCount = 0;
        float tmpSpeed = 0;
        for(float item:speedArray){
            itemsCount++;
            tmpSpeed = tmpSpeed+item;
        }
        float averageSpeed = tmpSpeed/itemsCount;
        workoutItem.setAvgSpeed(roundFloat(averageSpeed, 2));
        return kmH;
    }

    private void calculateSpeedByGPS(float currSpeed){
        speedArray.add(currSpeed);
        int itemsCount = 0;
        float tmpSpeed = 0;
        for(float item:speedArray){
            itemsCount++;
            tmpSpeed = tmpSpeed+item;
        }
        float averageSpeed = tmpSpeed/itemsCount;
        workoutItem.setAvgSpeed(roundFloat(averageSpeed, 2));

        if (workoutItem.getMaxSpeed() < currSpeed) {
            workoutItem.setMaxSpeed(currSpeed);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(!isSensorRecognitionPaused)
            processSensorData(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        processGPSData(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onProviderEnabled(String s) {
        processGPSData(locationManager.getLastKnownLocation(s));
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void notifyListener(){
        workoutListener.dataChanged(workoutItem);
    }

    private void calculateCalories(int stepCount){
        burnedCalories = (float)((stepCount*userStepSize)*0.5*userWeight)/1000;
        workoutItem.setCalories(roundFloat(burnedCalories, 1));
    }

    private float roundFloat(double value, int decimalPlaces){
        double shift = Math.pow(10,decimalPlaces);
        return Float.parseFloat(Double.toString(Math.round(value*shift)/shift));
    }

    @Override
    public void onGpsStatusChanged(int i) {

    }
}
