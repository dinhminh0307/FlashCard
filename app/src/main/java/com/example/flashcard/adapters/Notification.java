package com.example.flashcard.adapters;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.flashcard.MainActivity;
import com.example.flashcard.R;
import com.example.flashcard.models.Events;

public class Notification {
    private static final String CHANNEL_ID = "EVENT_CHANNEL_ID";
    private static final String CHANNEL_NAME = "Event Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for upcoming events";



    private Context context;

    public Notification(Context context) {
        this.context = context.getApplicationContext();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("ThreadTasks", "Notification channel created");
            } else {
                Log.e("ThreadTasks", "Failed to create notification channel: NotificationManager is null");
            }
        }
    }

    public void sendNotification(String title, String message) {
        Log.d("ThreadTasks", "Sending notification: " + title + " - " + message);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Log.e("ThreadTasks", "NotificationManager is null");
            return;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon) // Ensure this icon exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for immediate visibility
                .setAutoCancel(true); // Automatically remove the notification when tapped

        // Display the notification
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void postNotificationToMainThread(Events event, int minutesBefore) {
        // Create a Handler for the main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            // Build and display the notification
            sendNotification(
                    "Upcoming Event",
                    "You have an event in about " + minutesBefore + " minutes: " + event.getName()
            );
        });
    }
}
