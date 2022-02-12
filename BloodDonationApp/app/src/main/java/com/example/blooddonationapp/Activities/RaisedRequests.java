package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blooddonationapp.databinding.ActivityRaisedRequestsBinding;

public class RaisedRequests extends AppCompatActivity
{
    ActivityRaisedRequestsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRaisedRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}