package com.example.hoopfinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class PermissionActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private final String TAG = "PermissionActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Log.d("PermissionActivity", "Beginning of onCreate");

        while (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(".PermissionActivity","1. We need permissions");
            // we add permissions we need to request location of the users
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            //permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            //permissions.add(Manifest.permission.SEND_SMS);


            permissionsToRequest = permissionsToRequest(permissions);
            Log.d("PermissionActivity","2. Here");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ALL_PERMISSIONS_RESULT);

                    //requestPermissions(permissionsToRequest.toArray(
                            //new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                }
            }


        }


//        // we add permissions we need to request location of the users
//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        //permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        permissions.add(Manifest.permission.SEND_SMS);
//
//
//        permissionsToRequest = permissionsToRequest(permissions);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (permissionsToRequest.size() > 0) {
//                requestPermissions(permissionsToRequest.toArray(
//                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//            }
//        }

        // we build google api client
//        googleApiClient = new GoogleApiClient.Builder(this).
//                addApi(LocationServices.API).
//                addConnectionCallbacks(this).
//                addOnConnectionFailedListener(this).build();


        Intent launchActivity1 = new Intent(PermissionActivity.this, firebaseAuth.class);
        startActivity(launchActivity1);

        Log.d("PermissionActivity", "End of onCreate");

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

//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (!checkPlayServices()) {
//            locationTv.setText("You need to install Google Play Services to use the App properly");
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        // stop location updates
//        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            googleApiClient.disconnect();
//        }
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
        Log.d("PermissionActivity", "onConnected");



        //location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
//        if (location != null) {
//            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
//        }
//
//        // on location change, we can fetch a list of subscribed locations from court table
//        // and send alert to all subscribed individual
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
                            new AlertDialog.Builder(this).
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
                }
//                else {
////                    if (googleApiClient != null) {
////                        googleApiClient.connect();
////                    }
//                }

                break;
        }
    }




}

