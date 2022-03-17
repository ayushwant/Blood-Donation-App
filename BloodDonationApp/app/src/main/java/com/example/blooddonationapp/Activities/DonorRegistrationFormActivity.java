package com.example.blooddonationapp.Activities;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDonorRegistrationFormBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DonorRegistrationFormActivity extends AppCompatActivity {

    private EditText donorName,donorPhone,donorAge,donorEmail,blood_group, location,donorPdfUri;
    private View bloodList, locationOptions;
    private ImageView drop_up, locationDropUp;
    private Button register;
    Donor donor=new Donor();
    private CheckBox isPublicBox;
    ActivityDonorRegistrationFormBinding binding;
    private FirebaseAuth mAuth;
    private Uri uri;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    FirebaseFirestore db,db1,db2,db3;
    ProgressDialog progressDialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDonorRegistrationFormBinding .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Progress Bar while loading pdf
        progressDialog = new ProgressDialog(DonorRegistrationFormActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading pdf");



        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db1 = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();
        db3 = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference()
                .child("Documents").child(mAuth.getCurrentUser().getPhoneNumber());


        donorName=findViewById(R.id.name);
        donorPhone=findViewById(R.id.phone);
        donorAge=findViewById(R.id.age);
        donorEmail=findViewById(R.id.email);
        blood_group=findViewById(R.id.blood_group);
        location =findViewById(R.id.location_Donor);
        donorPdfUri=findViewById(R.id.upload_documents);
        isPublicBox=findViewById(R.id.isPublic);
        register=findViewById(R.id.register);
        bloodList=findViewById(R.id.blood_list);
        drop_up=findViewById(R.id.drop_up);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Uploading documents
        donorPdfUri.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent i= new Intent();
        i.setType("application/pdf");
        i.setAction(i.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"PDF FILE SELECTED"),12);
             }
        });


        binding.bloodGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                binding.bloodList.setVisibility(View.VISIBLE);
                binding.dropUp.setVisibility(View.VISIBLE);
                binding.dropUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodList.setVisibility(View.GONE);
                        binding.dropUp.setVisibility(View.GONE);
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
                        binding.bloodGroup.setText(op.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);

                    }
                });
                on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(on.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);

                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                an.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(an.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(bp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(bn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(abp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(abn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
            }
        });
        db1.collection("Users").document(currentUser.getPhoneNumber())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot dc) {
                donorName.setText(dc.getString("name"));
                donorPhone.setText(currentUser.getPhoneNumber());
                donorEmail.setText(dc.getString("email"));
                blood_group.setText(dc.getString("bloodGrp"));
                location.setText(dc.getString("address"));
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean complete=true;
                if(donorName.length()==0)
                {
                    complete=false;
                    donorName.setError("Required");
                }
                if(donorPhone.length()==0)
                {
                    complete=false;
                    donorPhone.setError("Required");
                }
                if(donorAge.length()==0)
                {
                    complete=false;
                    donorAge.setError("Required");
                }
                if(blood_group.length()==0)
                {
                    complete=false;
                    blood_group.setError("Required");
                }
                if(donorEmail.length()==0)
                {
                    complete=false;
                    donorEmail.setError("Required");
                }
                if(location.length()==0)
                {
                    complete=false;
                    location.setError("Required");
                }
                if(donorPdfUri.length()==0)
                {
                    complete=false;
                    donorPdfUri.setError("Required");
                }
                if(complete==true)
                {
                    donor.setName(donorName.getText().toString());
                    donor.setPhone(donorPhone.getText().toString());
                    donor.setAge(donorAge.getText().toString());
                    donor.setEmail(donorEmail.getText().toString());
                    donor.setBloodGrp(blood_group.getText().toString());
                    donor.setLocation(location.getText().toString());
                    donor.setValid(false);
                    donor.setPublic(false);
                    if(isPublicBox.isChecked())
                        donor.setPublic(true);

                    db.collection("Donor Requests").document(currentUser.getPhoneNumber())
                            .set(donor).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(DonorRegistrationFormActivity.this,"Details Submitted",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(DonorRegistrationFormActivity.this,"Error in posting request, try after sometime",Toast.LENGTH_LONG).show();
                        }
                    });



                }
            }
        });

        ///--- Location works

        // initialize Places API key
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), MAPS_API_KEY);
        }

        locationDropUp = findViewById(R.id.drop_up_request_Donor);
        locationOptions = findViewById(R.id.locationDonorOptions);
        lm = (LocationManager)DonorRegistrationFormActivity.this.getSystemService(Context.LOCATION_SERVICE);

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
                useCurrent = findViewById(R.id.currentLocationDonor);
                searchLoc = findViewById(R.id.SearchLocationDonor);

                searchLoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<Place.Field> fields = Arrays.asList(Place.Field.NAME,
                                Place.Field.LAT_LNG, Place.Field.ID);

                        // Start the autocomplete intent.
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(DonorRegistrationFormActivity.this);
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

        if(!gps_enabled && !network_enabled)
            checkLocationEnabled();
       }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    donor.setPdfUri(uri1.toString());
                    donorPdfUri.setText(data.getDataString());
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });

        }

        ///---- Location
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
//                Log.i("request", "Place: " + place.getName() + ", " + place.getId());

                location.setText(place.getName());
                locationDropUp.setVisibility(View.GONE);
                locationOptions.setVisibility(View.GONE);

                donor.setLocation(place.getName());

                if(place.getLatLng()!=null)
                    donor.setLatLng(place.getLatLng().latitude+ "," + place.getLatLng().longitude );
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
//                            Toast.makeText(PostBloodRequestFormActivity.this, "Location:" +
//                                    currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                            // get the Address from LatLng
                            Geocoder geocoder = new Geocoder(DonorRegistrationFormActivity.this, Locale.ENGLISH);
                            StringBuilder addressInfo = new StringBuilder("");

                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

                                if (addresses.size() > 0)
                                {
                                    Address fetchedAddress = addresses.get(0);

                                    if(fetchedAddress.getSubLocality()!=null)
                                        addressInfo.append(fetchedAddress.getSubLocality()).append(", ").append(fetchedAddress.getSubAdminArea());
                                }

                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }


                            if(!addressInfo.toString().equals("")) {
                                location.setText(addressInfo);
                                donor.setLocation(addressInfo.toString());
                            }
                            else{
                                location.setText(R.string.curr_loc);
                                donor.setLocation("User's current Location");
                            }

                            locationDropUp.setVisibility(View.GONE);
                            locationOptions.setVisibility(View.GONE);
                            donor.setLatLng(currentLocation.getLatitude()
                                    +"," +currentLocation.getLongitude());
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
            AlertDialog alertDialog = new AlertDialog.Builder(DonorRegistrationFormActivity.this)
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
