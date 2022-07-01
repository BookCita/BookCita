package com.dam.bookcita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dam.bookcita.databinding.ActivityMainBinding;

public class MainScreen extends AppCompatActivity {
    //view binding
    private ActivityMainBinding binding;

    private Button loginBtn;
    private Button skipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //  handle loginBtn click, start login screen
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this,LoginActivity.class));
            }
        });
        //handle skipBtn click, start continue without login screen
        skipBtn = findViewById(R.id.skipBtn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this,
                        DashboardUserActivity.class));
            }
        });
    }
}