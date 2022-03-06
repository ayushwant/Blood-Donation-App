package com.example.blooddonationapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Activities.RegisteredMsg;
import com.example.blooddonationapp.Adapters.VPAdapter;
import com.example.blooddonationapp.AdminSideFragments.DonorRegistrationList;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.ModelClasses.RequestHistory;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RequestFragment extends Fragment {

    private Button raiseRequest,postRequest;
    private TextView userName;
    private ImageView drop_up;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Uri uri;
    private EditText patient_name,age,blood_group,required_units,location,documents,details;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference ref;
    private User user =new User();
    private Patient patient=new Patient();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View bloodList;


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
        storage =FirebaseStorage.getInstance();
        storageReference =storage.getReference()
                .child("Documents").child(auth.getCurrentUser().getPhoneNumber());


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
        bloodList=dialog.findViewById(R.id.blood_list);
        drop_up=dialog.findViewById(R.id.drop_up);

        //Uploading documents
        documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setType("application/pdf");
                i.setAction(i.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(i,"PDF FILE SELECTED"),12);
            }
        });

        blood_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bloodList.setVisibility(View.VISIBLE);
                drop_up.setVisibility(View.VISIBLE);
                drop_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });

                TextView op,on,ap,an,bp,bn,abp,abn;
                op=dialog.findViewById(R.id.O_pos);
                on=dialog.findViewById(R.id.O_neg);
                ap=dialog.findViewById(R.id.A_pos);
                an=dialog.findViewById(R.id.A_neg);
                bp=dialog.findViewById(R.id.B_pos);
                bn=dialog.findViewById(R.id.B_neg);
                abp=dialog.findViewById(R.id.AB_pos);
                abn=dialog.findViewById(R.id.AB_neg);

                op.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(op.getText().toString());

                    }
                });
                on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(on.getText().toString());

                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(ap.getText().toString());

                    }
                });
                an.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(an.getText().toString());

                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(ap.getText().toString());

                    }
                });
                bp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(bp.getText().toString());

                    }
                });
                bn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(bn.getText().toString());

                    }
                });
                abp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(abp.getText().toString());

                    }
                });
                abn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(abn.getText().toString());
                      //  bloodList.setVisibility(View.GONE);
                      //  drop_up.setVisibility(View.GONE);
                    }
                });
            }
        });

        postRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(patient_name.length()==0)
                {
                    patient_name.setError("Required");
                }
                if(age.length()==0)
                {
                    age.setError("Required");
                }
                if(blood_group.length()==0)
                {
                    blood_group.setError("Required");
                }
                if(required_units.length()==0)
                {
                    required_units.setError("Required");
                }
                if(location.length()==0)
                {
                    location.setError("Required");
                }
                if(documents.length()==0)
                {
                    documents.setError("Required");
                }

                else
                {

                    patient.setUserName(user.getName());
                    patient.setUserPhone(user.getPhone());
                    patient.setPatientName(patient_name.getText().toString());
                    patient.setAge(age.getText().toString());
                    patient.setBloodGrp(blood_group.getText().toString());
                    patient.setRequiredUnits(required_units.getText().toString());
                    patient.setLocation(location.getText().toString());
                    patient.setAdditionalDetails(details.getText().toString());


                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    RequestHistory requestHistory=new RequestHistory();
                    requestHistory.setUserPhone(user.getPhone());
                    requestHistory.setPatientName(patient_name.getText().toString());
                    requestHistory.setPatientBloodGrp(blood_group.getText().toString());
                    requestHistory.setRequiredUnits(Long.parseLong(required_units.getText().toString()));
                    requestHistory.setLocation(location.getText().toString());
                    requestHistory.setStatus("Pending");
                    mDatabase.child("Raised Request History")
                            .child(user.getPhone()).child(patient_name.getText().toString()).setValue(requestHistory);


                    if (uri != null) {
                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isComplete()) ;
                                Uri uri1 = uriTask.getResult();
                                patient.setPdfUri(uri1.toString());
                                db.collection("Raised Requests").document(currentUser.getPhoneNumber())
                                        .set(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(),"Request Sent",Toast.LENGTH_LONG).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(getContext(), "Error in posting request, try after sometime", Toast.LENGTH_LONG).show();
                                    }
                                });
                                dialog.cancel();
                            }
                        });
                    }

                }



            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            documents.setText(data.getDataString());
            uri=data.getData();
        }
    }

    //For picking the file from mobile storage
    private void selectPDF()
    {

    }

}