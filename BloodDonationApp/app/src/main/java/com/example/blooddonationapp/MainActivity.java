package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.blooddonationapp.Activities.Chats;
import com.example.blooddonationapp.Activities.LoginActivity;
import com.example.blooddonationapp.Activities.MyRequest;
import com.example.blooddonationapp.Activities.NotificationActivity;
import com.example.blooddonationapp.Activities.ProfileActivity;
import com.example.blooddonationapp.Fragments.FeedFragment;
import com.example.blooddonationapp.Fragments.MapFragment;
import com.example.blooddonationapp.Fragments.RequestFragment;
import com.example.blooddonationapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivityMainBinding binding;
    private View hView;
    private TextView edit;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private NavigationView navigationView;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting current user from firebase authentication
        auth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();
        navigationView=findViewById(R.id.nav_view_side);
        navigationView.setNavigationItemSelectedListener(this);

        // updating uid of user in fireStore upon successful login
        if(currentUser!=null) {
            uid = currentUser.getUid();
            Map<String, Object> data = new HashMap<>();
            data.put("uid", uid);
            db.collection("Users").document(currentUser.getPhoneNumber())
                    .update("uid", uid);
//                    .set(data, SetOptions.merge());
        }

        hView=binding.navViewSide.getHeaderView(0);
        edit=hView.findViewById(R.id.edit_profile);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });


        //Menu items
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_blood));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_map_1));

        binding.bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                // Fragment Initialization
                Fragment fragment = null;
                switch(item.getId())
                {
                    case 1: fragment = new FeedFragment();
                        break;
                    case 2: fragment = new RequestFragment();
                        break;
                    case 3: fragment = new MapFragment();
                        break;
                }
                loadFragment(fragment);
            }
        });

        binding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  binding.container.openDrawer(GravityCompat.START);
            }
        });

        binding.bottomNavigation.show(1,true);
        binding.bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });
        binding.bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });

        // initiating chat
        binding.menuChatBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, Chats.class);
            startActivity(i);
        });


        //In app notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("My Notification",
                    "My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //Notication
        binding.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               //In app notification
                String notification="In app notification";
                NotificationCompat.Builder builder=new NotificationCompat.Builder
                        (MainActivity.this,"My Notification").setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle("New Notification")
                        .setContentText(notification)
                        .setAutoCancel(true);

                Intent intent=new Intent(MainActivity.this,NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.putExtra("message",notification);

                PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity.this,0,
                        intent,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

                notificationManager.notify(0,builder.build());

               //Intent i=new Intent(MainActivity.this, NotificationActivity.class);
               // startActivity(i);
            }
        });

    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment)
                .commit();
    }


    // For side navigation menu items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.log_out: auth.signOut();
                               sendToLogin();
                               break;

            case R.id.my_request:
                               Intent i1=new Intent(MainActivity.this, MyRequest.class);
                               startActivity(i1);
                               break;
            case R.id.settings:
                               break;
            case R.id.about_us:
                               break;

        }
        return true;
    }

    private void sendToLogin()
    {
        Intent i=new Intent(MainActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
        {
            sendToLogin();
        }
    }
}