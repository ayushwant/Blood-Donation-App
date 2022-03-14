package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.ModelClasses.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityChatPartnerInfoBinding;

public class ChatPartnerInfo extends AppCompatActivity {

    ActivityChatPartnerInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_partner_info);

        binding = ActivityChatPartnerInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User partner = getIntent().getParcelableExtra("chatPartner");

        binding.cpiName.setText(partner.getName());

        if (partner.getImgUri() != null)
            if (!partner.getImgUri().equals(""))
                Glide.with(ChatPartnerInfo.this).load(partner.getImgUri()).into(binding.cpiImage);

        if (partner.getBloodGrp() != null)
            if (!partner.getBloodGrp().equals(""))
                binding.cpiBloodValue.setText(partner.getBloodGrp());
            else
                binding.bloodConstraintLayout.setVisibility(View.GONE);
        else
            binding.bloodConstraintLayout.setVisibility(View.GONE);


        if (partner.getAddress() != null)
            if (!partner.getAddress().equals(""))
                binding.cpiAddressValue.setText(partner.getAddress());
            else
                binding.addressConstraintLayout.setVisibility(View.GONE);
        else
            binding.addressConstraintLayout.setVisibility(View.GONE);


    }
}