package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Activities.DetailedNotification;
import com.example.blooddonationapp.Activities.MyRequestDetail;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.ModelClasses.RequestHistory;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestHistoryAdapter extends RecyclerView.Adapter<RequestHistoryAdapter.RequestHistoryViewHolder>{
    Context context;
    ArrayList<RequestHistory> arrayList;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    public RequestHistoryAdapter(Context context, ArrayList<RequestHistory> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RequestHistoryAdapter.RequestHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(context).inflate(R.layout.item_raised_request_history,parent,false);
        return new RequestHistoryViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RequestHistoryViewHolder holder, int position)
    {
        TextView patient_name,patient_blood_grp,patient_units,status,patient_location;

        RequestHistory  requestHistory=arrayList.get(position);
        holder.patient_name.setText(requestHistory.getPatientName());
        holder.patient_blood_grp.setText(requestHistory.getPatientBloodGrp());
        holder.patient_units.setText(Long.toString(requestHistory.getRequiredUnits()));
        holder.status.setText(requestHistory.getStatus());
        holder.patient_location.setText(requestHistory.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent(holder.itemView.getContext(), MyRequestDetail.class);
                Toast.makeText( holder.itemView.getContext(), requestHistory.getUserPhone(), Toast.LENGTH_SHORT).show();
                i.putExtra("Seeker Number",requestHistory.getUserPhone());
                holder.itemView.getContext().startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RequestHistoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView patient_name,patient_blood_grp,patient_units,status,patient_location;
        public RequestHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            patient_name=itemView.findViewById(R.id.patient_name);
            patient_blood_grp=itemView.findViewById(R.id.patient_blood_group);
            patient_units=itemView.findViewById(R.id.patient_units);
            status=itemView.findViewById(R.id.status);
            patient_location=itemView.findViewById(R.id.patient_location);

        }
    }

}
