package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.blooddonationapp.Activities.AdminLogin;
import com.example.blooddonationapp.Activities.Chats;
import com.example.blooddonationapp.Activities.LoginActivity;
import com.example.blooddonationapp.Activities.MapActivity;
import com.example.blooddonationapp.Activities.MyRequest;
import com.example.blooddonationapp.Activities.NotificationActivity;
import com.example.blooddonationapp.Activities.ProfileActivity;
import com.example.blooddonationapp.Activities.SavedPostsActivity;
import com.example.blooddonationapp.Fragments.FeedFragment;
import com.example.blooddonationapp.Fragments.MapFragment;
import com.example.blooddonationapp.Fragments.RequestFragment;
import com.example.blooddonationapp.ModelClasses.Notification;
import com.example.blooddonationapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivityMainBinding binding;
    private View hView;
    private TextView edit,userName,adminSwitch;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference database;
    private FirebaseFirestore db;
    private NavigationView navigationView;
    private String uid;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting current user from firebase authentication
        auth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();
        database= FirebaseDatabase.getInstance().getReference("Notifications").
                child(currentUser.getPhoneNumber());

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
        userName=hView.findViewById(R.id.user_name);
        adminSwitch=hView.findViewById(R.id.switch_to_admin);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        db.collection("Users").document(currentUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String name=task.getResult().getString("name");
                userName.setText(name);
            }
        });
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                   Notification notification=dataSnapshot.getValue(Notification.class);
                   if(!notification.isSeen())count++;
                }
                Toast.makeText(MainActivity.this,Integer.toString(count),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Switching to admin side if admin
        db.collection("Admin").document(currentUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                    adminSwitch.setVisibility(View.VISIBLE);
                }
            }
        });

        adminSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, AdminLogin.class);
                i.putExtra("phone",currentUser.getPhoneNumber());
                startActivity(i);
                finish();
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
                    case 1: loadFragment(new FeedFragment());
                        break;
                    case 2: loadFragment( new RequestFragment() );
                        break;
                    case 3: startActivity( new Intent(MainActivity.this, MapActivity.class) );
                        break;
                }

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

        //Notication
        binding.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(i);
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
            case R.id.saved_post:
                               Intent i2=new Intent(MainActivity.this, SavedPostsActivity.class);
                               startActivity(i2);
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
//        if(currentUser==null)
//        {
//            sendToLogin();
//        }
    }
}