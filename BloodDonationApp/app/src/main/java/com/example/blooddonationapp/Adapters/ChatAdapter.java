package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Activities.MessageRoomActivity;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>
{

    private FirebaseFirestore firestoreDb;
    private DocumentReference ref;
    String str;
    Context context;
    ArrayList<User> usersList;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    //HERE: our constructor
    public ChatAdapter(Context context, ArrayList<User> userslist) {
        this.context = context;
        this.usersList = userslist;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(context).inflate(R.layout.sample_row_chats, parent, false);

        // can do this if context is not asked in constructor
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_row, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User user = usersList.get(position);

        // get name and image uri from firestore
        firestoreDb = FirebaseFirestore.getInstance();
        ref = firestoreDb.collection("Users").document(user.getPhone());

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    {
                        holder.chatName.setText(documentSnapshot.getString("name"));
                        str=documentSnapshot.getString("imgUri");
                        Glide.with(context).load(str)
                                .placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.chatProfilePic);
                    }
                }
            }
        });

        // get last msg and its time from realtime database

        // start messaging now
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MessageRoomActivity.class);
                i.putExtra("chatPartner", user);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //HERE: our nested view holder class. It holds the view of each item/row in our RV
    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        ImageView chatProfilePic;
        TextView chatName;
        TextView chatLastMsg;
        TextView chatMsgTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            // binding the elements
            chatProfilePic = itemView.findViewById(R.id.chatProfilePic);
            chatName = itemView.findViewById(R.id.chatName);
            chatLastMsg = itemView.findViewById(R.id.lastMsg);
            chatMsgTime = itemView.findViewById(R.id.lastMsgTime);
        }
    }
}
