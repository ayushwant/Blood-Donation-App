package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.blooddonationapp.Fragments.RequestFragment;
import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.databinding.ActivityRegisteredMsgBinding;

import java.util.Timer;
import java.util.TimerTask;

public class RegisteredMsg extends AppCompatActivity {

    ActivityRegisteredMsgBinding binding;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisteredMsgBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(RegisteredMsg.this, RequestFragment.class);
                startActivity(i);
                finish();
            }
        },5000);
    }
}