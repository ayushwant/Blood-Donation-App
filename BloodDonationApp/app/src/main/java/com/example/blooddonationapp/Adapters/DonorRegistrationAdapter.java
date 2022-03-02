package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.ActivitiesAdmin.DonorRegistrationDetail;
import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.R;

import java.util.ArrayList;

public class DonorRegistrationAdapter extends RecyclerView.Adapter<DonorRegistrationAdapter.DonorRegistrationViewHolder>{
    Context context;
    ArrayList<Donor> donorArrayList;

    public DonorRegistrationAdapter(Context context, ArrayList<Donor> donorArrayList) {
        this.context = context;
        this.donorArrayList = donorArrayList;
    }

    @NonNull
    @Override
    public DonorRegistrationAdapter.DonorRegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_donors,parent,false);
        return new DonorRegistrationAdapter.DonorRegistrationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorRegistrationAdapter.DonorRegistrationViewHolder holder, int position) {
        Donor donor=donorArrayList.get(position);
        holder.name.setText(donor.getName());
        holder.phone.setText(donor.getPhone());
        holder.location.setText(donor.getLocation());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(holder.itemView.getContext(),DonorRegistrationDetail.class);
                i.putExtra("phone",donor.getPhone());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donorArrayList.size();
    }

    public static class DonorRegistrationViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,phone,location;
        public DonorRegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.donor_name);
            phone=itemView.findViewById(R.id.donor_number);
            location=itemView.findViewById(R.id.donor_location);
        }
    }

}
