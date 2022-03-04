package com.example.blooddonationapp.AdminSideFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blooddonationapp.Adapters.VPAdapter;
import com.example.blooddonationapp.Fragments.DonorList;
import com.example.blooddonationapp.Fragments.RequestList;
import com.example.blooddonationapp.R;
import com.google.android.material.tabs.TabLayout;

public class RequestFragmentAdmin extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public RequestFragmentAdmin() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_request_admin, container, false);
        tabLayout=v.findViewById(R.id.tab_layout);
        viewPager=v.findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter=new VPAdapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new RaisedRequestsList(),"Blood Requests");
        vpAdapter.addFragment(new DonorRegistrationList(),"Donor Registration");
        viewPager.setAdapter(vpAdapter);

        return v;
    }
}