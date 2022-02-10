package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.blooddonationapp.Adapters.AvailableChatsAdapter;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityAvailableChatsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AvailableChats extends AppCompatActivity {

    ActivityAvailableChatsBinding binding;
    // create  vars
    RecyclerView recyclerView;
    ArrayList<User> usersList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_available_chats);

        binding = ActivityAvailableChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.availableChatsRv;

        usersList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(AvailableChats.this));
        AvailableChatsAdapter rvAdapter = new AvailableChatsAdapter(this, usersList);
        recyclerView.setAdapter(rvAdapter);

        db=FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        String senderPhone=currentUser.getPhoneNumber().substring(3);

        User testUser = new User();
        testUser.setName("Test wala");
        testUser.setPhone("12345");

//        usersList.add(testUser);

        //HERE: create/fetch the data here
//        firebaseDatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                usersList.clear();
//                // got list of all users in the database
//
//                for(DataSnapshot snapshot1 : snapshot.getChildren())
//                {
//                    // add each user in usersList
//                    User user = snapshot1.getValue(User.class);
//                    usersList.add(user);
//                }
//
//                rvAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                User user = document.toObject(User.class);
                                usersList.add(user);
                            }

                            rvAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AvailableChats.this, "Can't get users list from firestore",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}