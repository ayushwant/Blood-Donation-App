package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityLoginBinding;
import com.example.blooddonationapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity
{
    String name,phone,email,uid;
    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        uid=currentUser.getUid();

        binding.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.errorMsg.setText("");
            }
        });
        binding.phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.errorMsg.setText("");
            }
        });
        binding.emailTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.errorMsg.setText("");
            }
        });
        //Back button
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        //On clicking on sign-up button
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //User details
                name=binding.userName.getText().toString().trim();
                phone=binding.phoneNumber.getText().toString().trim();
                email=binding.emailTxt.getText().toString().trim();
                binding.errorMsg.setText("");
                //Checking details
                if(name.equals("") || phone.equals("") || email.equals(""))
                {
                    binding.errorMsg.setText("Complete all fields");
                }
                else if(phone.length()!=10)
                {
                    binding.errorMsg.setText("Enter valid number");
                }
                //Proceeding only if details are valid
                else
                {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.signUp.setVisibility(View.INVISIBLE);

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber("+91"+phone)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(SignUpActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                binding.errorMsg.setText("Verification Failed, please try again");
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.signUp.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {

                                Intent otpIntent=new Intent(SignUpActivity.this, OTPActivity.class);
                                otpIntent.putExtra("Authcredentials",s);
                                otpIntent.putExtra("activity","SignUp");
                                otpIntent.putExtra("name",name);
                                otpIntent.putExtra("email",email);
                                otpIntent.putExtra("phone",phone);
                                otpIntent.putExtra("uid",uid);
                                startActivity(otpIntent);
                            }
                        },1000);



            }
        };

    }


}