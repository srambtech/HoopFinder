package com.example.hoopfinder;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Controls the structure for app users and allows subscribing users to courts
 */
public class User {
    String user_id;
    String user_email;
    String user_phone_number;
    String user_courtsSubscribedTo;
    String user_usersSubscribedTo;
    public static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private String TAG = "USER CLASS";

    /**
     * Default constructor for use by Firebase
     */
    public User(){

    }

    /**
     * Constructor for a user
     * @param user_id ID of user automatically generated by registration process
     * @param user_email Email address of user
     * @param user_courtsSubscribedTo List of courts user is subscribed to
     */
    public User(String user_id,
                String user_email,
                String user_phone_number,
                String user_courtsSubscribedTo,
                String user_usersSubscribedTo){
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_phone_number = user_phone_number;
        this.user_courtsSubscribedTo = user_courtsSubscribedTo;
        this.user_usersSubscribedTo = user_usersSubscribedTo;
    }

    /**
     * Subscribes the current user to a court
     * @param courtName The name of the court to subscribe the user to
     * @return boolean true if successful false if unsuccessful
     *
     */
    public boolean subscribeToCourt(String courtName){
        // depending on the implementation of this method, the validity of the court name and format may need to be checked
        user_courtsSubscribedTo += "," + courtName;
        Log.d(TAG, this.user_id);
        db.child("Users").child(this.user_id).child("user_courtsSubscribedTo").setValue(user_courtsSubscribedTo);
        return true;
    }

    public boolean subscribeToUser(String userId) {

        user_usersSubscribedTo += "," + userId;
        db.child("Users").child(user_id).child("user_usersSubscribedTo").setValue(user_usersSubscribedTo);
        return true;
    }

    public static void genericSubscribeToCourt(String newCourtNames, String uid) {
        db.child("Users").child(uid).child("user_courtsSubscribedTo").setValue(newCourtNames);
    }

    public static void genericSubscribeToUser(String newUserNames, String uid) {
        db.child("Users").child(uid).child("user_courtsSubscribedTo").setValue(newUserNames);
    }


    public String getUser_id() {
        return this.user_id;
    }

    public String getUser_email() {
        return this.user_email;
    }

    public String getUser_phone_number() {
        return this.user_phone_number;
    }

    public String getUser_courtsSubscribedTo(){
        return this.user_courtsSubscribedTo;
    }
  
    public String getUser_usersSubscribedTo() { return this.user_usersSubscribedTo; }


    @Override
    public String toString() { return this.user_id; }
}