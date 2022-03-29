package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.ModelClasses.User;

import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri imageUri;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting current user from firebase authentication
        auth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        //Register as donor button
        binding.donorRegistration1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ProfileActivity.this,DonorRegistrationFormActivity.class);
                startActivity(i);
            }
        });

        //Form filled but not verified
        db.collection("Donor Requests").document(currentUser.getPhoneNumber()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            binding.donorRegistration.setVisibility(View.VISIBLE);
                            binding.donorRegistration1.setVisibility(View.GONE);
                            binding.donorRegistration.setText("Donor Registration form filled, verification pending");
                        }
                    }
                });

        //Verified donors
        db.collection("Registered Donors").document(currentUser.getPhoneNumber()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            binding.donorRegistration.setVisibility(View.VISIBLE);
                            binding.donorRegistration1.setVisibility(View.GONE);
                            binding.donorRegistration.setText("Verified Donor");
                        }
                    }
                });

        //Loading user details from firestore database
        db.collection("Users").document(currentUser.getPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        binding.name.setText(documentSnapshot.getString("name"));
                        binding.name1.setText(documentSnapshot.getString("name"));
                        binding.contactNumber.setText(documentSnapshot.getString("phone"));
                        binding.location.setText(documentSnapshot.getString("address"));
                        binding.bloodGroup.setText(documentSnapshot.getString("bloodGrp"));
                        binding.email.setText(documentSnapshot.getString("email"));

                        
                        
                        binding.name1T.setText(documentSnapshot.getString("name"));
                        if(binding.name1T.getText().toString().length()==0)binding.name1T.setVisibility(View.GONE);

                        binding.locationT.setText(documentSnapshot.getString("address"));
                        if(binding.locationT.getText().toString().length()==0)binding.locationT.setVisibility(View.GONE);

                        binding.bloodGroupT.setText(documentSnapshot.getString("bloodGrp"));
                        if(binding.bloodGroupT.getText().toString().length()==0)binding.bloodGroupT.setVisibility(View.GONE);

                        binding.emailT.setText(documentSnapshot.getString("email"));
                        if(binding.emailT.getText().toString().length()==0)binding.emailT.setVisibility(View.GONE);


                        //Loading image
                        if(documentSnapshot.getString("imgUri")!="")
                        {
                            Glide.with(ProfileActivity.this)
                                    .load(documentSnapshot.getString("imgUri"))
                                    .placeholder(R.drawable.pp)
                                    .into(binding.profilePicture);
                        }
                    }
            }
        });

        //On editing
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edit.setVisibility(View.GONE);
                binding.editOff.setVisibility(View.VISIBLE);
                binding.layout1.setVisibility(View.VISIBLE);
                binding.layout.setVisibility(View.GONE);
            }
        });

        //On edit off
        binding.editOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edit.setVisibility(View.VISIBLE);
                binding.editOff.setVisibility(View.GONE);
                binding.layout1.setVisibility(View.GONE);
                binding.layout.setVisibility(View.VISIBLE);
            }
        });

        //Editing profile image
        binding.profilePicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openFileChooser();
                return false;
            }
        });

        //On updating
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.editOff.setVisibility(View.GONE);
                binding.edit.setVisibility(View.VISIBLE);

                db.collection("Users").document(currentUser.getPhoneNumber())
                        .update("name", binding.name1.getText().toString());
                db.collection("Users").document(currentUser.getPhoneNumber())
                        .update("email",binding.email.getText().toString());
                db.collection("Users").document(currentUser.getPhoneNumber())
                        .update("bloodGrp", binding.bloodGroup.getText().toString());
                db.collection("Users").document(currentUser.getPhoneNumber())
                        .update("address", binding.location.getText().toString());
                db.collection("Users").document(currentUser.getPhoneNumber())
                        .update("phone", binding.contactNumber.getText().toString());
                Toast.makeText(ProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();

                binding.layout1.setVisibility(View.GONE);
                binding.layout.setVisibility(View.VISIBLE);

                //Saving image uri in storage
                if(imageUri!=null)
                {
                    StorageReference reference =storage.getReference()
                            .child("Profiles").child(auth.getCurrentUser().getPhoneNumber());

                    //Saving image uri in storage
                    reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        db.collection("Users").document(currentUser.getPhoneNumber())
                                                .update("imgUri",uri.toString());
                                    }
                                });
                            }

                        }
                    });
                }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK
                && data != null && data.getData() != null)
        {
            //getting back the uri of image
            imageUri=data.getData();
            binding.profilePicture.setImageURI(imageUri);
            //Saving image uri in storage
            if(imageUri!=null)
            {
                StorageReference reference =storage.getReference()
                        .child("Profiles").child(auth.getCurrentUser().getPhoneNumber());

                //Saving image uri in storage
                reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    db.collection("Users").document(currentUser.getPhoneNumber())
                                            .update("imgUri",uri.toString());
                                }
                            });
                        }

                    }
                });
            }
        }
    }
}