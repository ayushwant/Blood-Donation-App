package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.ModelClasses.Feed;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.FeedSampleRowBinding;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder>
{
    Context context;
    ArrayList<Feed> feedArrayList;

    public FeedAdapter(Context context, ArrayList<Feed> feedArrayList) {
        this.context = context;
        this.feedArrayList = feedArrayList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(context).inflate(R.layout.feed_sample_row, parent, false);

        // can do this if context is not asked in constructor
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_row, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Feed feed = feedArrayList.get(position);

//        holder.feedImg.setImageResource(R.drawable.background);
        holder.feedImg.setImageResource(feed.getImage());

        holder.feedText.setText(feed.getText());
        holder.saveBtn.setImageResource(R.drawable.save_alt_24);
        if(feed.isLiked()) holder.likeBtn.setImageResource(R.drawable.liked);
        else holder.likeBtn.setImageResource(R.drawable.unliked);

        holder.shareBtn.setImageResource(R.drawable.ic_baseline_share_24);


    }

    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    //here: our nested view holder class. It holds the view of each item/row in our RV
    public static class FeedViewHolder extends RecyclerView.ViewHolder
    {
        ImageView feedImg;
        ImageView likeBtn;
        ImageView shareBtn;
        ImageView saveBtn;

        TextView feedText;
        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            feedText = itemView.findViewById(R.id.feedText);
            feedImg = itemView.findViewById(R.id.feedImg);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            saveBtn = itemView.findViewById(R.id.saveBtn);
        }

//        private  FeedSampleRowBinding binding;
//
//        public FeedViewHolder(FeedSampleRowBinding binding){
//            super(binding.getRoot());
//            this.binding = binding;
//        }

    }

}
