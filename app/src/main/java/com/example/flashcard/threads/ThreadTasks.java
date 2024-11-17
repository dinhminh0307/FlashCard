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
import com.example.flashcard.adapters.Notification;
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

    private Notification notification;

    List<Events> events;



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
        this.notification = new Notification(context);
        notification.createNotificationChannel();
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
                    notification.postNotificationToMainThread(event, Math.abs(difference));
                    break;
                }
            }
            if (isNoti) {
                break;
            }
        }
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
                 Thread.sleep(10000);
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
