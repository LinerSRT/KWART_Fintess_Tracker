package com.kwart.tracking.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.kwart.tracking.utils.Constants;

import static android.content.Context.LOCATION_SERVICE;

public class GPSManager {

    private GPSInterface gpsInterface;
    private Context context;
    private Activity activity;

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpsInterface.avilableSatelites(location.getExtras().getInt("satellites"));
            gpsInterface.onAccuracyChanged(location.getAccuracy());
            gpsInterface.onAltitudeChanged(location.getAltitude());
            gpsInterface.onLatLonChanged(location.getLatitude(), location.getLatitude());
            gpsInterface.onSpeedChanged(location.getSpeed());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("GPSDATA:\n");
            stringBuilder.append("Accuracy: "+location.getAccuracy());
            stringBuilder.append("Altitude: "+location.getAltitude());
            stringBuilder.append("LAT-LON: "+location.getLatitude()+"|"+location.getLongitude());
            stringBuilder.append("Speed: "+location.getSpeed());
            stringBuilder.append("Sats: "+location.getExtras().getInt("satellites"));
            gpsInterface.debug(stringBuilder.toString());
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
    };


    private boolean permissionGranted = false;


    private int PERMISSION_RQST_CODE = 9881;
    private int UPDATE_TIMEOUT = 1; //in sec
    private int UPDATE_DISTANCE = 1; // per one meter

    public GPSManager(Context context, Activity activity ,GPSInterface gpsInterface){
        this.context = context;
        this.activity = activity;
        this.gpsInterface = gpsInterface;
        this.locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        checkPermissions();
    }



    @SuppressLint("MissingPermission")
    public void requestLocation(String provider){
        if(permissionGranted) {
            locationManager.removeUpdates(locationListener);
            locationManager.requestLocationUpdates(provider, UPDATE_TIMEOUT * 1000, UPDATE_DISTANCE, locationListener);
        } else {
            Log.e(Constants.APP_TAG, "No permissions!");
        }
    }


    public void stopManager(){
        locationManager.removeUpdates(locationListener);
    }




    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionGranted = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            gpsInterface.permissionAccessState(permissionGranted);
            if(!permissionGranted){
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RQST_CODE);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == PERMISSION_RQST_CODE){
            if(grantResults.length != 0){
                permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                gpsInterface.permissionAccessState(permissionGranted);
            }
        }
    }


}
