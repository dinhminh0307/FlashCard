package com.example.flashcard.threads;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.flashcard.R;
import com.example.flashcard.models.Events;
import com.example.flashcard.models.ScheduleDate;
import com.example.flashcard.services.EventServices;
import com.example.flashcard.utils.DateUtils;

import java.util.List;

public class ThreadTasks {
    private ScheduleDate currentDate;
    private DateUtils dateUtils;
    private EventServices eventServices;
    private Context context;

    List<Events> events;

    private static final String CHANNEL_ID = "EVENT_CHANNEL_ID";
    private static final String CHANNEL_NAME = "Event Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for upcoming events";

    /**
     * Constructor for ThreadTasks.
     *
     * @param context The Context from which ThreadTasks is instantiated.
     */
    public ThreadTasks(Context context) {
        // Use application context to prevent memory leaks
        this.context = context.getApplicationContext();
        this.currentDate = new ScheduleDate();
        this.dateUtils = new DateUtils();
        this.eventServices = new EventServices(this.context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
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

    private void sendNotification(String title, String message) {
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

    /**
     * Checks the time before an event and logs if the event is within 30 minutes.
     *
     * @param events List of events to check.
     */
    private void checkTimeBeforeEvent(List<Events> events) {
        int currentTotalMins = currentDate.getHour() * 60 + currentDate.getMins();
        boolean isNoti = false;

        for (Events event : events) {
            for (String e_time : event.getTime()) {
                int eventHour = dateUtils.getHourFromFormatedString(e_time);
                int eventMin = dateUtils.getMinsFromFormatedString(e_time);
                int eventTotalMins = eventHour * 60 + eventMin;

                int difference = 60 - currentDate.getMins();

                if (eventHour - 1 == currentDate.getHour() && Math.abs(difference) <= 30) {
                    isNoti = true;
                    postNotificationToMainThread(event, Math.abs(difference));
                    break;
                }
            }
            if (isNoti) {
                break;
            }
        }
    }

    private void postNotificationToMainThread(Events event, int minutesBefore) {
        // Create a Handler for the main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            // Build and display the notification
            sendNotification(
                    "Upcoming Event",
                    "You have an event in about " + minutesBefore + " minutes: " + event.getName()
            );
        });
    }

    /**
     * Starts a new thread to perform background tasks.
     */
    public void runThread() {
        new Thread(() -> {
            while (true) {
                try {
                    getCurrentDate();

                    String date = currentDate.getDayOfWeeks(); // Replace with dynamic date as needed
                    date = dateUtils.mapDay(date);
                    events = eventServices.getEventsByDateService(date);

                    if (events.isEmpty()) {
                        Log.d("ThreadTasks", "No events for Current Date and Time: " +
                                currentDate.getDayOfWeeks() + " " + currentDate.getTime());
                    } else {
                        checkTimeBeforeEvent(events);
                    }

                    // Sleep for 5 seconds (adjust as needed)
                    //Thread.sleep(1200000);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.e("ThreadTasks", "Thread was interrupted", e);
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    break; // Exit the loop if interrupted
                } catch (Exception e) {
                    Log.e("ThreadTasks", "Error during background task", e);
                }
            }
        }).start();
    }

    /**
     * Retrieves the current date and time.
     *
     * @throws InterruptedException If the thread is interrupted during sleep.
     */
    private void getCurrentDate() throws InterruptedException {
        currentDate = dateUtils.getCurrentDateAndTime();
        Log.d("ThreadTasks", "Current Date and Time: " + currentDate.getDayOfWeeks() + " " + currentDate.getTime());
    }
}
