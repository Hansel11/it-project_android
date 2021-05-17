 package com.example.kepo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

 public class MainActivity extends AppCompatActivity {

     private SharedPref pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        pref = new SharedPref(this);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent;
                if(pref.getId().equals("")){
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
                else{
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}