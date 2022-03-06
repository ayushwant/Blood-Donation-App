package com.example.blooddonationapp.ActivitiesAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivityAdmin;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDonorRegistrationDetailBinding;
import com.example.blooddonationapp.databinding.ActivityRequestDetailAdminBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RequestDetailAdmin extends AppCompatActivity {

    ActivityRequestDetailAdminBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityRequestDetailAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting current user from firebase authentication
        auth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        Intent i=getIntent();
        number=i.getStringExtra("phone");

        //Displaying details
        db.collection("Raised Requests").document(number).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                binding.seekerName.setText(task.getResult().getString("userName"));
                binding.seekerContact.setText(task.getResult().getString("userPhone"));
                binding.patientBloodGroup.setText(task.getResult().getString("bloodGrp"));
                binding.patientName.setText(task.getResult().getString("patientName"));
                binding.requiredUnits.setText(task.getResult().getString("requiredUnits"));
                binding.patientAge.setText(task.getResult().getString("age"));
                binding.location.setText(task.getResult().getString("location"));
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

        //Marking as verified
        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetailAdmin.this);
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Map < String, Object > data = new HashMap<>();
                                db.collection("Raised Requests").document(number)
                                        .update("valid", "true");

                                Notification notification=new Notification();
                                notification.setLine1("Blood Request Verification");
                                notification.setLine2("Your request has been verified");
                                notification.setLine3("");
                                notification.setSeen(false);

                                mDatabase.child("Notifications")
                                        .child(number).push().setValue(notification);

                                Intent intent= new Intent(RequestDetailAdmin.this, MainActivityAdmin.class);
                                startActivity(intent);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetailAdmin.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Cancellation is Permanent");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Raised Requests").document(number).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Intent i= new Intent(RequestDetailAdmin.this, MainActivityAdmin.class);
                                        startActivity(i);
                                        //Notifying
                                        Notification notification=new Notification();
                                        notification.setLine1("Blood Request Verification");
                                        notification.setLine2("Your request has been declined");
                                        notification.setLine3("Kindly check your credentials before applying");
                                        notification.setSeen(false);

                                        mDatabase.child("Notifications")
                                                .child(number).push().setValue(notification);

                                        Intent intent= new Intent(RequestDetailAdmin.this, MainActivityAdmin.class);
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
}