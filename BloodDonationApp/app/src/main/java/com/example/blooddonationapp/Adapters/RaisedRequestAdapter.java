package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.ActivitiesAdmin.RequestDetailAdmin;
import com.example.blooddonationapp.ModelClasses.Patient;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RaisedRequestAdapter extends RecyclerView.Adapter<RaisedRequestAdapter.RaisedRequestViewHolder>{
    Context context;
    ArrayList<Patient> patientArrayList;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseFirestore db;

    public RaisedRequestAdapter(Context context, ArrayList<Patient> patientArrayList) {
        this.context = context;
        this.patientArrayList = patientArrayList;
    }

    @NonNull
    @Override
    public RaisedRequestAdapter.RaisedRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_request,parent,false);
        return new RaisedRequestAdapter.RaisedRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RaisedRequestAdapter.RaisedRequestViewHolder holder, int position) {
        Patient patient = patientArrayList.get(position);
        holder.name.setText(patient.getUserName());
        holder.bloodGrp.setText(patient.getBloodGrp());
        holder.location.setText(patient.getLocation());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(holder.itemView.getContext(), RequestDetailAdmin.class);
                i.putExtra("phone",patient.getUserPhone());
                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return patientArrayList.size();
    }

    public static class RaisedRequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,bloodGrp,location;
        public RaisedRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.request_list_name);
            bloodGrp=itemView.findViewById(R.id.request_list_blood_grp);
            location=itemView.findViewById(R.id.request_list_location);
        }
    }

}
