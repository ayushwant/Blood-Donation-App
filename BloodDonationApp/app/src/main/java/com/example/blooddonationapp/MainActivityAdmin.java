package com.example.blooddonationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class MainActivityAdmin extends AppCompatActivity {
    //initialize variable
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        //assign variable
        bottomNavigation = findViewById(R.id.bottom_navigation);

        //add menu item
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_feed));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_requests));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_history));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                //initialize fragment
                Fragment fragment = null;
                //check condition
                switch(item.getId()) {
                    case 1: //when id is 1
                        //initialize feed fragment
                        fragment = new FeedFragment();
                        break;

                    case 2: //when id is 2
                        //initialize request fragment
                        fragment = new RequestFragment();
                        break;

                    case 3: //when id is 3
                        //initialize history fragment
                        fragment = new HistoryFragment();
                        break;
                }
                //load fragment
                loadFragment(fragment);

            }
        });

            //set feed count
        bottomNavigation.setCount(1, "10");
        //set request fragment initially selected
        bottomNavigation.show(2, true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                //display toast
                Toast.makeText(getApplicationContext(), "You clicked" + item.getId(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                //display toast
                Toast.makeText(getApplicationContext(), "You Reselected" + item.getId(),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadFragment(Fragment fragment) {
        //replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,fragment);
                commit();



    }

    private void commit() {
    }
}