package com.example.hoopfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class SubscriberListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private ArrayList<User> allUserList = new ArrayList<>();
    private final String TAG = "SubscriberList";
    public String userCourtsSubscribedTo;

    SubscriberAdapter md;

    Button courtsTab, subscriberTab, myAccount, mapButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_list);

        courtsTab = (Button)findViewById(R.id.courtsTab);
        subscriberTab =(Button)findViewById(R.id.subscriberTab);
        myAccount =(Button)findViewById(R.id.accountTab);
        mapButton =(Button)findViewById(R.id.CourtMap);

        courtsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(SubscriberListActivity.this, SubscribeToCourtActivity.class);
                startActivity(launchActivity1);
            }
        });
        subscriberTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(SubscriberListActivity.this, SubscriberListActivity.class);
                startActivity(launchActivity1);
            }
        });
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(SubscriberListActivity.this, LogoutActivity.class);
                startActivity(launchActivity1);
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity1 = new Intent(SubscriberListActivity.this, AddCourtActivity.class);
                startActivity(launchActivity1);
            }
        });


        recyclerView = (RecyclerView)findViewById(R.id.recylcer_view_user);

        //rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        updateUI();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String _uid = firebaseUser.getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"+_uid);

        final Query userQuery = userRef;
        userQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    //Log.d(TAG, dataSnapshot.toString());
                    if (dataSnapshot.getKey().equals("user_courtsSubscribedTo")) {
                        userCourtsSubscribedTo = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "HAVE I FOUND THE COURTS LIST??");
                    }

                } else {
                    Log.d(TAG, "why you no exist??");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference();

        userReference = databaseReference.child("Users");

        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                        Log.d(TAG, "DATA EXISTS " + dataSnapshot.toString());
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(TAG, user.toString());
                        allUserList.add(user);
                        updateUI();
                        // fetchData(dataSnapshot);
                    } else {
                        Log.d(TAG, "WHY NO DATA???");
                    }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG+"Changed",dataSnapshot.getValue(User.class).toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG+"Removed",dataSnapshot.getValue(User.class).toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG+"Moved",dataSnapshot.getValue(User.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG+"Cancelled",databaseError.toString());
            }
        });


        
    }

    private void updateUI(){
        mAdapter = new SubscriberAdapter(allUserList);
        recyclerView.setAdapter(mAdapter);
    }

    public void fetchData(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        allUserList.add(user);
        updateUI();
    }
}
