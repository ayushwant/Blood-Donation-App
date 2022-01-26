package com.example.blooddonationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.blooddonationapp.Fragments.FeedFragment;
import com.example.blooddonationapp.Fragments.MapFragment;
import com.example.blooddonationapp.Fragments.RequestFragment;
import com.example.blooddonationapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private MeowBottomNavigation bottomNavigation;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Menu items
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_dynamic_feed_24));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_local_hospital_24));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_map_24));
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

            private void loadFragment(Fragment fragment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment)
                        .commit();
            }
        });
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                Toast.makeText(MainActivity.this, item.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
    }
}