package com.murach.runtracker;

import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RunMapActivity extends FragmentActivity implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    
    //private LocationClient locationClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private List<Location> locationList;
    
    private GoogleMap map;
    
    private Button stopwatchButton;
    private Intent stopwatchIntent;

    private RunTrackerApp app;
    
    //**************************************************************
    // Activity lifecycle methods
    //****************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_map);
        
        stopwatchButton = (Button) findViewById(R.id.viewStopwatchButton);
        stopwatchButton.setOnClickListener(this);        
        stopwatchIntent = new Intent(getApplicationContext(), 
            StopwatchActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        app = (RunTrackerApp) getApplication();
        app.enableGPS(this);
        
        //locationClient = new LocationClient(this, this, this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        // if GoogleMap object is not already available, get it
        if (map == null) {
            FragmentManager manager = getSupportFragmentManager();
            SupportMapFragment fragment = 
                    (SupportMapFragment) manager.findFragmentById(R.id.map);
            map = fragment.getMap();
        }
        
        // if GoogleMap object is available, configure it
        if (map != null) { 
            map.getUiSettings().setZoomControlsEnabled(true);
        }

        mGoogleApiClient.connect();
       // updateMap();
    }
    
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        
        super.onStop();
    }
    
    //**************************************************************
    // Private methods
    //****************************************************************
    private void updateMap(){
        if (mGoogleApiClient.isConnected()){
            setCurrentLocationMarker();
        }
        displayRun();
    }
    
    private void setCurrentLocationMarker(){
        if (map != null) { 
            // get current location
            //Location location = locationClient.getLastLocation();
            Location location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            
            if (location != null) {
                // zoom in on current location
                map.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                       new CameraPosition.Builder()
                           .target(new LatLng(location.getLatitude(), 
                                              location.getLongitude()))
                           .zoom(16.5f)
                           .bearing(0)
                           .tilt(25)
                           .build()));
                
                // add a marker for the current location
                map.clear();      // clear old marker(s)
                map.addMarker(    // add new marker
                    new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), 
                                             location.getLongitude()))
                        .title("You are here"));
            }
        }
    }
    
    private void displayRun(){
        if (map != null) {
            locationList = app.getDB().getLocations();

            PolylineOptions polyline = new PolylineOptions();
            Polyline route;
            if (locationList.size() > 0) {
                for (Location l : locationList) {
                    LatLng point = new LatLng(
                        l.getLatitude(), l.getLongitude()); 
                    polyline.add(point);
                }
            }
            route = map.addPolyline(polyline);

            /*PolylineOptions rectOptions = new PolylineOptions()
                    .add(new LatLng(37.570327, -122.333907))
                    .add(new LatLng(37.573150, -122.336783))  // North of the previous point, but at the same longitude
                    .add(new LatLng(37.574400, -122.335002))  // Same latitude, and 30km to the west
                    .add(new LatLng(37.570905, -122.331118))  // Same longitude, and 16km to the south
                    .add(new LatLng(37.569587, -122.333070))
                    .add(new LatLng(37.570327, -122.333907))
                    .color(Color.RED);// Closes the polyline.

            // Get back the mutable Polyline
            Polyline polyline = map.addPolyline(rectOptions);*/
        }
    }
    
    //**************************************************************
    // Implement ConnectionCallbacks interface
    //****************************************************************
    @Override
    public void onConnected(Bundle dataBundle) {
        updateMap();
        //locationClient.requestLocationUpdates(app.getLocationRequest(), this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
        
    /*@Override
    public void onDisconnected() {
        if (mGoogleApiClient.isConnected()) {
            //locationClient.removeLocationUpdates(this);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }*/
    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient.isConnected()) {
            //locationClient.removeLocationUpdates(this);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }
    //**************************************************************
    // Implement OnConnectionFailedListener
    //****************************************************************
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // if Google Play services can resolve the error, display activity
        if (connectionResult.hasResolution()) {
            try {
                // start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }
            catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } 
        else {
            new AlertDialog.Builder(this)
                .setMessage("Connection failed. Error code: " 
                            + connectionResult.getErrorCode())
                .show();
        }
    }
    
    //**************************************************************
    // Implement OnClickListener
    //****************************************************************
    @Override
    public void onClick(View v) {
        startActivity(stopwatchIntent);
    }

    //**************************************************************
    // Implement OnLocationListener
    //****************************************************************
    @Override
    public void onLocationChanged(Location location) {
        updateMap();
    }

    /*@Override
        public void onBackPressed() {
            Intent intent = new Intent(StopwatchActivity.this, StartMenuActivity.class);
            StopwatchActivity.this.startActivity(intent);
        }*/

}