package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.MainActivityAdmin;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private FirebaseUser currentUser;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);
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
                        Intent i = new Intent(SplashScreen.this, MainActivityAdmin.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
                        startActivity(i);
                        finish();
                    }
                }
                else
                {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
                    startActivity(i);
                    finish();
                }
            }
        });



    }
}