package com.example.blooddonationapp.AdminSideFragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blooddonationapp.Adapters.RaisedRequestAdapter;
import com.example.blooddonationapp.Adapters.RequestAdapter;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class RaisedRequestsList extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Patient> patientArrayList;
    FirebaseFirestore db;
    RaisedRequestAdapter adapter;
    ProgressDialog progressDialog;

    public RaisedRequestsList() {
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
        View v= inflater.inflate(R.layout.fragment_raised_requests_list, container, false);
//        //Progress Dialog
//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Fetching data");
//        progressDialog.show();

        //Loading verified requests
        recyclerView =v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db=FirebaseFirestore.getInstance();
        patientArrayList= new ArrayList<Patient>();
        adapter = new RaisedRequestAdapter(getContext(),patientArrayList);

        EventChangeListener();
        recyclerView.setAdapter(adapter);

        return v;
    }

    private void EventChangeListener() {

        db.collection("Raised Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        if(!isGenuine.equals("true"))
                            patientArrayList.add(dc.getDocument().toObject(Patient.class));
                       // if(progressDialog.isShowing())
                           // progressDialog.dismiss();
                    }
                 //   progressDialog.dismiss();
                    adapter.notifyDataSetChanged();

                }

            }
        });
     //   progressDialog.dismiss();

    }
}