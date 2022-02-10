package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blooddonationapp.databinding.ActivityMyRequestBinding;

public class MyRequest extends AppCompatActivity {

    ActivityMyRequestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}