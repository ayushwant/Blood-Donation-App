package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.BuildConfig;
import com.example.blooddonationapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    MapView mMapView;
    FusedLocationProviderClient mFusedLocationProviderClient;
    AlertDialog.Builder builder = null;
    AlertDialog alert = null;

    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;

    public static final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

    PlacesClient placesClient;

    // Specify the types of place data to return.
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // initialize Places API key
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), MAPS_API_KEY);
        }
        placesClient = Places.createClient(this);

        // Inflate the layout for this fragment and bind
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        initAutocomplete();
        getLocationPermission();
    }

    // initializes the map after getting permissions properly
    private void initMap() {
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
    }

    /** Called when the map is ready. */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));


        if(mLocationPermissionsGranted)
        {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Set a listener for long map click and drop marker there
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Marker dropMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng) );

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                    if (addresses.size() > 0)
                    {
                        Address fetchedAddress = addresses.get(0);

                        StringBuilder addressInfo = new StringBuilder("");
                        if(fetchedAddress.getSubLocality()!=null)
                        addressInfo.append( fetchedAddress.getSubLocality() +", " +fetchedAddress.getSubAdminArea() );

                        dropMarker.setTitle(String.valueOf(addressInfo));
                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this);
    }

    /** Called when the user clicks a marker.
     * https://developers.google.com/maps/documentation/android-sdk/marker#add_a_marker */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        Toast.makeText(this, "Clicked marker" +marker.getTitle(), Toast.LENGTH_SHORT).show();
        marker.showInfoWindow();

        //showDialog();
        final Dialog placeDetailsDialog;
        placeDetailsDialog = new Dialog(this);
        placeDetailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        placeDetailsDialog.setContentView(R.layout.bottom_sheet_map_places);

        // get and bind the views
        TextView name, rating, totalRatings, type, openStatus, address;
        ImageView starImg, getDirectionsBtn;

        name = findViewById(R.id.bs_placeName);
        rating = findViewById(R.id.bs_placeRating);
        totalRatings = findViewById(R.id.bs_totalRatings);
        type = findViewById(R.id.bs_placeType);
        openStatus = findViewById(R.id.bs_openStatus);
        address = findViewById(R.id.bs_fullAddress);
        starImg = findViewById(R.id.bs_starImage);
        getDirectionsBtn = findViewById(R.id.bs_getDirectionsBtn);

        placeDetailsDialog.show();
        placeDetailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        placeDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        placeDetailsDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        placeDetailsDialog.getWindow().setGravity(Gravity.BOTTOM);

        return false;
    }

    // --------Autocomplete-----------
    private void initAutocomplete()
    {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(placeFields);

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                LatLng placeLatLng = place.getLatLng();
                if(placeLatLng!=null) {
                    Log.i(TAG, "Place: " + placeLatLng);
                    moveCamera(placeLatLng, DEFAULT_ZOOM);

                    //addMarker
                    mMap.addMarker(new MarkerOptions()
                            .position(placeLatLng)
                            .title(place.getName()));
                }
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }
    // --------Autocomplete-----------

    private void moveCamera(LatLng loc, float level)
    {
        // For zooming automatically to the location
        CameraPosition cameraPosition;
        if (loc != null) {
            cameraPosition = new CameraPosition.Builder().target(loc).zoom(level).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    // Gets called after map is initialized properly
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted)
            {

                @SuppressLint("MissingPermission") final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }
                        else{
                            Log.d(TAG, "onComplete: current location is null");
//                            Toast.makeText(this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    // this is the 1st method that gets called after createView. It checks if the permission has been provided by the user,
    // and asks for permission if not granted. It then calls onRequestPermissionsResult to check if the needed permissions
    // were granted. If they were already granted, then it calls initMap()
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this, FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            mLocationPermissionsGranted = true;
            initMap();

        }
        else // request for permission
            ActivityCompat.requestPermissions(this,
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);


    }


    // checks if the needed permissions were granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    // Ask to enable GPS
    private void buildAlertMessageNoGps() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(alert!=null) alert.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if(alert!=null) alert.dismiss();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        if(alert!=null) alert.dismiss();
    }



}