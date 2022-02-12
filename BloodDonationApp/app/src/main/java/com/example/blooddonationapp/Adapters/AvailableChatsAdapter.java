package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Activities.AvailableChats;
import com.example.blooddonationapp.Activities.MessageRoomActivity;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AvailableChatsAdapter  extends RecyclerView.Adapter<AvailableChatsAdapter.AvailableChatsViewHolder>
{
    private FirebaseFirestore db;
    private DocumentReference ref;
    String str;
    Context context;
    ArrayList<User> usersList;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    //HERE: our constructor
    public AvailableChatsAdapter(Context context, ArrayList<User> userslist) {
        this.context = context;
        this.usersList = userslist;
    }

    @NonNull
    @Override
    public AvailableChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(context).inflate(R.layout.sample_row_available_chats, parent, false);

        // can do this if context is not asked in constructor
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_row, parent, false);
        return new AvailableChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableChatsViewHolder holder, int position)
    {
        User user = usersList.get(position);

//        if(user.getName()!=null)
//        holder.availableChatsName.setText(user.getName());

        // get name and image uri from firestore
        db = FirebaseFirestore.getInstance();
        ref = db.collection("Users").document(user.getPhone());

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    {
                        holder.availableChatsName.setText(documentSnapshot.getString("name"));
                        str=documentSnapshot.getString("imgUri");
                        Glide.with(context).load(str)
                                .placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.availableChatsProfilePic);
                    }
                }
            }
        });

        // Both above and below methods work!!!!

//        DocumentReference docRef = db.collection("Users").document(user.getPhone());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists())
//                    {
//                        holder.availableChatsName.setText( document.getString("name"));
//                        str=document.getString("imgUri");
//                        Glide.with(context).load(str)
//                                .placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.availableChatsProfilePic);
//                    }
//                    else
//                    {
//                        Toast.makeText(context, "User not available", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else {
//                    Toast.makeText(context, "Can't get user data", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        // start messaging now
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MessageRoomActivity.class);
                i.putExtra("name", user.getName());
                i.putExtra("phone", user.getPhone());
                i.putExtra("uri", user.getImgUri());
                i.putExtra("uid", user.getUid());

                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //HERE: our nested view holder class. It holds the view of each item/row in our RV
    public static class AvailableChatsViewHolder extends RecyclerView.ViewHolder
    {
        ImageView availableChatsProfilePic;
        TextView availableChatsName;

        public AvailableChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            // binding the elements
            availableChatsProfilePic = itemView.findViewById(R.id.availableChatsProfilePic);
            availableChatsName = itemView.findViewById(R.id.availableChatsName);
        }
    }
}
