package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.example.blooddonationapp.Adapters.NotificationAdapter;
import com.example.blooddonationapp.Adapters.RaisedRequestAdapter;
import com.example.blooddonationapp.Adapters.RequestHistoryAdapter;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.ModelClasses.RequestHistory;
import com.example.blooddonationapp.databinding.ActivityMyRequestBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MyRequest extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<RequestHistory> arrayList;
    private RequestHistoryAdapter adapter;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    ActivityMyRequestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(MyRequest.this));

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        database= FirebaseDatabase.getInstance().getReference("Raised Request History").
                child(currentUser.getPhoneNumber());

        arrayList = new ArrayList<RequestHistory>();
        adapter = new RequestHistoryAdapter(MyRequest.this,arrayList);
        binding.recyclerView.setAdapter(adapter);

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
                    RequestHistory requestHistory=dataSnapshot.getValue(RequestHistory.class);
                    arrayList.add(requestHistory);
                }
                Collections.reverse(arrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}