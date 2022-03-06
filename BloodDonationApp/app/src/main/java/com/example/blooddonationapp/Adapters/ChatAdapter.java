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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>
{

    private FirebaseFirestore firestoreDb;
    private DocumentReference ref;
    String str;
    Context context;
    ArrayList<User> usersList;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase realtimeDb;

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
        realtimeDb = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null)
        {
            String room = currentUser.getUid() + "__" + user.getUid();
            realtimeDb.getReference().child("chats").child(room)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String lastMsg = (String) snapshot.child("lastMsg").getValue();
                            Long lastMsgTime = (Long) snapshot.child("lastMsgTime").getValue();

                            String lastMsgTimeString = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastMsgTime), ZoneId.systemDefault());

                                LocalDateTime currentDate = LocalDateTime.now();

                                String s1 = ""+ date.getYear()+date.getMonth();
                                String s2 = ""+ currentDate.getYear() + currentDate.getMonth();

                                if(s1.equals(s2) ) {
                                    if (date.getDayOfMonth()==currentDate.getDayOfMonth()) // if same date
                                        lastMsgTimeString = date.getHour() + ":" + date.getMinute();

                                    else if( currentDate.getDayOfMonth() - date.getDayOfMonth() ==1 )
                                        lastMsgTimeString = "Yesterday";
                                }
                                else
                                    lastMsgTimeString = date.getDayOfMonth() +" " +
                                            date.getMonth().toString().charAt(0) + date.getMonth().toString().substring(1,3).toLowerCase();
                            }

                            if(lastMsg!=null)
                                holder.chatLastMsg.setText(lastMsg);
//                            if(lastMsgTime!=null)
//                                holder.chatMsgTime.setText(lastMsgTime+"");
                            if(lastMsgTimeString!=null)
                                holder.chatMsgTime.setText(lastMsgTimeString);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

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
