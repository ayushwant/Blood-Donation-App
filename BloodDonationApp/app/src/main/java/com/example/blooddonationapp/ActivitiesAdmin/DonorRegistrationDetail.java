package com.example.blooddonationapp.ActivitiesAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivityAdmin;
import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDonorRegistrationDetailBinding;
import com.example.blooddonationapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DonorRegistrationDetail extends AppCompatActivity {

    ActivityDonorRegistrationDetailBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    private String number;
    private boolean isPublic;
    private String pdfUri;

    Donor donor = new Donor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonorRegistrationDetailBinding .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting current user from firebase authentication
        auth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent i=getIntent();
        number=i.getStringExtra("phone");

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Downloading Document
        binding.drDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DonorRegistrationDetail.this, pdfUri, Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUri));
                startActivity(browserIntent);
            }
        });


        //Displaying details
        db.collection("Donor Requests").document(number).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                binding.donorName.setText(task.getResult().getString("name"));
                binding.donorContact.setText(task.getResult().getString("phone"));
                binding.donorEmail.setText(task.getResult().getString("email"));
                binding.drBloodGroup.setText(task.getResult().getString("bloodGrp"));
                binding.drAge.setText(task.getResult().getString("age"));
                binding.drLocation.setText(task.getResult().getString("location"));
                pdfUri=task.getResult().getString("pdfUri");
                isPublic=task.getResult().getBoolean("public");

            }
        });



        //Marking as verified i.e. adding donor to registered donors list
        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DonorRegistrationDetail.this);
                builder.setMessage("Mark as verified donor?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        donor.setName(binding.donorName.getText().toString());
                        donor.setPhone(binding.donorContact.getText().toString());
                        donor.setAge(binding.drAge.getText().toString());
                        donor.setEmail(binding.donorEmail.getText().toString());
                        donor.setBloodGrp(binding.drBloodGroup.getText().toString());
                        donor.setLocation(binding.drLocation.getText().toString());
                        donor.setPdfUri(binding.drDocuments.getText().toString());
                        donor.setValid(true);
                        donor.setPublic(false);
                        if(isPublic)
                            donor.setPublic(true);

                        db.collection("Registered Donors").document(number)
                                .set(donor).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Notification notification=new Notification();
                                notification.setLine1("Donor Registration");
                                notification.setLine2("Your request has been verified");
                                notification.setLine3("");
                                notification.setSeen(false);

                                mDatabase.child("Notifications")
                                        .child(number).push().setValue(notification);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(DonorRegistrationDetail.this,"Error in posting request, try after sometime",Toast.LENGTH_LONG).show();
                            }
                        });

                        db.collection("Donor Requests").document(number).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        //Notifying


                                        Intent i= new Intent(DonorRegistrationDetail.this, MainActivityAdmin.class);
                                        startActivity(i);
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

        //Cancelling Request
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DonorRegistrationDetail.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Cancellation is Permanent");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Donor Requests").document(number).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Intent i= new Intent(DonorRegistrationDetail.this, MainActivityAdmin.class);
                                        startActivity(i);
                                        //Notifying

                                        Notification notification=new Notification();
                                        notification.setLine1("Blood Donation Verification");
                                        notification.setLine2("You are not verified to donate");
                                        notification.setLine3("Kindly check your credentials before applying");
                                        notification.setSeen(false);

                                        mDatabase.child("Notifications")
                                                .child(number).push().setValue(notification);


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
}