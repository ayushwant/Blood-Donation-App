package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.Adapters.AvailableChatsAdapter;
import com.example.blooddonationapp.Adapters.ChatAdapter;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityChatsBinding;
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
import java.util.Collections;

public class Chats extends AppCompatActivity {

    ActivityChatsBinding binding;

    RecyclerView recyclerView;
    ArrayList<User> usersList;

    private FirebaseFirestore firestoreDb;
    private FirebaseAuth auth;
    private FirebaseDatabase realtimeDb;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chats);

        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.availableChatsBtn.setOnClickListener(view ->
                startActivity( new Intent(Chats.this, AvailableChats.class)));

        recyclerView = binding.msgedUsersRv;

        usersList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(Chats.this));
        ChatAdapter rvAdapter = new ChatAdapter(this, usersList);
        recyclerView.setAdapter(rvAdapter);

        firestoreDb = FirebaseFirestore.getInstance();
        realtimeDb = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        //HERE: create/fetch the data here
        // see // -----> check if chat already exists in chatFragment in Rudhira

        // get all users from firestore, and check if a chat has been initiated with this user
        firestoreDb.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                usersList.clear();
                                User user = document.toObject(User.class);

                                String neededRoom = currentUser.getUid()+ "__" +user.getUid();

                                // -----> check if chat already exists of currentUSer with this user in the realtime DB
                                realtimeDb.getReference().child("chats").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot chatSnapshot) {

                                        // check directly
                                        if(chatSnapshot.hasChild(neededRoom)) {
                                            usersList.add(user);
                                            rvAdapter.notifyDataSetChanged();
                                        }

                                        // or iterate
//                                        for(DataSnapshot snap1 : chatSnapshot.getChildren())
//                                        {
//                                            String room = snap1.getKey();
//
//                                            if( room.equals(neededRoom) ) {
//                                                usersList.add(user);
//                                                rvAdapter.notifyDataSetChanged();
//                                                break;
//                                            }
//                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Chats.this, error.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
//                     ----->

                            }
                            // this happens before itself. Check for change when adding users in realtime DB
                            // run debugger to see that this statement happens before the check in the realtime DB
//                            rvAdapter.notifyDataSetChanged();
                        }
                        else Toast.makeText(Chats.this, "Can't get users list from firestore",
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }
}