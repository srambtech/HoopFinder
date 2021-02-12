/**
 * The Court class holds information to create a structure for basketball courts.
 * It also contains a method for adding a court to the Firebase database
 *
 * @author Jamie Smart
 * @version 1.0
 */

package com.example.hoopfinder;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Database;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

/**
 * Controls the structure for courts and allows for courts to be added to and removed from database
 */
public class Court {

    private String name;
    private double longitude;
    private double latitude;
    private String usersAtCourt;


    private static final String TAG = "com.example.hoopfinder";

    public static ArrayList<Court> listOfCourts = new ArrayList<Court>();


    /**
     * Default contructor. Needed for Firebase data reads.
     */
    public Court() {

    }

    /**
     * Constructor to create a court
     *
     * @param name      This is the name of the court
     * @param latitude  This is the latitude of the court
     * @param longitude This is the longitude of the court
     */
    public Court(int id, String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.usersAtCourt = "";
    }


    /**
     * Adds a court to the database. Illegal characters will be automatically removed from the name
     *
     * @param enteredName      The name of the court to be added
     * @param latitude  The court's latitude
     * @param longitude The court's longitude
     */
    public static void addCourt(String enteredName, final double latitude, final double longitude) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Courts");

        // remove characters that are incompatable with database
        final String name = enteredName.replaceAll(Pattern.quote("."), "")
                .replaceAll(Pattern.quote("#"), "")
                .replaceAll(Pattern.quote("$"), "")
                .replaceAll(Pattern.quote("["), "")
                .replaceAll(Pattern.quote("]"), "");



        // check to make sure court is not already in db
        DatabaseReference dbCourts = FirebaseDatabase.getInstance().getReference().child("Courts");  // GET COURTS FROM FIREBASE DB
        ValueEventListener courtListener = new ValueEventListener() {
            // DATABASE CAN ONLY BE READ THROUGH LISTENERS
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // WILL RUN WHEN METHOD IS FIRST RUN AND THEN AGAIN WHENEVER COURTS "TABLE" CHANGES
                boolean addToCourtSuccessful = true;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Court court = child.getValue(Court.class);
                    Log.i("COURT NAME", court.getName());
                    Log.i("NEW COURT NAME",name);
                    if (court.getName().toLowerCase().equals(name.toLowerCase())){
                        //court is already in db
                        //Todo fix - not updating - Court location activity
                        addToCourtSuccessful = false;
                        Log.i("addToCourtSuccessful1", Boolean.toString(addToCourtSuccessful));
                    }
                }

                //Log.i("addToCourtSuccessful2", Boolean.toString(addToCourtSuccessful));

                if (addToCourtSuccessful) {
                    db.child(name).child("name").setValue(name);
                    db.child(name).child("latitude").setValue(latitude);
                    db.child(name).child("longitude").setValue(longitude);
                    db.child(name).child("usersAtCourt").setValue("");
                    Toast.makeText(AddCourtActivity.getAppContext(), "Court added", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(AddCourtActivity.getAppContext(), "Court already exists", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting a court failed
                Log.w("Court.addCourt()", "loadCourt:onCancelled", databaseError.toException());
            }
        };

        dbCourts.addListenerForSingleValueEvent(courtListener);
    }


    /**
     * Deletes a court from the database
     * @param name The name of the court to remove from the database
     */
    public static void deleteCourt(String name){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Courts");
        db.child(name).removeValue();
    }

    /**
     * Adds a set of pre-defined courts to the database
     * @return nothing
     */
    public static void batchAddCourts(Context context){
        addCourt("North Lawton Playground", 42.349743, -71.127213);
        addCourt("Titus Sparrow Park", 42.343640, -71.080235);
        addCourt("Peters Park", 42.343150, -71.067338);
        addCourt("Ringer Park", 42.350954, -71.138656);
        addCourt("Andrew P. Puopolo Junior Athletic Field", 42.368846, -71.054850);
        addCourt("Back Bay Fens", 42.341353, -71.096463);
        addCourt("Coolidge Park", 42.346037, -71.131806);
        addCourt("Joyce Playground", 42.345298, -71.15164);
        addCourt("McLaughlin Playground", 42.328629, -71.103482);
        addCourt("Christopher Lee Playground", 42.337969, -71.031285);
        addCourt("Joe Moakley Park", 42.328269, -71.050589);
        addCourt("Veterans Park", 42.328299, -71.056011);
        addCourt("Clifford Playground", 42.326576, -71.067668);
        addCourt("Orton Field", 42.338357, -71.053460);
        addCourt("Jackson Square Playground", 42.324394, -71.099094);
        addCourt("Marcella Playground", 42.323432, -71.096175);
        addCourt("Jefferson Playground", 42.326548, -71.108667);
        addCourt("Longwood Playground", 42.340168, -71.115721);
        addCourt("Classroom", 42.348775, -71.103703);
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getName() {
        return this.name;
    }

    public String getUsersAtCourt() { return this.usersAtCourt; }
}
