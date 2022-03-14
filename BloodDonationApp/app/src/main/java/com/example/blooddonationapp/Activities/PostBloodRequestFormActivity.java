package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.ModelClasses.RequestHistory;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityAdminLoginBinding;
import com.example.blooddonationapp.databinding.ActivityPostBloodRequestFormBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class PostBloodRequestFormActivity extends AppCompatActivity {

    ActivityPostBloodRequestFormBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference ref;
    private User user =new User();
    private Uri uri,uri1;
    private Patient patient=new Patient();
    private Button postRequest;
    private TextView userName;
    private ImageView drop_up;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private EditText patient_name,age,blood_group,required_units,location,documents,details,idProof;
    private View bloodList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPostBloodRequestFormBinding .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(currentUser.getPhoneNumber());
        userName=findViewById(R.id.name);
        userName.setText(user.getName());
        storage =FirebaseStorage.getInstance();
        storageReference =storage.getReference()
                .child("Documents").child(auth.getCurrentUser().getPhoneNumber());

        //Progress Bar while loading pdf
        progressDialog = new ProgressDialog(PostBloodRequestFormActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading pdf");

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    user.setName(documentSnapshot.getString("name"));
                    user.setPhone(documentSnapshot.getString("phone"));

                    userName=findViewById(R.id.name);
                    userName.setText(user.getName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostBloodRequestFormActivity.this,"Error in loading user details",
                        Toast.LENGTH_SHORT).show();
            }
        });


        patient_name=findViewById(R.id.patient_name);
        blood_group=findViewById(R.id.blood_group);
        required_units=findViewById(R.id.required_units);
        location=findViewById(R.id.location);
        documents=findViewById(R.id.upload_documents);
        idProof=findViewById(R.id.id_proof);
        details=findViewById(R.id.details);
        postRequest=findViewById(R.id.post_request);
        age=findViewById(R.id.age);
        bloodList=findViewById(R.id.blood_list);
        drop_up=findViewById(R.id.drop_up);

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

        //Uploading documents
        idProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setType("application/pdf");
                i.setAction(i.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"PDF FILE SELECTED"),10);
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
                op=findViewById(R.id.O_pos);
                on=findViewById(R.id.O_neg);
                ap=findViewById(R.id.A_pos);
                an=findViewById(R.id.A_neg);
                bp=findViewById(R.id.B_pos);
                bn=findViewById(R.id.B_neg);
                abp=findViewById(R.id.AB_pos);
                abn=findViewById(R.id.AB_neg);

                op.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(op.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(on.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                an.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(an.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(bp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(bn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(abp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blood_group.setText(abn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
            }
        });

        postRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean complete=true;
                if(patient_name.length()==0)
                {
                    complete=false;
                    patient_name.setError("Required");
                }
                if(age.length()==0)
                {
                    complete=false;
                    age.setError("Required");
                }
                if(blood_group.length()==0)
                {
                    complete=false;
                    blood_group.setError("Required");
                }
                if(required_units.length()==0)
                {
                    complete=false;
                    required_units.setError("Required");
                }
                if(location.length()==0)
                {
                    complete=false;
                    location.setError("Required");
                }
                if(documents.length()==0)
                {
                    complete=false;
                    documents.setError("Required");
                }
                if(idProof.length()==0)
                {
                    complete=false;
                    idProof.setError("Required");
                }


                if(complete==true)
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


                    if(uri!=null && uri1!=null)
                    {
                        db.collection("Raised Requests").document(currentUser.getPhoneNumber())
                                .set(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(PostBloodRequestFormActivity.this,"Request Sent",Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(PostBloodRequestFormActivity.this, "Error in posting request, try after sometime", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            progressDialog.show();
            uri=data.getData();
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri uri1 = uriTask.getResult();
                    patient.setPdfUri(uri1.toString());
                    documents.setText(data.getDataString());
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
        }
        if(requestCode==10 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            progressDialog.show();
            uri1=data.getData();
            storageReference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri uri2 = uriTask.getResult();
                    patient.setIdProof(uri2.toString());
                    idProof.setText(data.getDataString());
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
        }
    }

}