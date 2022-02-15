package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;
    private String mAuthCredentials;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    String name,phone,email,uid,activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get Firebase instances
        db= FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        // Get entered data from SingUp
        Intent i= getIntent();
        mAuthCredentials=i.getStringExtra("Authcredentials");
        name=i.getStringExtra("name");
        phone=i.getStringExtra("phone");
        email=i.getStringExtra("email");
        activity=i.getStringExtra("activity");
//        uid=i.getStringExtra("uid");
        phone="+91"+phone;

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mCode=binding.code.getText().toString();
                if(mCode.isEmpty())
                {
                    Toast.makeText(OTPActivity.this,"Enter OTP",Toast.LENGTH_LONG).show();
                }
                else if(mCode.length() != 6)
                {
                    Toast.makeText(OTPActivity.this,"Enter valid 6-digit OTP",Toast.LENGTH_LONG).show();
                }
                else
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthCredentials, mCode);
                    signInWithPhoneAuthCredential(credential);
                    if(activity.equals("SignUp"))
                    {
                        User user = new User();
                         user.setName(name);
                         user.setPhone(phone);
                         user.setEmail(email);
                        //Saving in firestore in +91... format
                         db.collection("Users").document(phone).set(user)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void unused) {

                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(getApplicationContext(),"Error in saving data",Toast.LENGTH_SHORT).show();
                             }
                         });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                        sendUserToHome();
                    }
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                        }
                    }
                });
    }

    public void sendUserToHome()
    {
        Intent i=new Intent(OTPActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
        startActivity(i);
        finish();
    }
}