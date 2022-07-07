package com.dam.bookcita.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.bookcita.databinding.ActivityFirstBinding;


public class FirstActivity extends AppCompatActivity {

    //view binding
    private ActivityFirstBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //handle loginBtn click, start login screen
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, LoginActivity.class));
            }
        });

        //handle skipBtn click, start continue without login screen
        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   startActivity(new Intent(FirstActivity.this, DashboardUserActivity.class));
            }
        });
    }
}