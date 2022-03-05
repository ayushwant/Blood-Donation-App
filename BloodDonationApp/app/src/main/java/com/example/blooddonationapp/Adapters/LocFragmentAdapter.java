package com.example.blooddonationapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.blooddonationapp.Fragments.AutocompleteFragment;

public class LocFragmentAdapter extends FragmentPagerAdapter {
    public LocFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new AutocompleteFragment();
    }

    @Override
    public int getCount() {
        return 1;
    }

    public void addFragment(Fragment fragment,String title)
    {

    }
}
