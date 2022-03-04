package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Activities.DetailedNotification;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>
{

    Context context;
    ArrayList<Notification> arrayList;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    public NotificationAdapter(Context context, ArrayList<Notification> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(context).inflate(R.layout.item_notification,parent,false);
        return new NotificationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position)
    {
        Notification notification=arrayList.get(position);
        holder.line1.setText(notification.getLine1());
        holder.line2.setText(notification.getLine2());
        holder.line3.setText(notification.getLine3());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                notification.setSeen(true);
                DatabaseReference database;
                auth= FirebaseAuth.getInstance();
                currentUser=auth.getCurrentUser();
                database= FirebaseDatabase.getInstance().getReference("Notifications").
                        child(currentUser.getPhoneNumber());
                Map<String,Object> m=new HashMap<String,Object>();
                m.put(notification.getKey(),notification);
                database.updateChildren(m);

                Intent i=new Intent(holder.itemView.getContext(), DetailedNotification.class);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        TextView line1,line2,line3;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            line1=itemView.findViewById(R.id.line1);
            line2=itemView.findViewById(R.id.line2);
            line3=itemView.findViewById(R.id.line3);

        }
    }
}
