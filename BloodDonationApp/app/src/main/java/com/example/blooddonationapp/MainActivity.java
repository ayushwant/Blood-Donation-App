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
import com.example.blooddonationapp.Activities.ProfileActivity;
import com.example.blooddonationapp.Fragments.FeedFragment;
import com.example.blooddonationapp.Fragments.MapFragment;
import com.example.blooddonationapp.Fragments.RequestFragment;
import com.example.blooddonationapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private View hView;
    private TextView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment)
                .commit();
    }
}