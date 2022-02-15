package com.example.blooddonationapp.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Activities.RegisteredMsg;
import com.example.blooddonationapp.Adapters.VPAdapter;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RequestFragment extends Fragment {

    private Button raiseRequest,postRequest;
    private TextView userName;
    private EditText patient_name,age,blood_group,required_units,location,documents,details;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference ref;
    private User user =new User();
    private Patient patient=new Patient();
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public RequestFragment(){
    }

    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_request, container, false);
        tabLayout=v.findViewById(R.id.tab_layout);
        viewPager=v.findViewById(R.id.view_pager);
        raiseRequest=v.findViewById(R.id.raised_request);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(currentUser.getPhoneNumber());

        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter=new VPAdapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new RequestList(),"Requests");
        vpAdapter.addFragment(new DonorList(),"Blood Donors");
        viewPager.setAdapter(vpAdapter);

        raiseRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  showDialog();
            }
        });

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        user.setName(documentSnapshot.getString("name"));
                        user.setPhone(documentSnapshot.getString("phone"));
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error in loading user details",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void showDialog()
    {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        userName=dialog.findViewById(R.id.name);
        userName.setText(user.getName());


        patient_name=dialog.findViewById(R.id.patient_name);
        blood_group=dialog.findViewById(R.id.blood_group);
        required_units=dialog.findViewById(R.id.required_units);
        location=dialog.findViewById(R.id.location);
        documents=dialog.findViewById(R.id.upload_documents);
        details=dialog.findViewById(R.id.details);
        postRequest=dialog.findViewById(R.id.post_request);
        age=dialog.findViewById(R.id.age);


        postRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                patient.setUserName(user.getName());
                patient.setUserPhone(user.getPhone());
                patient.setPatientName(patient_name.getText().toString());
                patient.setAge(age.getText().toString());
                patient.setBloodGrp(blood_group.getText().toString());
                patient.setRequiredUnits(required_units.getText().toString());
                patient.setLocation(location.getText().toString());
                patient.setPdfUri(documents.getText().toString());
                patient.setAdditionalDetails(details.getText().toString());

                db.collection("Raised Requests").document(currentUser.getPhoneNumber())
                        .set(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent=new Intent(getContext(),RegisteredMsg.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                      Toast.makeText(getContext(),"Error in posting request, try after sometime",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }
}