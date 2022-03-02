package com.example.blooddonationapp.Activities;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.MainActivityAdmin;
import com.example.blooddonationapp.R;

import com.example.blooddonationapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity
{
    ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    private boolean isAdmin=false;
    private FirebaseUser currentUser;
    private String phone;
    FirebaseFirestore db;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String st=binding.signUpTxt.getText().toString();
        SpannableString ss= new SpannableString(st);

        ClickableSpan clickableSpan=new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLACK);

            }
            @Override
            public void onClick(@NonNull View view) {
                Intent i= new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
            }

        };
        ss.setSpan(clickableSpan,10,st.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.signUpTxt.setText(ss);
        binding.signUpTxt.setMovementMethod(LinkMovementMethod.getInstance());

        binding.phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.errorMsg.setText("");
            }
        });

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.errorMsg.setText("");
                if(binding.phoneNumber.getText().toString().trim().length()!=10)
                {
                    binding.errorMsg.setText("Enter valid number");
                }
                else
                {
                    phone=binding.phoneNumber.getText().toString().trim();
                    db.collection("Admin").document("+91"+phone)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists())
                            {
                                isAdmin=true;
                            }
                        }
                    });

                    // Checking if already registered
                    db.collection("Users").document("+91"+phone)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists())
                            {
                                binding.progressBar.setVisibility(View.VISIBLE);
                                binding.signIn.setVisibility(View.INVISIBLE);
                                phone="+91"+phone;
                                PhoneAuthOptions options =
                                        PhoneAuthOptions.newBuilder(mAuth)
                                                .setPhoneNumber(phone) // Phone number to verify
                                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(LoginActivity.this) // Activity (for callback binding)
                                                .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);

                            }
                            else
                            {
                                binding.errorMsg.setText("Number not registered, kindly sign-up first");
                            }
                        }
                    });

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
                binding.signIn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {

                                Intent otpIntent=new Intent(LoginActivity.this, OTPActivity.class);
                                otpIntent.putExtra("Authcredentials",s);
                                if(isAdmin)
                                otpIntent.putExtra("activity","Admin");
                                else
                                otpIntent.putExtra("activity","Login");
                                otpIntent.putExtra("phone",phone);
                                startActivity(otpIntent);
                                finish();
                            }
                        },1000);

            }
        };

        //Google sign in
        binding.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //FB sign in
        binding.fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //Yahoo sign in
        binding.yahoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(currentUser!=null)
        {
            sendUserToHome();
        }
    }

    //For sending user to main activity
    public void sendUserToHome()
    {

        db.collection("Admin").document(currentUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                    String signedin=task.getResult().getString("Signed_in");
                    if(signedin.equals("true"))
                    {
                        Intent i = new Intent(LoginActivity.this, MainActivityAdmin.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
                        startActivity(i);
                    }
                    else
                    {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
                        startActivity(i);
                    }
                }
                else
                {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
                    startActivity(i);
                }
            }
        });



    }

}