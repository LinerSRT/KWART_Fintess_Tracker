package com.kwart.tracking.utils.workout;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class WorkoutMapManager implements OnMapReadyCallback {

    private Context context;
    private WorkoutMapInterface mapInterface;

    private GoogleMap googleMap;
    private boolean mapInitialised = false;
    private PolylineOptions polylineOptions;

    public WorkoutMapManager(Context context, WorkoutMapInterface mapInterface){
        this.context = context;
        this.mapInterface = mapInterface;
    }

    public void initMapManager(SupportMapFragment mapFragment){
        polylineOptions = new PolylineOptions();
        mapFragment.getMapAsync(this);
    }

    public void setMarker(double lat, double lon, String title){
        LatLng marker = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions().position(marker).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    public void setMapType(int type){
        googleMap.setMapType(type);
    }

    public void drawPath(double lat, double lon, int color, int width){
        polylineOptions.color(color);
        polylineOptions.width(width);
        polylineOptions.visible(true);
        polylineOptions.add(new LatLng(lat, lon));
        googleMap.addPolyline(polylineOptions);
    }

    public void moveCamera(double lat, double lon, int zoomLevel){
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
    }

    public boolean isMapInitialised(){
        return mapInitialised;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapInitialised = true;
        mapInterface.onMapLoaded(googleMap);
    }
}
