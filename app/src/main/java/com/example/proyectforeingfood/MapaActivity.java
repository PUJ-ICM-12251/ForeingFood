package com.example.proyectforeingfood;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener, GoogleApiClient.OnConnectionFailedListener {


    private static final int ACCESS_LOCATION_ID = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final double lowerLat = 1.396967;
    private static final double lowerLong = -78.903968;
    private static final double upperLat = 11.983639;
    private static final double upperLong = -71.869905;

    private GoogleMap mMap;
    private EditText findLocation;
    private SupportMapFragment mapFragment;
    private LatLng myLocation;
    private LatLng otherLocation;
    private Geocoder mGeocoder;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private List<Polyline> route = null;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if(location != null){
                    usePermission();
                }
            }


        };


        mGeocoder = new Geocoder(getBaseContext());
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (mMap != null) {
                    if (sensorEvent.values[0] < 2000) {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapaActivity.this, R.raw.night));
                    } else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapaActivity.this, R.raw.day));
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        findLocation = findViewById(R.id.etLocation);
        findLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String address = findLocation.getText().toString();
                    if (!address.isEmpty()) {
                        try {
                            List<Address> addresses = mGeocoder.getFromLocationName(address, 2, lowerLat, lowerLong, upperLat, upperLong);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address res = addresses.get(0);
                                LatLng pos = new LatLng(res.getLatitude(), res.getLongitude());
                                if (mMap != null) {
                                    mMap.clear();
                                    mMap.addMarker(new MarkerOptions().position(myLocation).title("Current Location").alpha(0.8f).snippet("My home").
                                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    otherLocation = pos;
                                    MarkerOptions mo = new MarkerOptions();
                                    mo.position(otherLocation);
                                    mo.title(res.getAddressLine(0));
                                    mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    mo.alpha(0.8f);
                                    mMap.addMarker(mo);
                                    double dist = distance(myLocation.latitude, myLocation.longitude, otherLocation.latitude, otherLocation.longitude);
                                    showRoute(myLocation.latitude, myLocation.longitude, otherLocation.latitude, otherLocation.longitude);
                                    Toast.makeText(MapaActivity.this, "Distance is: " + dist + " km", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MapaActivity.this, "Address not found!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MapaActivity.this, "Invalid Address!", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        solicitPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Permission to Access Location", ACCESS_LOCATION_ID);
        usePermission();

    }

    private void solicitPermission(Activity context, String permit, String justification, int id) {
        if (ContextCompat.checkSelfPermission(context, permit) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permit)) {
                Toast.makeText(this, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permit}, id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case ACCESS_LOCATION_ID:{
                usePermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this,
                            "Can´t access location",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void usePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(myLocation).title("My Location").snippet("My Home").alpha(0.8f)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                otherLocation = latLng;
                mMap.clear();
                if(mFusedLocationClient != null){
                    mMap.addMarker(new MarkerOptions().position(myLocation).title("Current Location").alpha(0.8f).snippet("My home").
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.addMarker(new MarkerOptions().position(otherLocation).title(geoCoderSearch(latLng)).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    double dist = distance(myLocation.latitude, myLocation.longitude, otherLocation.latitude, otherLocation.longitude);
                    Toast.makeText(MapaActivity.this, "Distance is: " + dist + " km", Toast.LENGTH_LONG).show();
                    showRoute(myLocation.latitude, myLocation.longitude, otherLocation.latitude, otherLocation.longitude);
                }
            }


        });
    }


    private String geoCoderSearch(LatLng latlng){
        String address = "";
        try{
            List<Address> res = mGeocoder.getFromLocation(latlng.latitude, latlng.longitude, 2);
            if(res != null && res.size() > 0){
                address = res.get(0).getAddressLine(0);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return address;
    }

    protected LocationRequest createLocationRequest(){
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }


    private void startLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    public void settingsLocation(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode){
                    case CommonStatusCodes.RESOLUTION_REQUIRED: {
                        try{
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MapaActivity.this, REQUEST_CHECK_SETTINGS);
                        }catch(IntentSender.SendIntentException sendEx)
                        {
                        }
                        break;
                    }

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                        break;
                    }
                }
            }
        });
    }

    private void stopLocationUpdates(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private double distance(double myLat, double myLong, double otherLat, double otherLong){
        double latDistance = Math.toRadians(myLat - otherLat);
        double longDistance = Math.toRadians(myLong - otherLong);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(myLat)) *
                Math.cos(Math.toRadians(otherLat)) * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double res = 6371.01 * c;

        return Math.round(res * 100.0) / 100.0;
    }

    public void showRoute(double myLat, double myLong, double otherLat, double otherLong){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(myLat, myLong), new LatLng(otherLat, otherLong))
                .key("AIzaSyBXj-lO6VIZ_XcoPN-MZKJ3v9msKNRu7ZE")
                .build();
        routing.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        settingsLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        stopLocationUpdates();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> r, int index) {

        LatLng polyStart = null;
        if (route != null) {
            route.clear();
        }

        PolylineOptions pOptions = new PolylineOptions();
        polyStart = null;
        LatLng polyEnd = null;

        route = new ArrayList<>();
        for (int i = 0; i < r.size(); i++) {
            if (i == index) {
                pOptions.color(Color.rgb(218, 0, 255));
                pOptions.width(12);
                pOptions.addAll(r.get(index).getPoints());
                Polyline polyline = mMap.addPolyline(pOptions);
                polyStart = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polyEnd = polyline.getPoints().get(k - 1);
                route.add(polyline);
            }
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyStart, 15));

    }

    @Override
    public void onRoutingCancelled() {
        showRoute(myLocation.latitude, myLocation.longitude, otherLocation.latitude, otherLocation.longitude);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showRoute(myLocation.latitude, myLocation.longitude, otherLocation.latitude, otherLocation.longitude);
    }
}