package com.example.blooddonationapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{

    Context context;
    ArrayList<Patient> patientArrayList;

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

   /* private void showDialog()
    {
        final Dialog dialog;
       // dialog = new Dialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        //

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }*/

}
