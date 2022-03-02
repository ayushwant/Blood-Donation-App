package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDetailedNotificationBinding;

public class DetailedNotification extends AppCompatActivity {

    ActivityDetailedNotificationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}