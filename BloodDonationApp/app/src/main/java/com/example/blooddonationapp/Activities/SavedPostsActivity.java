package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.blooddonationapp.Adapters.FeedAdapter;
import com.example.blooddonationapp.ModelClasses.Feed;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SavedPostsActivity extends AppCompatActivity {

    ImageView back;

    private FirebaseFirestore fireStore;
    RecyclerView savedFeedsRv;
    FeedAdapter.RvClickListener clickListener;
    List<Feed> feedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);

        back=findViewById(R.id.back);
        back.setOnClickListener(view -> onBackPressed() );

        savedFeedsRv = findViewById(R.id.savedFeedsRV);
        savedFeedsRv.setLayoutManager(new LinearLayoutManager(SavedPostsActivity.this));

        fireStore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // get the saved feeds
        fireStore.collection("Users").document( currentUser.getPhoneNumber() )
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                assert user != null;

                feedList = new LinkedList<>();
                setRvOnClickListener();

                Log.d("feedList", feedList.toString());
                FeedAdapter feedAdapter = new FeedAdapter(SavedPostsActivity.this, feedList, clickListener);
                savedFeedsRv.setAdapter(feedAdapter);

                // -----iterate and get the UID of each saved feed
                for(Map.Entry<String, Boolean> feedUID : user.getSavedFeeds().entrySet())
                {
                    Log.d("Feed", feedUID.getKey());

                    // now get the feeds from Feed collection
                    fireStore.collection("Feed").document( feedUID.getKey() )
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Feed feed = documentSnapshot.toObject(Feed.class);
                            feedList.add(0, feed);

                            Log.d("feedList", feedList.toString());
                            feedAdapter.notifyDataSetChanged();
                        }
                    });
                }
                // ------- got all saved feeds in feedList
            }
        });



    }

    private void setRvOnClickListener() {
        clickListener = new FeedAdapter.RvClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(SavedPostsActivity.this, WebViewActivity.class);
                intent.putExtra("link", feedList.get(position).getLink());
                startActivity(intent);
            }
        };
    }
}