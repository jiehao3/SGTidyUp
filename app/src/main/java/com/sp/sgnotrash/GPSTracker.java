package com.sp.sgnotrash;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.Manifest;



import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class GPSTracker extends Service implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int GPS_PERMISSION_REQUEST_CODE = 1001;
    private Context mContext = null;
    boolean canGetlocation = false;

    protected LocationManager locationManager;

    Location location;
    double latitude;
    double longitude;
    public GPSTracker() {
        getLocation();
    }
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    private void getLocation() {
        this.canGetlocation = false;
        int permissionState1 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location!=null){
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
            this.canGetlocation = true;
        }
        else{
            ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_REQUEST_CODE)   ;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        getLocation();
    }
    @Override
    public void onProviderDisabled(String provider){
    }
    @Override
    public void onProviderEnabled(String provider){
    }
    @Override
    public void onStatusChanged(String provider, int status , Bundle extras){
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GPS_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                getLocation();
            }
            else{
                ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_REQUEST_CODE);
            }
        }
    }
    public Double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();

        }
        return latitude;
    }
    public Double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }
    public boolean canGetlocation(){
        getLocation();
        return canGetlocation;

    }
    public void stopUsingGPS(){
        if( location != null ){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
}