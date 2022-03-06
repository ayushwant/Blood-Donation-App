package com.example.blooddonationapp.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDonorRegistrationDetailBinding;
import com.example.blooddonationapp.databinding.ActivityProfileAdminBinding;

public class ProfileActivityAdmin extends AppCompatActivity
{
    ActivityProfileAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}