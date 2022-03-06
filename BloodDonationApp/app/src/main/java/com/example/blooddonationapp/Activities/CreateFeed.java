package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.ModelClasses.Feed;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityCreateFeedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.UUID;

public class CreateFeed extends AppCompatActivity {
    ActivityCreateFeedBinding binding;
    FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST=1;//To identify our image request
    Uri feedImgUri;
    FirebaseStorage storage;
    String finalUri;
    Feed feed = new Feed();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.feedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser(); // choose file
            }
        });

        binding.feedUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDataToFireStore();
            }
        });

    }

    private void openFileChooser() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK
                && data != null && data.getData() != null)
        {
            feedImgUri = data.getData();

            //getting back the uri of image
//            feed.setImage(data.getData());
//            activityUserProfileBinding.profilePic.setImageURI(imageUri);
        }
    }

    private void addDataToFireStore()
    {
        // creating a collection reference
        // for our Firebase Firestore database.
        Date date = new Date();

        StorageReference reference =storage.getReference()
                .child("Feed").child(String.valueOf(date.getTime()));

        // first upload image to firebase
        reference.putFile(feedImgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            finalUri = uri.toString();
                            feed.setImage(finalUri);
                            Toast.makeText(CreateFeed.this,finalUri,Toast.LENGTH_LONG).show();

                            // -----> image uri set hone par hi feed object me data daalo and firestore pe upload karo. Warna null upload hoga imageUri
                            feed.setText(binding.addFeedText.getText().toString());
                            feed.setLink(binding.addFeedLink.getText().toString());
                            feed.setUid( UUID.randomUUID().toString() );
                            feed.setTimeCreated( String.valueOf(date.getTime()) );

                            // below method is use to add data to Firebase Firestore.
                            db.collection("Feed").document( feed.getTimeCreated() ).set(feed) // String.valueOf(date.getTime())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            Toast.makeText(CreateFeed.this, "Your Feed has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateFeed.this, "Can't update Feed has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // ----->
                        }

                    });
                }
            }
        });



        Toast.makeText(CreateFeed.this,"2nd time " +finalUri,Toast.LENGTH_LONG).show();
    }


}