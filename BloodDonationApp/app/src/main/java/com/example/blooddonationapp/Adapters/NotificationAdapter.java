package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>
{

    Context context;
    ArrayList<Notification> arrayList;

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
        return new NotificationAdapter.NotificationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position)
    {
        Notification notification=arrayList.get(position);
        holder.line1.setText(notification.getLine1());
        holder.line2.setText(notification.getLine2());
        holder.line3.setText(notification.getLine3());
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
