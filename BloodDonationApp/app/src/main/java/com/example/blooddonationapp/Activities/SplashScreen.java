package com.example.blooddonationapp.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.messaging.FirebaseMessaging;
import android.util.Log;
import android.widget.Toast;

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
    public static String token;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Getting app's token for cloud messaging
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM TOKEN Failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                       // Toast.makeText(SplashScreen.this, token, Toast.LENGTH_SHORT).show();

                    }
                });



        //Subscribing to topic-- CODE
        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            Log.w("FCM TOKEN Failed", task.getException());
                            return;
                        }
                        Toast.makeText(SplashScreen.this,"Subscribed", Toast.LENGTH_SHORT).show();
                    }
                });

//       //Sending message to topic
//        // The topic name can be optionally prefixed with "/topics/".
//        String topic = "highScores";
//
//
//        // See documentation on defining a message payload.
//        Message message = Message.builder()
//                .putData("score", "850")
//                .putData("time", "2:45")
//                .setTopic(topic)
//                .build();
//
//        // Send a message to the devices subscribed to the provided topic.
//        String response = null;
//        try {
//            response = FirebaseMessaging.getInstance().send(message);
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//        }
//        // Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentUser!=null)
                {
                    sendUserToHome();
                }
                else
                {
                    sendUserToLogin();
                }
            }
        }, 3000);
    }

    @Override
    public void onStart()
    {
        super.onStart();
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
    private void sendUserToLogin()
    {
        Intent i=new Intent(SplashScreen.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
        startActivity(i);
        finish();
    }
}