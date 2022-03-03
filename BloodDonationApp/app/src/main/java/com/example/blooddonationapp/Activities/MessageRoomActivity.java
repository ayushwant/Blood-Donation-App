package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.blooddonationapp.Adapters.MessagesAdaptor;
import com.example.blooddonationapp.ModelClasses.Message;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityMessageRoomBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageRoomActivity extends AppCompatActivity {

    ActivityMessageRoomBinding binding;

    MessagesAdaptor messagesAdaptor;
    ArrayList<Message> messageArrayList;

    String senderRoom, receiverRoom;
    FirebaseDatabase realtimeDb;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    String receiverUid;
    String senderUid;

    ProgressDialog dialog;
    User chatPartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_message_room);

        binding = ActivityMessageRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.messagesBack.setOnClickListener(view -> onBackPressed() );

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading message...");

        // got the list of messages, adaptor and set to the RV
        messageArrayList = new ArrayList<>();
        messagesAdaptor = new MessagesAdaptor(this, messageArrayList);

        binding.messageRoomRV.setAdapter(messagesAdaptor);
        binding.messageRoomRV.setLayoutManager(new LinearLayoutManager(this));

        // get the current user and credentials
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        chatPartner = (User) getIntent().getParcelableExtra("chatPartner");
        binding.chatPartnerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MessageRoomActivity.this, ChatPartnerInfo.class);
                i.putExtra("chatPartner",chatPartner );
                startActivity(i);
            }
        });


        binding.chatPartnerName.setText(chatPartner.getName());

        receiverUid = chatPartner.getUid(); //getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid(); // uid of logged in user

//        String receiverPhone = getIntent().getStringExtra("mobile");
//        String senderPhone = currentUser.getPhoneNumber();

        // creating 2 different rooms to update the data differently for 2 diff users
        senderRoom = senderUid + "__" +receiverUid;
        receiverRoom = receiverUid +"__" +senderUid;

        realtimeDb = FirebaseDatabase.getInstance();

        // updating the Realtime DB with messages
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = binding.messageBox.getText().toString();

                Date date = new Date();

                Message msg = new Message(messageText, senderUid, date.getTime());
                binding.messageBox.setText("");

                HashMap<String,Object> lastMsgObj =new HashMap<>();
                lastMsgObj.put("lastMsg",msg.getMsg());
                lastMsgObj.put("lastMsgTime",date.getTime());

                // updating last messages in both rooms
                realtimeDb.getReference().child("chats").child(senderRoom).
                        updateChildren(lastMsgObj);
                realtimeDb.getReference().child("chats").child(receiverRoom).
                        updateChildren(lastMsgObj);


                //adding to the list of messages in both rooms
                realtimeDb.getReference().child("chats").child(senderRoom)
                        .child("messages").push().setValue(msg).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        // if updated sender room properly,then also add to receiver room
                        realtimeDb.getReference().child("chats")
                                .child(receiverRoom).child("messages").push()
                                .setValue(msg).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                    }
                });

            }
        });

        // notifying adapter and adding messages to RV
        realtimeDb.getReference().child("chats").child(senderRoom).child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageArrayList.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            Message message =snapshot1.getValue(Message.class);//Typecasting
                            messageArrayList.add(message);
                        }
                        messagesAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // also cater the attachments
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}