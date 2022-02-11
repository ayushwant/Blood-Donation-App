package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.ModelClasses.Message;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

// will just tell how the messages will look and react in the RecyclerView
public class MessagesAdaptor extends RecyclerView.Adapter
{
    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;


    public MessagesAdaptor(Context context, ArrayList<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    // to return the type of view : sent_message or received_message
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sent_message, parent, false);
            return new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.received_message, parent, false);
            return new ReceiverViewHolder(view);
        }
    }
    // coz we have 2 views
    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);

        if(FirebaseAuth.getInstance().getUid().equals(msg.getSenderID())) {
            return ITEM_SENT;
        }
        else return ITEM_RECEIVE;

//        return super.getItemViewType(position);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Message msg = messages.get(position);
        if(holder.getClass()==SentViewHolder.class)
        {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            if(msg.isHasImageAttachment())
            {
                viewHolder.sentAttachment.setVisibility(View.VISIBLE);
                Glide.with(context).load(msg.getImageUrl()).placeholder(R.drawable.pic1).into(viewHolder.sentAttachment);
                viewHolder.sentChat.setVisibility(View.GONE);
            }

            viewHolder.sentChat.setText(msg.getMsg());
        }
        else
        {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            if(msg.isHasImageAttachment())
            {
                viewHolder.receivedAttachment.setVisibility(View.VISIBLE);
                Glide.with(context).load(msg.getImageUrl()).placeholder(R.drawable.pic1).into(viewHolder.receivedAttachment);
                viewHolder.receivedChat.setVisibility(View.GONE);
            }

            viewHolder.receivedChat.setText((msg.getMsg()));
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // 2 view holder classes for 2 types of chats
    public class SentViewHolder  extends RecyclerView.ViewHolder{
        TextView sentChat;
        ImageView sentAttachment;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            sentChat = itemView.findViewById(R.id.sentMsg);
            sentAttachment = itemView.findViewById(R.id.sentImageAttachment);
        }
    }

    public class ReceiverViewHolder  extends RecyclerView.ViewHolder{
        TextView receivedChat;
        ImageView receivedAttachment;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedChat = itemView.findViewById(R.id.received_Msg);
            receivedAttachment = itemView.findViewById(R.id.receivedImageAttachment);
        }
    }
}
