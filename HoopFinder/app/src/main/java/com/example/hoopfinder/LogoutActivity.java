package com.example.hoopfinder;

import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogoutActivity extends AppCompatActivity {

    //private LoginViewModel loginViewModel;

    Button logout;
    ProgressBar loadingBar;
    Button courtsTab, subscriberTab, myAccount, mapButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        //loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory().get(LoginViewModel.class);

        // create variables for the data entered.
        courtsTab = (Button)findViewById(R.id.courtsTab);
        subscriberTab =(Button)findViewById(R.id.subscriberTab);
        myAccount =(Button)findViewById(R.id.accountTab);
        mapButton =(Button)findViewById(R.id.CourtMap);

        courtsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(LogoutActivity.this, SubscribeToCourtActivity.class);
                startActivity(launchActivity1);
            }
        });

        subscriberTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(LogoutActivity.this, SubscriberListActivity.class);
                startActivity(launchActivity1);
            }
        });
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(LogoutActivity.this, LogoutActivity.class);
                startActivity(launchActivity1);
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(LogoutActivity.this, AddCourtActivity.class);
                startActivity(launchActivity1);
            }
        });

        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();


            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            String userName = user.getDisplayName();
            String userEmail = user.getEmail();
            //Log.d("Username ", userName);


            TextView userId = (TextView) findViewById(R.id.userId);
            userId.setText(userEmail);

        }
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LogoutActivity.this,"Signed out",Toast.LENGTH_LONG).show();
                        // user is now signed out
                        startActivity(new Intent(LogoutActivity.this, firebaseAuth.class));
                        finish();
                    }
                });
        // [END auth_fui_signout]
    }
}
