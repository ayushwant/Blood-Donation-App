package com.example.blooddonationapp.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.example.blooddonationapp.Activities.RegisteredMsg;
import com.example.blooddonationapp.Activities.SplashScreen;
import com.example.blooddonationapp.Adapters.DonorAdapter;
import com.example.blooddonationapp.Adapters.RequestAdapter;
import com.example.blooddonationapp.MainActivityAdmin;
import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DonorList extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Donor> donorArrayList;
    DonorAdapter adapter;
    FirebaseFirestore db,db1;
    private FirebaseUser currentUser;
    public String isAdmin="false";

    public DonorList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_donor_list, container, false);

        //Loading verified requests
        recyclerView =v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db=FirebaseFirestore.getInstance();
        donorArrayList= new ArrayList<Donor>();
        adapter = new DonorAdapter(getContext(),donorArrayList);


        db.collection("Admin").document(currentUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                    String signedin=task.getResult().getString("Signed_in");
                    if(signedin.equals("true"))
                    {
                        isAdmin ="true";
                    }
                }


                EventChangeListener();
            }
        });


        recyclerView.setAdapter(adapter);

        return v;
    }

    private void EventChangeListener() {

        db.collection("Registered Donors").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null)
                {
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges())
                {
                    if(dc.getType() == DocumentChange.Type.ADDED)
                    {
                        Boolean isPublic=dc.getDocument().getBoolean("public");

                        //For admin side donor list
                        if(isAdmin.equals("true") || (isPublic.equals(true)))
                        {
                            donorArrayList.add(dc.getDocument().toObject(Donor.class));
                        }
//                        else
//                        {
//                            if(isPublic.equals(true))
//                            donorArrayList.add(dc.getDocument().toObject(Donor.class));
//                        }

                    }
                    adapter.notifyDataSetChanged();

                }

            }
        });

    }



}