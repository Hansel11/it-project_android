package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.kepo.adapter.TodoAdapter;
import com.example.kepo.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private SharedPref pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        getSupportActionBar().hide();
        pref = new SharedPref(this);
        binding.txtWelcome.setText("Welcome, "+pref.getName());

        binding.btnMyTodo.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyTodoActivity.class);
            startActivity(intent);
        });

        binding.btnSearchTodo.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchTodoActivity.class);
            startActivity(intent);
        });

        binding.btnSearchUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchUserActivity.class);
            startActivity(intent);
        });

        binding.btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

}