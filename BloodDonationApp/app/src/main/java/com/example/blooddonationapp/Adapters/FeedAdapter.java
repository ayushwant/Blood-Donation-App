package com.example.blooddonationapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Activities.WebViewActivity;
import com.example.blooddonationapp.ModelClasses.Feed;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder>
{
    Context context;
    List<Feed> feedArrayList;
    RvClickListener clickListener;
    // check for like and save on fireStore
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    private boolean centerCrop = true;


    public FeedAdapter(Context context, List<Feed> feedArrayList, RvClickListener listener) {
        this.context = context;
        this.feedArrayList = feedArrayList;
        this.clickListener = listener;
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
        holder.feedImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // idk why but this line needs to be there
        // for dynamic scaleType setting to work properly

        // change image view type on click
        holder.feedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(centerCrop) {
                    holder.feedImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    centerCrop = false;
                }
                else{
                    holder.feedImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    centerCrop = true;
                }

            }
        });

        Glide.with(context).load(feed.getImage()).into(holder.feedImg);
        holder.feedText.setText(feed.getText());

        holder.shareBtn.setImageResource(R.drawable.ic_baseline_share_24);

        holder.shareBtn.setOnClickListener(view -> {

            BitmapDrawable drawable = (BitmapDrawable) holder.feedImg.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    bitmap, "title", "");
            Uri imageUri = Uri.parse(bitmapPath);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");

            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.putExtra(Intent.EXTRA_TEXT,
                    feed.getText() +"\n" + feed.getLink());
            // can insert only one extra text at a time. So just append into a single message

            //            intent.putExtra(Intent.EXTRA_TEXT,
//            "PlayStore Link: https://play.google.com/store/apps/details?id=" +context.getPackageName());

            context.startActivity(Intent.createChooser(intent, "Share image via..."));

        });

        // check in database for liked and saved
        if(currentUser!=null && currentUser.getPhoneNumber()!=null )
        {
            fireStore.collection("Users").document( currentUser.getPhoneNumber() )
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);

                            assert user != null;
                            if(user.getLikedFeeds()==null)
                                user.setLikedFeeds(new HashMap<>());
                            if(user.getLikedFeeds().containsKey(feed.getUid() ) )
                                holder.likeBtn.setImageResource(R.drawable.liked);
                            else
                                holder.likeBtn.setImageResource(R.drawable.unliked);


                            if(user.getSavedFeeds()==null)
                                user.setSavedFeeds(new HashMap<>());
                            if(user.getSavedFeeds().containsKey(feed.getUid() ) )
                                holder.saveBtn.setImageResource(R.drawable.ic_saved);
                            else
                                holder.saveBtn.setImageResource(R.drawable.ic_unsaved_again);
                        }
                    });
        }

        // listen for like button clicks
        holder.likeBtn.setOnClickListener( view -> likeButtonCLick(holder, feed) );

        // listen for save button clicks
        holder.saveBtn.setOnClickListener( view -> saveButtonCLick(holder, feed) );
    }

    private void saveButtonCLick(FeedViewHolder holder, Feed feed)
    {
        if (currentUser != null && currentUser.getPhoneNumber() != null)
        {
            fireStore.collection("Users").document(currentUser.getPhoneNumber())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    User user = documentSnapshot.toObject(User.class);

                    assert user != null;
                    Map<String , Boolean> savedFeeds = user.getSavedFeeds();

                    if(savedFeeds==null)
                        savedFeeds=new HashMap<>();
                    if(savedFeeds.containsKey(feed.getUid() ) ) // was saved
                    {
                        holder.saveBtn.setImageResource(R.drawable.ic_unsaved_again);
                        savedFeeds.remove(feed.getUid());
                    }
                    else // was unsaved
                    {
                        holder.saveBtn.setImageResource(R.drawable.ic_saved);
                        savedFeeds.put(feed.getUid(), true);
                    }
                    fireStore.collection("Users").document(currentUser.getPhoneNumber())
                            .update( "savedFeeds", savedFeeds );
                }
            });
        }
    }

    private void likeButtonCLick(FeedViewHolder holder, Feed feed)
    {
        if (currentUser != null && currentUser.getPhoneNumber() != null)
        {
            fireStore.collection("Users").document(currentUser.getPhoneNumber())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    User user = documentSnapshot.toObject(User.class);

                    assert user != null;
                    Map<String , Boolean> likedFeeds = user.getLikedFeeds();

                    if(likedFeeds==null)
                        likedFeeds=new HashMap<>();
                    if(likedFeeds.containsKey(feed.getUid() ) ) // was liked
                    {
                        holder.likeBtn.setImageResource(R.drawable.unliked);
                        likedFeeds.remove(feed.getUid());
                    }
                    else // was unliked
                    {
                        holder.likeBtn.setImageResource(R.drawable.liked);
                        likedFeeds.put(feed.getUid(), true);
                    }
                    // now update in fireStore
                    fireStore.collection("Users").document(currentUser.getPhoneNumber())
                            .update( "likedFeeds", likedFeeds );
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    //here: our nested view holder class. It holds the view of each item/row in our RV
    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView feedImg;
        LottieAnimationView likeBtn;
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

            itemView.setOnClickListener(this);  // open WebView on item click
            // the clicks on other view of the item will be override with their specific click responses
            // Thus, this method responds to clicks on the empty spaces of the RV, or on the views
            // whose click listeners haven't been set
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());
        }

//        private  FeedSampleRowBinding binding;
//
//        public FeedViewHolder(FeedSampleRowBinding binding){
//            super(binding.getRoot());
//            this.binding = binding;
//        }

    }

    public interface RvClickListener {
        void onClick(View v, int position);
    }

}
