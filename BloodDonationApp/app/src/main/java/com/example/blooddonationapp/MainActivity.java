package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.blooddonationapp.Activities.Chats;
import com.example.blooddonationapp.Activities.LoginActivity;
import com.example.blooddonationapp.Activities.ProfileActivity;
import com.example.blooddonationapp.Fragments.FeedFragment;
import com.example.blooddonationapp.Fragments.MapFragment;
import com.example.blooddonationapp.Fragments.RequestFragment;
import com.example.blooddonationapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivityMainBinding binding;
    private View hView;
    private TextView edit;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
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

    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment)
                .commit();
    }


    // For side navigation menu items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.log_out:
                Toast.makeText(MainActivity.this,"999",Toast.LENGTH_LONG).show();
                auth.signOut();
                sendToLogin();
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