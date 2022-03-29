package com.example.blooddonationapp.Utilities;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.blooddonationapp.Activities.PostBloodRequestFormActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationUtils
{}
//{
//    private void getDeviceLocation(){
//        Log.d("getDeviceLocation:", " getting the devices current location");
//
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try{
//            if(mLocationPermissionsGranted)
//            {
//
//                @SuppressLint("MissingPermission") final Task lastLocation = mFusedLocationProviderClient.getLastLocation();
//                lastLocation.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task)
//                    {
//                        if(task.isSuccessful())
//                        {
//                            Log.d("getDeviceLocation:", "onComplete: found location!");
//                            Location currentLocation = (Location) task.getResult();
//                            mUserPosition = currentLocation;
//                            Toast.makeText(PostBloodRequestFormActivity.this, "Location:" +
//                                    currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//
//                            location.setText(currentLocation.getLatitude() +", " + currentLocation.getLongitude());
//                        }
//                        else{
//                            Log.d("getDeviceLocation:", "onComplete: current location is null");
////                            Toast.makeText(this, "unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }catch (SecurityException e){
//            Log.e("getDeviceLocation:", "getDeviceLocation: SecurityException: " + e.getMessage() );
//        }
//    }
//}
