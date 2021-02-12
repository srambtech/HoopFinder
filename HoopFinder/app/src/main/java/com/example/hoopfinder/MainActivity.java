package com.example.hoopfinder;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;

import android.telephony.TelephonyManager;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.room.Database;

//import com.facebook.internal.WebDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private Location location;
    private TextView locationTv;
    private TextView proximityTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private final String TAG = "com.example.hoopfinder";
    private static Context context;
    private User testUser;  // THIS WILL EVENTUALLY NEED TO BE THE ACTUAL USER


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTv = findViewById(R.id.location);
        proximityTv = findViewById(R.id.proximity);
        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.SEND_SMS);

        MainActivity.context = getApplicationContext();// save context to use elsewhere

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


    }    // end onCreate

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

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
        else{
            locationTv.setText("No valid location");
            Location loc2 = new Location("");
            loc2.setLongitude(-71.103703);
            loc2.setLatitude(42.348775);
        }

        Location testLocation = new Location("");

        // add court at GSU
        //Court.addCourt("GSU", 42.350661, -71.108064);

        // test vars for now
        double testLongitude = -71.0964750;
        double testLatitude = 42.3815890;
        double proximityThreshold = 50.0;
        testLocation.setLongitude(testLongitude);
        testLocation.setLatitude(testLatitude);
        String testMobile = ""; // fill out if you want to test SMS
        String testMessage = "Proximity Alert!"; // SMS message text
        // should eventually be actual app user
        //testUser = new User("jsmart", "jamie@bu.edu", "password", "GSU,Walnut Street Park");



         float distanceInMeters =  testLocation.distanceTo(location);

        if (distanceInMeters < proximityThreshold) {
            proximityTv.setText("You are close : " + distanceInMeters + " meters away ");

        }
        else {
            proximityTv.setText("You are not close : " + distanceInMeters + " meters away");
        }


        startLocationUpdates();
        proximityCheck();
        newUserAtSubscribedCourtCheck();

    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        // on location change, we can fetch a list of subscribed locations from court table
        // and send alert to all subscribed individual
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MainActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

//    /**
//     * This method sends a text notification with a specified message to a specified phone number
//     * @param phoneNumber The number the text will be sent to
//     * @param message The message that will be sent
//     * @returns nothing
//     */
//    public void sendText(String phoneNumber, String message){
//        SmsManager smgr = SmsManager.getDefault();
//        smgr.sendTextMessage(phoneNumber,null,message,null,null);
//
//    }


    /**
     * Checks to see if the current user is close to a court
     */
    public void proximityCheck(){

        Log.d("MainActivity", "proximityCheck");
        // CHECK PROXIMITY TO COURTS
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        DatabaseReference dbCourts = FirebaseDatabase.getInstance().getReference().child("Courts");  // GET COURTS FROM FIREBASE DB
        ValueEventListener courtListener = new ValueEventListener() {
            // DATABASE CAN ONLY BE READ THROUGH LISTENERS
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // WILL RUN WHEN METHOD IS FIRST RUN AND THEN AGAIN WHENEVER COURTS "TABLE" CHANGES
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Court court = child.getValue(Court.class);
                    Location courtLocation = new Location("");
                    courtLocation.setLatitude(court.getLatitude());
                    courtLocation.setLongitude(court.getLongitude());
                    float distanceInMeters = courtLocation.distanceTo(location);

                    if (distanceInMeters < 50) {
                        // ADD USER TO LIST OF USERS AT COURT
                        // if user not already in list at court
                        /*if (!court.getUsersAtCourt().contains(testUser.getUser_id())) {
                            String currentUsersAtCourt = court.getUsersAtCourt();
                            ChangeUserCourtStatus addTimer = new ChangeUserCourtStatus(testUser, court, currentUsersAtCourt, "ADD", googleApiClient);
                            addTimer.run();
                        }*/
                    }

                    // check if user has left court
                    if (!(court.getUsersAtCourt() == null) && court.getUsersAtCourt().contains(testUser.getUser_id())){
                        if (distanceInMeters >= 50){
                            //user has left court
                            String currentUsersAtCourt = court.getUsersAtCourt();
                            //ChangeUserCourtStatus removeTimer = new ChangeUserCourtStatus(testUser, court, currentUsersAtCourt, "REMOVE", googleApiClient);
                            //removeTimer.run();
                        }
                    }

                    // check if user has left court
                    if (!(court.getUsersAtCourt() == null) && court.getUsersAtCourt().contains(testUser.getUser_id())){
                        if (distanceInMeters >= 50){
                            //user has left court
                            String currentUsersAtCourt = court.getUsersAtCourt();
                            //ChangeUserCourtStatus removeTimer = new ChangeUserCourtStatus(testUser, court, currentUsersAtCourt, "REMOVE", googleApiClient);
                            //removeTimer.run();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting a court failed
                Log.w(TAG, "loadCourt:onCancelled", databaseError.toException());

            }
        };

        dbCourts.addValueEventListener(courtListener);

    }

    /**
     * Checks to see if a new user is at a court the user has subscribed to
     */
    public void newUserAtSubscribedCourtCheck(){
        DatabaseReference dbCourts = FirebaseDatabase.getInstance().getReference().child("Courts");  // GET COURTS FROM FIREBASE DB

        ChildEventListener listenerForNewUsersAtCourts = new ChildEventListener() {
            // DATABASE CAN ONLY BE READ THROUGH LISTENERS
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String childName) {
                // WILL RUN WHEN METHOD IS FIRST RUN AND THEN AGAIN WHENEVER COURTS "TABLE" CHANGES
                Court court = dataSnapshot.getValue(Court.class);
                if (testUser.getUser_courtsSubscribedTo().indexOf(court.getName()) >= 0) {   // user subscribed to court that changed
                    //Notification.sendNotification("Court alert!", "A new user is at " + court.getName()+"!");
                }

            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        };

        dbCourts.addChildEventListener(listenerForNewUsersAtCourts);

    }


    /**
     * Allows access to app context from other classes
     * @returns Context of app
     */
    public static Context getAppContext(){
        return MainActivity.context;
    }

}