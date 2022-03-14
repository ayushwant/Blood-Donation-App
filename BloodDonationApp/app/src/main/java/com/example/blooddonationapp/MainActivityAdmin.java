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
import com.example.blooddonationapp.Activities.Chats;
import com.example.blooddonationapp.Activities.LoginActivity;
import com.example.blooddonationapp.Activities.MyRequest;
import com.example.blooddonationapp.Activities.NotificationActivity;
import com.example.blooddonationapp.Activities.ProfileActivity;
import com.example.blooddonationapp.ActivitiesAdmin.ProfileActivityAdmin;
import com.example.blooddonationapp.AdminSideFragments.FeedFragmentAdmin;
import com.example.blooddonationapp.AdminSideFragments.HistoryFragmentAdmin;
import com.example.blooddonationapp.AdminSideFragments.RequestFragmentAdmin;
import com.example.blooddonationapp.Fragments.FeedFragment;
import com.example.blooddonationapp.databinding.ActivityMainAdminBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainAdminBinding binding;
    private NavigationView navigationView;
    private View hView;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private TextView position,name,switchUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting current user from firebase authentication
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        db= FirebaseFirestore.getInstance();

        //Side navigation
        navigationView=findViewById(R.id.nav_view_side_admin);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        hView=binding.navViewSideAdmin.getHeaderView(0);
        position=hView.findViewById(R.id.edit_profile);
        name=hView.findViewById(R.id.user_name);
        switchUser=hView.findViewById(R.id.switch_to_user);

        db.collection("Admin").document(currentUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String sname=task.getResult().getString("name");
                String sposition=task.getResult().getString("position");
                name.setText(sname);
                position.setText(sposition);
            }
        });

        switchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Admin").document(currentUser.getPhoneNumber())
                        .update("Signed_in", "false");
                Intent i=new Intent(MainActivityAdmin.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });


        binding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.container.openDrawer(GravityCompat.START);
            }
        });


        // initiating chat
        binding.menuChatBtn.setOnClickListener(view -> {
            startActivity( new Intent(MainActivityAdmin.this, Chats.class) );
        });

        //Notication
        binding.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //add menu item
        binding.bottomNavigationAdmin.add(new MeowBottomNavigation.Model(1,R.drawable.ic_feed));
        binding.bottomNavigationAdmin.add(new MeowBottomNavigation.Model(2, R.drawable.ic_blood));
        binding.bottomNavigationAdmin.add(new MeowBottomNavigation.Model(3,R.drawable.ic_history));

        binding.bottomNavigationAdmin.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                //initialize fragment
                Fragment fragment = null;
                //check condition
                switch(item.getId()) {
                    case 1: //when id is 1
                        //initialize feed fragment
                        loadFragment(new FeedFragmentAdmin());
                        break;

                    case 2: //when id is 2
                        //initialize request fragment
                        loadFragment(new RequestFragmentAdmin());
                        break;

                    case 3: //when id is 3
                        //initialize history fragment
                        loadFragment(new HistoryFragmentAdmin());
                        break;
                }
            }
        });

        //set feed count
        binding.bottomNavigationAdmin.setCount(1, "10");
        //set request fragment initially selected
        binding.bottomNavigationAdmin.show(2, true);

        binding.bottomNavigationAdmin.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });
        binding.bottomNavigationAdmin.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
    }
    private void loadFragment(Fragment fragment) {
        //replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_admin,fragment).commit();
    }

    // For side navigation menu items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.log_out:

                auth.signOut();
                sendToLogin();
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
        db.collection("Admin").document(currentUser.getPhoneNumber())
                .update("Signed_in", "false");
        Intent i=new Intent(MainActivityAdmin.this, LoginActivity.class);
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