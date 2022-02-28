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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.BuildConfig;
import com.example.blooddonationapp.ModelClasses.PolylineData;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.Utilities.NearbyPlacesSearch;
import com.example.blooddonationapp.Utilities.asyncResponse;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    MapView mMapView;
    FusedLocationProviderClient mFusedLocationProviderClient;
    AlertDialog.Builder builder = null;
    AlertDialog alert = null;
    Button hospitalBtn, pharmacyBtn, bloodBankBtn;

    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Location mUserPosition;  // the current user position

    //vars
    private Boolean mLocationPermissionsGranted = false;

    public static final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

    PlacesClient placesClient;
    private GeoApiContext mGeoApiContext = null;

    // Specify the types of place data to return.
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

    // list of polyLines
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();


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
        hospitalBtn = findViewById(R.id.hospital_Btn);
        pharmacyBtn = findViewById(R.id.pharmacy_Btn);
        bloodBankBtn = findViewById(R.id.bloodBanks_Btn);

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

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(MAPS_API_KEY)
                    .build();
        }
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

        // Set a listener for polyline click
        googleMap.setOnPolylineClickListener(this);

        buttonListeners(googleMap);
    }

    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        // get the destination
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true); // set alternatives route to true

        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserPosition.getLatitude(),
                        mUserPosition.getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    // add the routes
    private void addPolylinesToMap(final DirectionsResult result){

        // we need to add lines on the main thread itself
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                //prevent duplicate and old polyLines
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                Polyline fastest = null;
                for(DirectionsRoute route: result.routes)
                {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());

                    // sum up all the routes
                    List<com.google.maps.model.LatLng> decodedPath =
                            PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();
                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
                    }

                    // now create the polyline
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(MapActivity.this, R.color.grey));
                    polyline.setClickable(true);
                    // store all lines to AL
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        fastest = polyline;
//                        onPolylineClick(polyline);
                    }
                }
                if(fastest!=null)
                    onPolylineClick(fastest);

            }
        });
    }


    @Override
    public void onPolylineClick(@NonNull Polyline polyline)
    {
        zoomRoute(polyline.getPoints());
        int index = 0;
//        BitmapDescriptor transparent = BitmapDescriptorFactory.fromResource(R.drawable.transparent);

        // loop through all lines and only make the clicked one blue, rest grey
        for(PolylineData polylineData: mPolyLinesData)
        {
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());

            // if this polyLinesData references the clicked polyline, make it blue
            if(polylineData.getPolyline().getId().equals( polyline.getId() ))
            {
                polylineData.getPolyline().setColor(ContextCompat.getColor(MapActivity.this, R.color.quantum_vanillablueA700));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip #" + index )//+": Distance: " +polylineData.getLeg().distance)   //("Trip #" + index)
                        .alpha(0) // make transparent
//                        .icon(transparent)
                        .snippet( "Distance: " +polylineData.getLeg().distance + ", " +
                                "Duration: " + polylineData.getLeg().duration
                        ));


                marker.showInfoWindow();
            }
            else
            {
                polylineData.getPolyline().setColor(ContextCompat.getColor(MapActivity.this, R.color.grey));
                polylineData.getPolyline().setZIndex(0);
            }
        }

    }

    // animate and zoom the camera to the selected polyline
    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null);
    }

    private void buttonListeners(GoogleMap googleMap)
    {
        // set click listeners for hospital button
        hospitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) // search nearby hospitals
            {
                new nearbySearchTask(new asyncResponse() {
                    @Override // returns a list of all the hospitals
                    public void processFinish(List<HashMap<String, String>> output)
                    {
                        for (HashMap<String, String> hospital : output)
                        {
                            if( hospital.get("latitude")!=null && hospital.get("longitude")!=null ) {
                                LatLng latLng = new LatLng( Float.parseFloat(hospital.get("latitude")),
                                        Float.parseFloat(hospital.get("longitude") ));

                                mMap.addMarker(new MarkerOptions().position(latLng).title(hospital.get("placeName"))  );
                            }
                        }
                    }
                }).execute( new searchParameters(googleMap.getCameraPosition().target, "hospital", "") );
            }
        });

        // set click listeners for pharmacy button
        pharmacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) // search nearby hospitals
            {
                new nearbySearchTask(new asyncResponse() {
                    @Override // returns a list of all the hospitals
                    public void processFinish(List<HashMap<String, String>> output)
                    {
                        for (HashMap<String, String> pharmacy : output)
                        {
                            if( pharmacy.get("latitude")!=null && pharmacy.get("longitude")!=null ) {
                                LatLng latLng = new LatLng( Float.parseFloat(pharmacy.get("latitude")),
                                        Float.parseFloat(pharmacy.get("longitude") ));

                                mMap.addMarker(new MarkerOptions().position(latLng).title(pharmacy.get("placeName"))  );
                            }
                        }
                    }
                }).execute( new searchParameters(googleMap.getCameraPosition().target, "pharmacy", "") );
            }
        });

        // set click listeners for blood bank button - there's no field available to search for blood bank using nearby api.
        // need to use text search api
    }

    /** Called when the user clicks a marker.
     * https://developers.google.com/maps/documentation/android-sdk/marker#add_a_marker
     *
     * Show the details of the place where the marker was clicked
     * */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        // start a nearby search on the location of the marker using async task,
        // and show the nearest result on the bottom sheet dialog
        new nearbySearchTask(new asyncResponse() {
            @Override
            public void processFinish(List<HashMap<String, String>> results) {
                if (results != null && results.size() > 0) {
                    final Dialog dialog;
                    dialog = new Dialog(MapActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.bottom_sheet_map_places);

                    // get and bind the views
                    TextView placeName, placeRating, totalRatings, placeType, openStatus, fullAddress;
                    ImageView starImg, getDirectionsBtn;

                    placeName = dialog.findViewById(R.id.bs_placeName);
                    placeRating = dialog.findViewById(R.id.bs_placeRating);
                    totalRatings = dialog.findViewById(R.id.bs_totalRatings);
                    placeType = dialog.findViewById(R.id.bs_placeType);
                    openStatus = dialog.findViewById(R.id.bs_openStatus);
                    fullAddress = dialog.findViewById(R.id.bs_fullAddress);
                    starImg = dialog.findViewById(R.id.bs_starImage);
                    getDirectionsBtn = dialog.findViewById(R.id.bs_getDirectionsBtn);

                    placeName.setText(results.get(0).get("placeName"));
                    placeRating.setText(results.get(0).get("placeRating"));
                    totalRatings.setText(results.get(0).get("totalRatings"));
                    placeType.setText(results.get(0).get("placeType"));
                    openStatus.setText(results.get(0).get("openStatus"));
                    fullAddress.setText(results.get(0).get("fullAddress"));

                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.BOTTOM);

                    getDirectionsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            calculateDirections(marker);
                        }
                    });

                }
            }
        }).execute( new searchParameters(marker.getPosition(), null, null) );

        return false; // to do the default action
    }

    // helper class to pass parameters to asyncTask
    public static class searchParameters{
        LatLng location;
        String type;
        String radius;

        public searchParameters(LatLng location, String type, String radius) {
            this.location = location;
            this.type = type;
            this.radius = radius;
        }

        public LatLng getLocation() {
            return location;
        }

        public void setLocation(LatLng location) {
            this.location = location;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRadius() {
            return radius;
        }

        public void setRadius(String radius) {
            this.radius = radius;
        }
    }

    public static class nearbySearchTask extends AsyncTask<searchParameters, Void, List<HashMap<String, String>> >
    {
        @Override
        protected List<HashMap<String, String>> doInBackground(searchParameters... params)
        {
            NearbyPlacesSearch nearbyPlacesSearch = new NearbyPlacesSearch();
            try {
                List<HashMap<String, String>> results = nearbyPlacesSearch.search
                        (params[0].getLocation(), params[0].getType(), params[0].getRadius());

                return results;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public asyncResponse outputFromAsync = null;
        public nearbySearchTask(asyncResponse delegate){
            this.outputFromAsync = delegate;
        }
        @Override
        protected void onPostExecute(List<HashMap<String, String>> results) {
            outputFromAsync.processFinish(results);
        }

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
                            mUserPosition = currentLocation;

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