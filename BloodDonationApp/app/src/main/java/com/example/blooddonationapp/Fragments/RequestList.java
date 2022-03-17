package com.example.blooddonationapp.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.blooddonationapp.BuildConfig.MAPS_API_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Activities.DonorRegistrationFormActivity;
import com.example.blooddonationapp.Adapters.RequestAdapter;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RequestList extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Patient> patientArrayList;
    RequestAdapter adapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    LinearLayout filters;
    TextView textFilter,specificLocation,currentLocation;
    LinearLayout filter;
    boolean filterVisible=false;

    // Location
    private static final int AUTOCOMPLETE_REQUEST_CODE = 100;

    LocationManager lm ;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    FusedLocationProviderClient mFusedLocationProviderClient;
    // the current user position

    private static final int enableLocationRequestCode = 120;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionsGranted = false;
    private LatLng mLatLng;

    public RequestList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_request_list, container, false);
        filters=v.findViewById(R.id.filters);
        textFilter=v.findViewById(R.id.text_filter);
        filter=v.findViewById(R.id.filter);
        specificLocation=v.findViewById(R.id.specific_location);
        currentLocation=v.findViewById(R.id.current_location);

        // HERE: Location stuff
        assert getContext()!=null;

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), MAPS_API_KEY);
        }
        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        //Filter feature
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!filterVisible)
                {
                    filterVisible=true;
                    filters.setVisibility(View.VISIBLE);
                    textFilter.setVisibility(View.VISIBLE);
                }

                else
                {
                    filterVisible=false;
                    filters.setVisibility(View.GONE);
                    textFilter.setVisibility(View.GONE);
                }
            }
        });

        //On selecting specific location
        specificLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME,
                        Place.Field.LAT_LNG, Place.Field.ID);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                filterVisible=false;
                filters.setVisibility(View.GONE);
                textFilter.setVisibility(View.GONE);
            }
        });

        //On selecting current location
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();  // goes on to check if gps is enabled after successful

                if (gps_enabled && network_enabled && mLocationPermissionsGranted)
                    getDeviceLocation();

                filterVisible=false;
                filters.setVisibility(View.GONE);
                textFilter.setVisibility(View.GONE);
            }
        });

        if(!gps_enabled && !network_enabled)
            checkLocationEnabled();
        // HERE: Location in onCreate ends

        //Progress Dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data");
        progressDialog.show();

        //Loading verified requests
        recyclerView =v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        db=FirebaseFirestore.getInstance();
        patientArrayList= new ArrayList<Patient>();
        adapter = new RequestAdapter(getContext(),patientArrayList);

        EventChangeListener();
        recyclerView.setAdapter(adapter);
        return v;
    }

    private void EventChangeListener() {

        db.collection("Raised Requests List").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                 if(error!=null)
                 {
                     return;
                 }
                 for(DocumentChange dc : value.getDocumentChanges())
                 {
                     String isGenuine=dc.getDocument().getString("valid");
                     if(dc.getType() == DocumentChange.Type.ADDED)
                     {
                         if(isGenuine.equals("true"))
                         patientArrayList.add(dc.getDocument().toObject(Patient.class));
                         if(progressDialog.isShowing())
                             progressDialog.dismiss();
                     }
                     progressDialog.dismiss();
                     adapter.notifyDataSetChanged();

                 }

            }
        });
        progressDialog.dismiss();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ///---- Location
        if(requestCode==enableLocationRequestCode){
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if (gps_enabled && network_enabled) {
                getActivity().recreate();
//                getDeviceLocation();  // since activity is getting recreated, easier to use this in onCreate
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && data!=null)
        {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("request", "Place: " + place.getName());

                filters.setVisibility(View.GONE);
                textFilter.setVisibility(View.GONE);


                if(place.getLatLng()!=null)
                    mLatLng = place.getLatLng();
            }

            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
//                Log.i("request", status.getStatusMessage());
            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
    }

    private void getDeviceLocation(){
        Log.d("getDeviceLocation:", " getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted)
            {

                @SuppressLint("MissingPermission") final Task lastLocation = mFusedLocationProviderClient.getLastLocation();
                lastLocation.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful())
                        {
                            Log.d("getDeviceLocation:", "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Toast.makeText(getContext(), "Location: " + currentLocation.getLatitude() +", " +
                                    currentLocation.getLongitude(), Toast.LENGTH_LONG).show();

                            mLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        }
                        else{
                            Log.d("getDeviceLocation:", "onComplete: current location is null");
//                            Toast.makeText(this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("getDeviceLocation:", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }


    private void checkLocationEnabled()
    {
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.enable_gps)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            MapActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) , enableLocationRequestCode);
                        }
                    })
                    .setNegativeButton(R.string.Cancel,null)
                    .show();



        }
        else
            getDeviceLocation();
    }

    private void getLocationPermission(){
        Log.d("getLocationPermission", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),
                COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            mLocationPermissionsGranted = true;
            checkLocationEnabled();
        }
        else // request for permission
            ActivityCompat.requestPermissions(getActivity(),
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);


    }


    // checks if the needed permissions were granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        Log.d("getLocationPermission", "onRequestPermissionsResult: permission failed");
                        return;
                    }
                }
                Log.d("getLocationPermission", "onRequestPermissionsResult: permission granted");
                mLocationPermissionsGranted = true;
                //initialize our map
                checkLocationEnabled();
            }
        }
    }
}