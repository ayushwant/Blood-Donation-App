package com.example.blooddonationapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Adapters.RequestAdapter;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RequestList extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Patient> patientArrayList;
    RequestAdapter adapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    LinearLayout filters;
    TextView textFilter,specificLocation,currentLocation;
    LinearLayout filter;
    boolean filterVisible=false;
    public RequestList() {
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
        View v=inflater.inflate(R.layout.fragment_request_list, container, false);
        filters=v.findViewById(R.id.filters);
        textFilter=v.findViewById(R.id.text_filter);
        filter=v.findViewById(R.id.filter);
        specificLocation=v.findViewById(R.id.specific_location);
        currentLocation=v.findViewById(R.id.current_location);
        //Filter feature
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(filterVisible==false)
                {
                    filterVisible=true;
                    filters.setVisibility(View.VISIBLE);
                    textFilter.setVisibility(View.VISIBLE);
                }

                else
                {
                    filterVisible=false;
                    filters.setVisibility(View.GONE);
                    textFilter.setVisibility(View.GONE);
                }
            }
        });

        //On selecting specific location
        specificLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                filterVisible=false;
                filters.setVisibility(View.GONE);
                textFilter.setVisibility(View.GONE);
            }
        });

        //On selecting current location
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                

                filterVisible=false;
                filters.setVisibility(View.GONE);
                textFilter.setVisibility(View.GONE);
            }
        });


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
        patientArrayList= new ArrayList<Patient>();
        adapter = new RequestAdapter(getContext(),patientArrayList);

        EventChangeListener();
        recyclerView.setAdapter(adapter);
        return v;
    }

    private void EventChangeListener() {

        db.collection("Raised Requests List").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                 if(error!=null)
                 {
                     return;
                 }
                 for(DocumentChange dc : value.getDocumentChanges())
                 {
                     String isGenuine=dc.getDocument().getString("valid");
                     if(dc.getType() == DocumentChange.Type.ADDED)
                     {
                         if(isGenuine.equals("true"))
                         patientArrayList.add(dc.getDocument().toObject(Patient.class));
                         if(progressDialog.isShowing())
                             progressDialog.dismiss();
                     }
                     progressDialog.dismiss();
                     adapter.notifyDataSetChanged();

                 }

            }
        });
        progressDialog.dismiss();

    }
}