package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.ActivitiesAdmin.RequestDetailAdmin;
import com.example.blooddonationapp.MainActivityAdmin;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.ModelClasses.RequestHistory;
import com.example.blooddonationapp.databinding.ActivityMyRequestBinding;
import com.example.blooddonationapp.databinding.ActivityMyRequestDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyRequestDetail extends AppCompatActivity {

    ActivityMyRequestDetailBinding binding;
    String phone,patientName;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    RequestHistory requestHistory=new RequestHistory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyRequestDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent i=getIntent();
        phone=i.getStringExtra("Seeker Number");

        // getting current user from firebase authentication
        auth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();



        //Displaying details
        db.collection("Raised Requests").document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                binding.patientBloodGroup.setText(task.getResult().getString("bloodGrp"));
                requestHistory.setPatientBloodGrp(task.getResult().getString("bloodGrp"));

                binding.patientName.setText(task.getResult().getString("patientName"));
                requestHistory.setPatientName(task.getResult().getString("patientName"));
                patientName=task.getResult().getString("patientName");

                binding.requiredUnits.setText(task.getResult().getString("requiredUnits"));
              //  requestHistory.setRequiredUnits(task.getResult().getLong("requiredUnits"));

                binding.patientAge.setText(task.getResult().getString("age"));

                binding.location.setText(task.getResult().getString("location"));
                requestHistory.setLocation(task.getResult().getString("location"));

                binding.documents.setText(task.getResult().getString("pdfUri"));
                binding.details.setText(task.getResult().getString("additionalDetails"));

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //Cancelling Request
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyRequestDetail.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Cancellation is Permanent");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Raised Requests").document(phone).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        requestHistory.setStatus("Cancelled");
                                        mDatabase.child("Raised Request History")
                                                .child(phone).push().setValue(requestHistory);

                                        //Deleting pending request
                                        removePending();

                                        Intent intent= new Intent(MyRequestDetail.this, MyRequest.class);
                                        startActivity(intent);
                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog ad=builder.create();
                ad.show();

            }


        });

        //Marking as received
        binding.received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyRequestDetail.this);
                builder.setMessage("Received blood through our app");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Raised Requests").document(phone).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        requestHistory.setStatus("Received");
                                        mDatabase.child("Raised Request History")
                                                .child(phone).push().setValue(requestHistory);

                                        //Deleting pending request
                                        removePending();
                                        Intent intent= new Intent(MyRequestDetail.this, MyRequest.class);
                                        startActivity(intent);
                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog ad=builder.create();
                ad.show();

            }


        });

    }
    private void removePending()
    {
        Toast.makeText(this, patientName, Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Raised Request History").child(currentUser.getPhoneNumber())
                .child(patientName);
        mDatabase.removeValue();
    }
}