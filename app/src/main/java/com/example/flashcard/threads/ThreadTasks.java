package com.example.flashcard.threads;

import android.content.Context;
import android.util.Log;

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
    }

    /**
     * Starts a new thread to perform background tasks.
     */
    public void runThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        getCurrentDate();

                        // Example usage of EventServices within the thread
                        String date = currentDate.getDayOfWeeks(); // Replace with dynamic date as needed
                        List<Events> events = eventServices.getEventsByDateService(date);

                        // Log the retrieved events
                        for (Events event : events) {
                            Log.d("ThreadTasks", "Event: " + event.getName() + ", Date: " + event.getDate() + ", Times: " + event.getTime());
                        }

                        // If you need to update the UI, consider using a Handler or other mechanisms
                    } catch (InterruptedException e) {
                        Log.e("ThreadTasks", "Thread was interrupted", e);
                        Thread.currentThread().interrupt(); // Restore the interrupted status
                    } catch (Exception e) {
                        Log.e("ThreadTasks", "Error during background task", e);
                    }
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
        Thread.sleep(2000); // Sleep for 2 seconds to simulate a delay
    }
}
