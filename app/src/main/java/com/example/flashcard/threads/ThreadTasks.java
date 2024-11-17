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
    }

    /**
     * Checks the time before an event and logs if the event is within 30 minutes.
     *
     * @param events List of events to check.
     */
    private void checkTimeBeforeEvent(List<Events> events) {
        int hrs, mins = 0;
        boolean isNoti = false;
        for (Events event : events) {
            // If the current date is 30 minutes before the schedule
            for (String e_time : event.getTime()) {
                hrs = dateUtils.getHourFromFormatedString(e_time);
                mins = dateUtils.getMinsFromFormatedString(e_time);
                if (currentDate.getHour() == hrs - 1 && Math.abs(mins - currentDate.getMins()) >= 30) {
                    isNoti = true;
                    Log.d("ThreadTasks", "About " + Math.abs(mins - currentDate.getMins()) + " min before the event");
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        getCurrentDate();

                        String date = currentDate.getDayOfWeeks(); // Replace with dynamic date as needed
                        date = dateUtils.mapDay(date);
                        events = eventServices.getEventsByDateService(date);

                        if (events.isEmpty()) {
                            Log.d("ThreadTasks", "No events for Current Date and Time: " +
                                    currentDate.getDayOfWeeks() + " " + currentDate.getTime());
                        }
                        checkTimeBeforeEvent(events);

                        Thread.sleep(2000); // Sleep for 2 seconds to simulate a delay
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
    }
}

