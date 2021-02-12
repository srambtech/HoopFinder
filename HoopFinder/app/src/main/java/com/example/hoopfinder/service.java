package com.example.hoopfinder;


import android.app.IntentService;
import android.content.Intent;



public class service extends IntentService{

    /**
     * A constructor is required, and must call the super <code><a href="/reference/android/app/IntentService.html#IntentService(java.lang.String)">IntentService(String)</a></code>
     * constructor with a name for the worker thread.
     */
    public service() {

        super("ProximityCheckerService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.



        ProximityChecker proximityChecker = new ProximityChecker(this);
        proximityChecker.checkProximity(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.createNotificationChannel(this);
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        super.onStartCommand(intent,flags,startId);


        return START_STICKY;
    }







}










