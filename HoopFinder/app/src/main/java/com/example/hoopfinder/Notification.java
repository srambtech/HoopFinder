package com.example.hoopfinder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/**
 * Controls the structure for notifications and allows them to be sent statically
 */
public class Notification extends AppCompatActivity {
    // many constants in this class need to be changed
    static String CHANNEL_ID="1"; // perhaps use different channels for different types of notifications
    String title;
    String content;

    public Notification(String title, String content){
        this.title = title;
        this.content = content;
    }

    /**
     * Create a channel for notifications
     */
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "General"; //getString(R.string.channel_name);
            String description = "Channel description"; // getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            // the below line needs to refer to a class that always
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Sends notifications through the channel
     */
    public void sendNotification(Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.hoopfinder_logo)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, builder.build());  // use unique ID for first arg
    }


}