package com.example.hoopfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class StartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);


        Timer t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent launchActivity1 = new Intent(StartingActivity.this, firebaseAuth.class);
                startActivity(launchActivity1);
            }
        },5000);

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Intent launchActivity1 = new Intent(StartingActivity.this, PermissionActivity.class);
//        startActivity(launchActivity1);

    }
}
