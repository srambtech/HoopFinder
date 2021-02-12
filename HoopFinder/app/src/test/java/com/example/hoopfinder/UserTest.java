package com.example.hoopfinder;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testConstructor() {
        final String user_id = "test_id";
        final String user_email = "test@test.com";
        final String user_phone_number = "9999999999";
        final String user_courtsSubscribedTo = "";
        final String user_usersSubscribedTo = "";

        User testUser = new User(user_id,
                user_email,
                user_phone_number,
                user_courtsSubscribedTo,
                user_usersSubscribedTo);

        assertEquals(user_id, testUser.user_id);
        assertEquals(user_email, testUser.user_email);
        assertEquals(user_phone_number, testUser.user_phone_number);
        assertEquals(user_courtsSubscribedTo, testUser.user_courtsSubscribedTo);
        assertEquals(user_usersSubscribedTo, testUser.user_usersSubscribedTo);
    }

    @Test
    public void testSubscribeToCourt() {
        final String currentUid = "wAhy0JiIicS0B0XJSEewDbAsdT83";
        final String testCourt = "Boston Common";

        User currentUser = new User(currentUid,
                "blah",
                "blah",
                "",
                "");

        // check to make sure they are in DB
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser.subscribeToCourt(testCourt, dbUsers);
        dbUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean courtSubscribed = false;
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if (user.getCourtsSubscribedTo().contains(testCourt)){
                        courtSubscribed = true;
                    }
                }
                assertTrue(courtSubscribed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Test
    public void testSubscribeToUser() {
        final String currentUid = "wAhy0JiIicS0B0XJSEewDbAsdT83";
        final String testUser = "F7SpyoNZCtZHnU6bAGTNcjCpu";

        User currentUser = new User(currentUid,
                "blah",
                "blah",
                "",
                "");


        // check to make sure they are in DB
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser.subscribeToUser(testUser, dbUsers);
        dbUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean userSubscribed = false;
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if (user.getUserSubscribedTo().contains(testUser)){
                        userSubscribed = true;
                    }
                }
                assertTrue(userSubscribed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
