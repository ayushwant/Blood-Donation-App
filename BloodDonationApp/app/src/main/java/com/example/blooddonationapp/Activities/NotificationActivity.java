package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.example.blooddonationapp.Adapters.NotificationAdapter;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.databinding.ActivityNotificationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Notification> arrayList;
    private NotificationAdapter notificationAdapter;
    private DatabaseReference database;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;


    ActivityNotificationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Back button
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Progress Bar while loading notifications
        progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data");
        progressDialog.show();

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));


        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        database= FirebaseDatabase.getInstance().getReference("Notifications").
                child(currentUser.getPhoneNumber());


        arrayList = new ArrayList<Notification>();
        notificationAdapter = new NotificationAdapter(NotificationActivity.this,arrayList);
        binding.recyclerView.setAdapter(notificationAdapter);

        //Load Notifications
        loadNotifications();

    }


    private void loadNotifications()
    {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String st=dataSnapshot.getKey();
                    Notification notification=dataSnapshot.getValue(Notification.class);
                    notification.setKey(st);
                    arrayList.add(notification);
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                progressDialog.dismiss();
                Collections.reverse(arrayList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

}