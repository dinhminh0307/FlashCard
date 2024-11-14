package com.example.flashcard.utils;


import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

public class DateUtils {
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    public String formatTime(String time) {
        try {
            int hour = Integer.parseInt(time.split(":")[0]);
            return String.format("%02d:00", hour);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return time;
        }
    }

}
