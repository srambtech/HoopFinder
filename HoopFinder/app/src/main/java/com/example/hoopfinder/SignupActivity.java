package com.example.hoopfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {
    // Database reference
    DatabaseReference databaseUsers;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private final String TAG = "com.example.hoopfinder";
    private static Context context;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button confirm = findViewById(R.id.confirm);
        final EditText emailAdd = findViewById(R.id.emailaddress);
        final EditText password = findViewById(R.id.password);
        final EditText password2 = findViewById(R.id.password2);
        Button cancel = findViewById(R.id.cancel);


        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.INTERNET);

        SignupActivity.context = getApplicationContext();// save context to use elsewhere

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        mAuth = FirebaseAuth.getInstance();

        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psswrd1, psswrd2;

                psswrd1 = password.getText().toString();
                psswrd2 = password2.getText().toString();

                if (!(psswrd1.equals(psswrd2))) {
                    Log.d(psswrd1, "onClick:password 2: " + psswrd2);
                    Toast.makeText(getApplicationContext(),
                            "Passwords dont match", Toast.LENGTH_SHORT).show();
                } else {

                    registerNewUser();

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailAdd.setText("");
                password.setText("");
                password2.setText("");


            }
        });
    }


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    public String getUserPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            @Nullable String mPhoneNumber = tMgr.getLine1Number(); // todo check permissions for READ PHONE STATE

            if (mPhoneNumber != null) {
                return mPhoneNumber;
            } else {
                return "no phone number, do something instead";
            }
        } else {
            Toast.makeText(this, "You need to enable permissions to get phone number!", Toast.LENGTH_SHORT).show();
            return "need permissions";
        }
    }


    private void registerNewUser() {
        //progressBar.setVisibility(View.VISIBLE);

        EditText emailTV = findViewById(R.id.emailaddress);
        EditText passwordTV = findViewById(R.id.password);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        Log.d("Email ", email);
        Log.d("Password ", password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d()
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
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

                                String mPhoneNumber = getUserPhoneNumber();
                                String uid = user.getUid();
                                String userName = user.getDisplayName();
                                String userEmail = user.getEmail();



                                //Log.d("Phonenumber ", mPhoneNumber);
                                User test_user = new User(uid, userEmail, mPhoneNumber,"", "");

                                databaseUsers.child(uid).setValue(test_user);
                            }

                            Intent intent = new Intent(SignupActivity.this, CourtLocationActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
                            Log.d("Login Error: ",task.getException().getMessage());
                        }
                    }
                });
    }

    /*private void initializeUI() {
        emailTV = findViewById(R.id.email);
        passwordTV = findViewById(R.id.password);
        regBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
    }*/




}
