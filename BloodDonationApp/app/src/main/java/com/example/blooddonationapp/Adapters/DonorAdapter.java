package com.example.blooddonationapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Activities.RegisteredMsg;
import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.ModelClasses.Donor;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder>{
    Context context;
    ArrayList<Donor> donorArrayList;

    public DonorAdapter(Context context, ArrayList<Donor> donorArrayList) {
        this.context = context;
        this.donorArrayList = donorArrayList;
    }

    @NonNull
    @Override
    public DonorAdapter.DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_donors,parent,false);
        return new DonorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorAdapter.DonorViewHolder holder, int position) {
        Donor donor=donorArrayList.get(position);
        holder.name.setText(donor.getName());
        holder.phone.setText(donor.getPhone());
        holder.location.setText(donor.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialog();
                final Dialog dialog;
                dialog = new Dialog(holder.itemView.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet_donor_detail);

                TextView donorName,donorAge,donorBloodGrp,donorEmail,donorLocation;
                ImageView donorCall,donorShare;
                donorCall=dialog.findViewById(R.id.call);
                donorShare=dialog.findViewById(R.id.share);
                donorName=dialog.findViewById(R.id.name);
                donorAge=dialog.findViewById(R.id.age);
                donorBloodGrp=dialog.findViewById(R.id.blood_group);
                donorEmail=dialog.findViewById(R.id.email);
                donorLocation=dialog.findViewById(R.id.location);


                donorName.setText(donor.getName());
                donorAge.setText(donor.getAge());
                donorBloodGrp.setText(donor.getBloodGrp());
                donorEmail.setText(donor.getEmail());
                donorLocation.setText(donor.getLocation());

                //Calling Donor
                donorCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(holder.itemView.getContext(),"Call",Toast.LENGTH_LONG).show();
                    }
                });

                //Share Feature
                donorShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);

                        String msg = donor.getName() + " is available to donate " +donor.getBloodGrp()
                                + " blood at " +donor.getLocation();

                        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        dialog.getContext().startActivity(shareIntent);
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
        return donorArrayList.size();
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,phone,location;
        public DonorViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.donor_name);
            phone=itemView.findViewById(R.id.donor_number);
            location=itemView.findViewById(R.id.donor_location);
        }
    }
}
