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
import com.example.blooddonationapp.Adapters.DonorAdapter;
import com.example.blooddonationapp.Adapters.RequestAdapter;
import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DonorList extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Donor> donorArrayList;
    DonorAdapter adapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

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

        //Progress Dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data");
        progressDialog.show();

        //Loading verified requests
        recyclerView =v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        db=FirebaseFirestore.getInstance();
        donorArrayList= new ArrayList<Donor>();
        adapter = new DonorAdapter(getContext(),donorArrayList);

        EventChangeListener();
        recyclerView.setAdapter(adapter);

        return v;
    }

    private void EventChangeListener() {

        db.collection("Donor Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null)
                {
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges())
                {
                    String isValid=dc.getDocument().getString("isValid");
                    String isPublic=dc.getDocument().getString("isPublic");
                    if(dc.getType() == DocumentChange.Type.ADDED)
                    {
                        if(isValid.equals("true") && isPublic.equals("true"))
                        donorArrayList.add(dc.getDocument().toObject(Donor.class));
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

            }
        });

    }



}