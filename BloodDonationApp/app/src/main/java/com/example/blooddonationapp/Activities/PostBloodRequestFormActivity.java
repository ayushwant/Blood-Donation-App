package com.example.blooddonationapp.Activities;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.example.blooddonationapp.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.ModelClasses.RequestHistory;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityAdminLoginBinding;
import com.example.blooddonationapp.databinding.ActivityPostBloodRequestFormBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;

public class PostBloodRequestFormActivity extends AppCompatActivity {

    ActivityPostBloodRequestFormBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference ref;
    private User user =new User();
    private Uri uri,uri1;
    private Patient patient=new Patient();
    private Button postRequest;
    private TextView userName;
    private ImageView drop_up, locationDropUp;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private EditText patient_name,age,blood_group,required_units,location,documents,details,idProof;
    private View bloodList, locationOptions;
    ProgressDialog progressDialog;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 100;

    LocationManager lm ;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mUserPosition;  // the current user position

    private static final int enableLocationRequestCode = 120;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionsGranted = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPostBloodRequestFormBinding .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(currentUser.getPhoneNumber());
        userName=findViewById(R.id.name);
        userName.setText(user.getName());
        storage =FirebaseStorage.getInstance();
        storageReference =storage.getReference()
                .child("Documents").child(auth.getCurrentUser().getPhoneNumber());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Progress Bar while loading pdf
        progressDialog = new ProgressDialog(PostBloodRequestFormActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading pdf");

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    user.setName(documentSnapshot.getString("name"));
                    user.setPhone(documentSnapshot.getString("phone"));

                    userName=findViewById(R.id.name);
                    userName.setText(user.getName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostBloodRequestFormActivity.this,"Error in loading user details",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // initialize Places API key
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), MAPS_API_KEY);
        }


        patient_name=findViewById(R.id.patient_name);
        blood_group=findViewById(R.id.blood_group);
        required_units=findViewById(R.id.required_units);
        location=findViewById(R.id.location_request);
        documents=findViewById(R.id.upload_documents);
        idProof=findViewById(R.id.id_proof);
        details=findViewById(R.id.details);
        postRequest=findViewById(R.id.post_request);
        age=findViewById(R.id.age);
        bloodList=findViewById(R.id.blood_list);
        drop_up=findViewById(R.id.drop_up);

        locationDropUp = findViewById(R.id.drop_up_request_location);
        locationOptions = findViewById(R.id.locationRequestOptions);
        lm = (LocationManager)PostBloodRequestFormActivity.this.getSystemService(Context.LOCATION_SERVICE);

        //Uploading documents
        documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setType("application/pdf");
                i.setAction(i.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"PDF FILE SELECTED"),12);
            }
        });

        //Uploading documents
        idProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setType("application/pdf");
                i.setAction(i.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"PDF FILE SELECTED"),10);
            }
        });


        blood_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bloodList.setVisibility(View.VISIBLE);
                drop_up.setVisibility(View.VISIBLE);
                drop_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });

                TextView op,on,ap,an,bp,bn,abp,abn;
                op=findViewById(R.id.O_pos);
                on=findViewById(R.id.O_neg);
                ap=findViewById(R.id.A_pos);
                an=findViewById(R.id.A_neg);
                bp=findViewById(R.id.B_pos);
                bn=findViewById(R.id.B_neg);
                abp=findViewById(R.id.AB_pos);
                abn=findViewById(R.id.AB_neg);

                op.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(op.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(on.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                an.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(an.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(bp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(bn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(abp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(abn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
            }
        });

        postRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean complete=true;
                if(patient_name.length()==0)
                {
                    complete=false;
                    patient_name.setError("Required");
                }
                if(age.length()==0)
                {
                    complete=false;
                    age.setError("Required");
                }
                if(blood_group.length()==0)
                {
                    complete=false;
                    blood_group.setError("Required");
                }
                if(required_units.length()==0)
                {
                    complete=false;
                    required_units.setError("Required");
                }
                if(location.length()==0)
                {
                    complete=false;
                    location.setError("Required");
                }
                if(documents.length()==0)
                {
                    complete=false;
                    documents.setError("Required");
                }
                if(idProof.length()==0)
                {
                    complete=false;
                    idProof.setError("Required");
                }


                if(complete==true)
                {

                    patient.setUserName(user.getName());
                    patient.setUserPhone(user.getPhone());
                    patient.setPatientName(patient_name.getText().toString());
                    patient.setAge(age.getText().toString());
                    patient.setBloodGrp(blood_group.getText().toString());
                    patient.setRequiredUnits(required_units.getText().toString());
                    patient.setLocation(location.getText().toString());
                    patient.setAdditionalDetails(details.getText().toString());


                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    RequestHistory requestHistory=new RequestHistory();
                    requestHistory.setUserPhone(user.getPhone());
                    requestHistory.setPatientName(patient_name.getText().toString());
                    requestHistory.setPatientBloodGrp(blood_group.getText().toString());
                    requestHistory.setRequiredUnits(Long.parseLong(required_units.getText().toString()));
                    requestHistory.setLocation(location.getText().toString());
                    requestHistory.setStatus("Pending");
                    mDatabase.child("Raised Request History")
                            .child(user.getPhone()).child(patient_name.getText().toString()).setValue(requestHistory);


                    if(uri!=null && uri1!=null)
                    {
                        db.collection("Raised Requests").document(currentUser.getPhoneNumber())
                                .set(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(PostBloodRequestFormActivity.this,"Request Sent",Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(PostBloodRequestFormActivity.this, "Error in posting request, try after sometime", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                locationDropUp.setVisibility(View.VISIBLE);
                locationOptions.setVisibility(View.VISIBLE);

                locationDropUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        locationDropUp.setVisibility(View.GONE);
                        locationOptions.setVisibility(View.GONE);
                    }
                });


                TextView useCurrent, searchLoc;
                useCurrent = findViewById(R.id.currentLocationRequest);
                searchLoc = findViewById(R.id.SearchLocationRequest);

                searchLoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<Place.Field> fields = Arrays.asList(Place.Field.NAME,
                                Place.Field.LAT_LNG, Place.Field.ID);

                        // Start the autocomplete intent.
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(PostBloodRequestFormActivity.this);
                        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    }
                });

                useCurrent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        getLocationPermission();  // goes on to check if gps is enabled after successful

                        if (gps_enabled && network_enabled && mLocationPermissionsGranted)
                            getDeviceLocation();
                    }
                });


            }
        });


    }

    private void getDeviceLocation(){
        Log.d("getDeviceLocation:", " getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                            mUserPosition = currentLocation;
//                            Toast.makeText(PostBloodRequestFormActivity.this, "Location:" +
//                                    currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                            location.setText(currentLocation.getLatitude() +", " + currentLocation.getLongitude());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==enableLocationRequestCode){
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if (gps_enabled && network_enabled) {
                recreate();
//                getDeviceLocation();  // since activity is getting recreated, easier to use this in onCreate
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && data!=null)
        {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("request", "Place: " + place.getName() + ", " + place.getId());
                location.setText(place.getName());
                locationDropUp.setVisibility(View.GONE);
                locationOptions.setVisibility(View.GONE);
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

        if(requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            progressDialog.show();
            uri=data.getData();
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri uri1 = uriTask.getResult();
                    patient.setPdfUri(uri1.toString());
                    documents.setText(data.getDataString());
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
        }
        if(requestCode==10 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            progressDialog.show();
            uri1=data.getData();
            storageReference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri uri2 = uriTask.getResult();
                    patient.setIdProof(uri2.toString());
                    idProof.setText(data.getDataString());
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
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
            AlertDialog alertDialog = new AlertDialog.Builder(PostBloodRequestFormActivity.this)
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

        if(ContextCompat.checkSelfPermission(this, FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            mLocationPermissionsGranted = true;
            checkLocationEnabled();
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