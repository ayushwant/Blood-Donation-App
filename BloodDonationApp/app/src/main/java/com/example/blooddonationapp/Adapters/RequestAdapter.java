package com.example.blooddonationapp.Adapters;

import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Activities.RegisteredMsg;
import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{

    private EditText donorName,donorPhone,donorAge,donorEmail,donorBloodGrp,donorLocation,donorPdfUri;
    private Button register;
    private CheckBox isPublicBox;
    private Boolean isValid=false;
    private Boolean isPublic=false;

    Context context;
    ArrayList<Patient> patientArrayList;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseFirestore db,db1,db2,db3;
    public RequestAdapter(Context context, ArrayList<Patient> patientArrayList) {
        this.context = context;
        this.patientArrayList = patientArrayList;
    }

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.item_request,parent,false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.RequestViewHolder holder, int position) {
        Patient patient = patientArrayList.get(position);
        holder.name.setText(patient.getUserName());
        holder.bloodGrp.setText(patient.getBloodGrp());
        holder.location.setText(patient.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialog();
                final Dialog dialog;
                dialog = new Dialog(holder.itemView.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet_request_detail);

                //Loading details from firebase firestore
                TextView userName,patientName,bloodGrp,units,age,location;
                Button donate;

                userName=dialog.findViewById(R.id.name);
                patientName=dialog.findViewById(R.id.patient_name);
                bloodGrp=dialog.findViewById(R.id.blood_group);
                units=dialog.findViewById(R.id.units);
                age=dialog.findViewById(R.id.age);
                location=dialog.findViewById(R.id.location);
                donate=dialog.findViewById(R.id.donate);

                userName.setText(patient.getUserName());
                patientName.setText(patient.getPatientName());
                bloodGrp.setText(patient.getBloodGrp());
                units.setText(patient.getRequiredUnits());
                age.setText(patient.getAge());
                location.setText(patient.getLocation());

                donate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mAuth=FirebaseAuth.getInstance();
                        currentUser=mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        db1 = FirebaseFirestore.getInstance();
                        db2 = FirebaseFirestore.getInstance();
                        db3 = FirebaseFirestore.getInstance();
                        db.collection("Registered Donors").document(currentUser.getPhoneNumber())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists())
                                {
                                    Map<String,Object> m=new HashMap<>();
                                    m.put("Donor Number",currentUser.getPhoneNumber());
                                    m.put("Patient Number",patient.getUserPhone());
                                    //Request Send
                                    Toast.makeText(holder.itemView.getContext(), "Request Sent",Toast.LENGTH_LONG).show();
                                    db3.collection("Blood Donation Request").
                                            document(currentUser.getPhoneNumber()+"-"+patient.getUserPhone())
                                            .set(m);

                                    //Notify both donor and seeker


                                }
                                else
                                {
                                    db2.collection("Donor Requests").document(currentUser.getPhoneNumber())
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().exists())
                                            {
                                                Toast.makeText(holder.itemView.getContext(),
                                                        "Your request has been registered, we will contact you soon " +
                                                                "after manual verification of documents",Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                //Directly Register
                                                dialog.cancel();
                                                //Opening other dialog
                                                final Dialog dialog1;
                                                dialog1 = new Dialog(holder.itemView.getContext());
                                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog1.setContentView(R.layout.bottom_sheet_donor_registration);


                                                donorName=dialog1.findViewById(R.id.name);
                                                donorPhone=dialog1.findViewById(R.id.phone);
                                                donorAge=dialog1.findViewById(R.id.age);
                                                donorEmail=dialog1.findViewById(R.id.email);
                                                donorBloodGrp=dialog1.findViewById(R.id.blood_group);
                                                donorLocation=dialog1.findViewById(R.id.location);
                                                donorPdfUri=dialog1.findViewById(R.id.upload_documents);
                                                isPublicBox=dialog1.findViewById(R.id.isPublic);
                                                register=dialog1.findViewById(R.id.register);

                                                db1.collection("Users").document(currentUser.getPhoneNumber())
                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot dc) {
                                                        donorName.setText(dc.getString("name"));
                                                        donorPhone.setText(currentUser.getPhoneNumber());
                                                        donorEmail.setText(dc.getString("email"));
                                                        donorBloodGrp.setText(dc.getString("bloodGrp"));
                                                        donorLocation.setText(dc.getString("address"));
                                                    }
                                                });


                                                register.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        if(donorName.length()==0)
                                                        {
                                                            donorName.setError("Required");
                                                        }
                                                        if(donorPhone.length()==0)
                                                        {
                                                            donorPhone.setError("Required");
                                                        }
                                                        if(donorAge.length()==0)
                                                        {
                                                            donorAge.setError("Required");
                                                        }
                                                        if(donorBloodGrp.length()==0)
                                                        {
                                                            donorBloodGrp.setError("Required");
                                                        }
                                                        if(donorEmail.length()==0)
                                                        {
                                                            donorEmail.setError("Required");
                                                        }
                                                        if(donorLocation.length()==0)
                                                        {
                                                            donorLocation.setError("Required");
                                                        }
                                                        if(donorPdfUri.length()==0)
                                                        {
                                                            donorPdfUri.setError("Required");
                                                        }
                                                        else
                                                        {
                                                            Donor donor=new Donor();
                                                            donor.setName(donorName.getText().toString());
                                                            donor.setPhone(donorPhone.getText().toString());
                                                            donor.setAge(donorAge.getText().toString());
                                                            donor.setEmail(donorEmail.getText().toString());
                                                            donor.setBloodGrp(donorBloodGrp.getText().toString());
                                                            donor.setLocation(donorLocation.getText().toString());
                                                            donor.setPdfUri(donorPdfUri.getText().toString());
                                                            donor.setValid(false);
                                                            donor.setPublic(false);
                                                            if(isPublicBox.isChecked())
                                                                donor.setPublic(true);

                                                            db.collection("Donor Requests").document(currentUser.getPhoneNumber())
                                                                    .set(donor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(holder.itemView.getContext(),"Details Submitted",Toast.LENGTH_LONG).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Toast.makeText(holder.itemView.getContext(),"Error in posting request, try after sometime",Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                                dialog1.show();
                                                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                                dialog1.getWindow().setGravity(Gravity.BOTTOM);
                                            }
                                        }
                                     });


                                }
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
        });

    }

    @Override
    public int getItemCount() {
        return patientArrayList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,bloodGrp,location;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.request_list_name);
            bloodGrp=itemView.findViewById(R.id.request_list_blood_grp);
            location=itemView.findViewById(R.id.request_list_location);
        }
    }


}
