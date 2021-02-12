package com.example.hoopfinder;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.content.Context;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class AddCourtActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = AddCourtActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location BU library and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(42.349410, -71.104330);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static Context context;   // needed until app structure is finalized and can be placed in appropriate location
    private Marker mNewCourt;  // used to hold new Court location - JS 11/27
    private Marker mCurrentPos;  // used to hold current position - JS 12/03


    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // LatLng of the marker
    private LatLng mMarkerLatLng;
    // Alert Dialog text to prompt user to enter the court name
    private String mPromptUserCourtName;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Button courtsTab, subscriberTab, myAccount, mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();// save context to use elsewhere - needed until app structure is finalized and final location can be determined

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_add_court);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_court_location_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit_court_location) {
            submitCourtLocation();
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //mNewCourt.clear();
                if (!(mNewCourt == null)){
                    Log.i("MARKER EXISTS", "MARKER EXISTS");
                    mNewCourt.remove();
                }
                mNewCourt = mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMarkerLatLng = point;
            }
        });

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        getAllCourts();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            LatLng mTempMapMarker = new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            // mMap.clear();  - removed 12/3

                            if (!(mCurrentPos == null)){
                                Log.i("MARKER EXISTS", "MARKER EXISTS");
                                mCurrentPos.remove();
                            }
                            //mCurrentPos = mMap.addMarker(new MarkerOptions().position(mTempMapMarker));
                            mMarkerLatLng = mTempMapMarker;
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void getAllCourts() {

        DatabaseReference dbCourts = FirebaseDatabase.getInstance().getReference().child("Courts");  // GET COURTS FROM FIREBASE DB
        ValueEventListener courtListener = new ValueEventListener() {
            // DATABASE CAN ONLY BE READ THROUGH LISTENERS
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // WILL RUN WHEN METHOD IS FIRST RUN AND THEN AGAIN WHENEVER COURTS "TABLE" CHANGES
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Court court = child.getValue(Court.class);
                    Log.d("AddCourtActivity name", court.getName() + Math.random());
                    LatLng mTempMapMarker = new LatLng(court.getLatitude(), court.getLongitude());

                    // if court is subscribed to by user, make marker green
                    Log.d("AddCourtActivity", "In getAllCourts()" + Math.random());
                    //mMap.addMarker(new MarkerOptions().position(mTempMapMarker).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    mMap.addMarker(new MarkerOptions().position(mTempMapMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_marker)));

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
     * Prompts the user to enter a court name and submit
     */
    private void submitCourtLocation() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // We assume the marker is always set as long as the location permission are granted
            // since the marker will default the user location and update on click

            Log.d(TAG, "in submitCourtLocation()");

            // Construct a dialog box to prompt user for input Court Name
            // Add Court to database along with the marker LatLng
            final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this);
            inputAlert.setTitle("Please Enter Court Name");
            final EditText userInput = new EditText(this);
            inputAlert.setView(userInput);
            inputAlert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String userInputValue = userInput.getText().toString();
                    // ToDo check if add court works, update Toast accordingly
                    Court.addCourt(userInputValue, mMarkerLatLng.latitude, mMarkerLatLng.longitude);
                    Log.d(TAG, userInputValue);
                    // handle below in Court class
                    //Toast toast = Toast.makeText(getApplicationContext(), "You've succesfully added a court", Toast.LENGTH_LONG);
                    //toast.show();
                }
            });
            inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = inputAlert.create();
            alertDialog.show();
            // promptName();
                
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static Context getAppContext(){ return AddCourtActivity.context; }  // needed until location of context is finalized
}
