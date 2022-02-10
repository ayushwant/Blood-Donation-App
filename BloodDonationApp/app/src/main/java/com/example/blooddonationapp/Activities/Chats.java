package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityChatsBinding;

public class Chats extends AppCompatActivity {

    ActivityChatsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chats);

        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.availableChatsBtn.setOnClickListener(view ->
                startActivity( new Intent(Chats.this, AvailableChats.class)));

    }
}