package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDonorRegistrationFormBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class DonorRegistrationFormActivity extends AppCompatActivity {

    private EditText donorName,donorPhone,donorAge,donorEmail,blood_group,donorLocation,donorPdfUri;
    private View bloodList;
    private ImageView drop_up;
    private Button register;
    Donor donor=new Donor();
    private CheckBox isPublicBox;
    ActivityDonorRegistrationFormBinding binding;
    private FirebaseAuth mAuth;
    private Uri uri;
    private String patientName,patientNumber;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    FirebaseFirestore db,db1,db2,db3;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDonorRegistrationFormBinding .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Progress Bar while loading pdf
        progressDialog = new ProgressDialog(DonorRegistrationFormActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading pdf");



        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db1 = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();
        db3 = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference()
                .child("Documents").child(mAuth.getCurrentUser().getPhoneNumber());


        donorName=findViewById(R.id.name);
        donorPhone=findViewById(R.id.phone);
        donorAge=findViewById(R.id.age);
        donorEmail=findViewById(R.id.email);
        blood_group=findViewById(R.id.blood_group);
        donorLocation=findViewById(R.id.location);
        donorPdfUri=findViewById(R.id.upload_documents);
        isPublicBox=findViewById(R.id.isPublic);
        register=findViewById(R.id.register);
        bloodList=findViewById(R.id.blood_list);
        drop_up=findViewById(R.id.drop_up);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Uploading documents
        donorPdfUri.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent i= new Intent();
        i.setType("application/pdf");
        i.setAction(i.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"PDF FILE SELECTED"),12);
             }
        });


        binding.bloodGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                binding.bloodList.setVisibility(View.VISIBLE);
                binding.dropUp.setVisibility(View.VISIBLE);
                binding.dropUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodList.setVisibility(View.GONE);
                        binding.dropUp.setVisibility(View.GONE);
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
                        binding.bloodGroup.setText(op.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);

                    }
                });
                on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(on.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);

                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                an.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(an.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                ap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(ap.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(bp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                bn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(bn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(abp.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
                abn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.bloodGroup.setText(abn.getText().toString());
                        bloodList.setVisibility(View.GONE);
                        drop_up.setVisibility(View.GONE);
                    }
                });
            }
        });
        db1.collection("Users").document(currentUser.getPhoneNumber())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot dc) {
                donorName.setText(dc.getString("name"));
                donorPhone.setText(currentUser.getPhoneNumber());
                donorEmail.setText(dc.getString("email"));
                blood_group.setText(dc.getString("bloodGrp"));
                donorLocation.setText(dc.getString("address"));
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean complete=true;
                if(donorName.length()==0)
                {
                    complete=false;
                    donorName.setError("Required");
                }
                if(donorPhone.length()==0)
                {
                    complete=false;
                    donorPhone.setError("Required");
                }
                if(donorAge.length()==0)
                {
                    complete=false;
                    donorAge.setError("Required");
                }
                if(blood_group.length()==0)
                {
                    complete=false;
                    blood_group.setError("Required");
                }
                if(donorEmail.length()==0)
                {
                    complete=false;
                    donorEmail.setError("Required");
                }
                if(donorLocation.length()==0)
                {
                    complete=false;
                    donorLocation.setError("Required");
                }
                if(donorPdfUri.length()==0)
                {
                    complete=false;
                    donorPdfUri.setError("Required");
                }
                if(complete==true)
                {
                    donor.setName(donorName.getText().toString());
                    donor.setPhone(donorPhone.getText().toString());
                    donor.setAge(donorAge.getText().toString());
                    donor.setEmail(donorEmail.getText().toString());
                    donor.setBloodGrp(blood_group.getText().toString());
                    donor.setLocation(donorLocation.getText().toString());
                    donor.setValid(false);
                    donor.setPublic(false);
                    if(isPublicBox.isChecked())
                        donor.setPublic(true);

                    db.collection("Donor Requests").document(currentUser.getPhoneNumber())
                            .set(donor).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(DonorRegistrationFormActivity.this,"Details Submitted",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(DonorRegistrationFormActivity.this,"Error in posting request, try after sometime",Toast.LENGTH_LONG).show();
                        }
                    });



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
                    donor.setPdfUri(uri1.toString());
                    donorPdfUri.setText(data.getDataString());
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });

        }
    }



}
