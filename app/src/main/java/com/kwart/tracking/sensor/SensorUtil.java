package com.kwart.tracking.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

public class SensorUtil {

    public static boolean hasPedometerSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        List<Sensor> listSensors = sensorManager.getSensorList(Sensor.TYPE_STEP_COUNTER);
        return !listSensors.isEmpty();
    }

    public static boolean hasHeartRateSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        List<Sensor> listSensors = sensorManager.getSensorList(Sensor.TYPE_HEART_RATE);
        return !listSensors.isEmpty();
    }

}
