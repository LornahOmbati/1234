package com.example.realmapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.realmapp.MainActivity;
import com.example.realmapp.databinding.ActivitySplash2Binding;

import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.utils.PermissionManagement;

public class SplashActivity extends AppCompatActivity {
    boolean initiallyHadPermissions = false;
    ActivitySplash2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplash2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initiallyHadPermissions = PermissionManagement.checkAllPermissions(this);
        binding.permissionBtn.setOnClickListener((v)-> PermissionManagement.checkAndRequestAllPermissions(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(()->{
            Log.e("on resume","before if ");
           if (PermissionManagement.checkAllPermissions(this)){
               if (!initiallyHadPermissions){
                   Realm.databaseManager = new DatabaseManager(Realm.context);
                   Log.e("on resume","after if ");
               }
               startActivity(new Intent(SplashActivity.this, MainActivity.class));
           }
        },3000);
    }
}