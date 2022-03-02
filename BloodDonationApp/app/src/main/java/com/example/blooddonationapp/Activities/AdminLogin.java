package com.example.blooddonationapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.MainActivityAdmin;
import com.example.blooddonationapp.databinding.ActivityAdminLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminLogin extends AppCompatActivity {

    ActivityAdminLoginBinding binding;
    private String ID,password,phone;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        Intent i= getIntent();
        phone=i.getStringExtra("phone");

        binding.userSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AdminLogin.this, MainActivity.class);
                startActivity(i);
            }
        });
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(AdminLogin.this,phone,Toast.LENGTH_LONG).show();
                db.collection("Admin").document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists())
                        {
                            String adminId=task.getResult().getString("id");
                            String adminPassword=task.getResult().getString("password");

                            ID=binding.adminId.getText().toString();
                            password=binding.adminPassword.getText().toString();
                            Toast.makeText(AdminLogin.this,adminId+" "+adminPassword
                                    +" "+ID+password,Toast.LENGTH_LONG).show();

                            if(adminId.equals(ID) && adminPassword.equals(password))
                            {
                                db.collection("Admin").document(phone)
                                        .update("Signed_in", "true");
                                Intent i=new Intent(AdminLogin.this, MainActivityAdmin.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(AdminLogin.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(AdminLogin.this,"**********",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }

    /* String adminId=documentSnapshot.getString("id");
                        String adminPassword=documentSnapshot.getString("password");

                        ID=binding.adminId.getText().toString();
                        password=binding.adminPassword.getText().toString();
                        Toast.makeText(AdminLogin.this,adminId+" "+adminPassword
                                +" "+ID+password,Toast.LENGTH_LONG).show();

                        if(adminId==ID && adminPassword==password)
                        {
                            Intent i=new Intent(AdminLogin.this, MainActivityAdmin.class);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(AdminLogin.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
                        }*/
}