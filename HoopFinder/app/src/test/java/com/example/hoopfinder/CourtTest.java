package com.example.hoopfinder;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CourtTest {

    @Before
    public void launchActivity(){
        ActivityScenario.launch(SignupActivity.class);

    }

    @Test
    public void addCourt() {
        final String testName = "addCourtTest";
        final double testLatitude = 1.;
        final double testLongitude = 2.;

        Court.addCourt(testName,testLatitude,testLongitude);


        DatabaseReference dbCourts = FirebaseDatabase.getInstance().getReference().child("Courts");
        dbCourts.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("woohoo", "woohoo");
                boolean courtInDB = false;
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Court court= child.getValue(Court.class);
                    if (court.getName().equals(testName) && court.getLatitude() == testLatitude && court.getLongitude() == testLongitude){
                        courtInDB = true;
                    }
                }

                Log.i("courtInDB", Boolean.toString(courtInDB));
                assertTrue(courtInDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }

    @Test
    public void deleteCourt() {
    }
}