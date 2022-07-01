package com.dam.bookcita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.bookcita.databinding.ActivityDashboardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardAdminActivity extends AppCompatActivity {
    //view binding
    private ActivityDashboardAdminBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        // handle click logout
        binding.logoutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });
    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //not logged in, gate main screen
            startActivity(new Intent(this, MainScreen.class));
            finish();
        } else {
            //logged in, get user info
            String email = firebaseUser.getEmail();
            //set in TextView of toolbar
            binding.subTitleTv.setText(email);

        }
    }
}