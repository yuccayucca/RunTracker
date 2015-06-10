package com.murach.runtracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class RunTrackerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private RunTrackerApp app;
    //private LocationClient locationClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // get app object
        app = (RunTrackerApp) getApplication();
        
        // get location client
        //locationClient = new LocationClient(this, this, this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //locationClient.connect();
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
    
    //**************************************************************
    // Implement ConnectionCallbacks interface
    //****************************************************************
    @Override
    public void onConnected(Bundle dataBundle) {
        /*Location location = locationClient.getLastLocation();
        if (location != null){
            app.getDB().insertLocation(location);
        }*/
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            app.getDB().insertLocation(location);
        }
    }
    
    /*@Override
    public void onDisconnected() {
        if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
    }*/
    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
        Log.i("runapp", "GoogleApiClient connection has been suspend");
    }
    
    //**************************************************************
    // Implement OnConnectionFailedListener
    //****************************************************************
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed! " + 
            "Please check your settings and try again.", 
            Toast.LENGTH_SHORT).show();
    }
    
    //**************************************************************
    // Implement LocationListener
    //****************************************************************
    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            app.getDB().insertLocation(location);    
        }
    }
}