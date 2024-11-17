package com.example.flashcard.utils;


import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import com.example.flashcard.models.ScheduleDate;
import com.google.android.material.timepicker.TimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

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

    public ScheduleDate getCurrentDateAndTime() {
        LocalDateTime now = LocalDateTime.now();
        // Get the current day of the week
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        String day = dayOfWeek.toString();
        int hour = now.getHour();
        int mins = now.getMinute();
        ScheduleDate currentDate = new ScheduleDate(day, hour, mins);
        return  currentDate;
    }

    public String mapDay(String day) {
        switch (day.toUpperCase()) {
            case "MONDAY":
                return "Monday";
            case "TUESDAY":
                return "Tuesday";
            case "WEDNESDAY":
                return "Wednesday";
            case "THURSDAY":
                return "Thursday";
            case "FRIDAY":
                return "Friday";
            case "SATURDAY":
                return "Saturday";
            case "SUNDAY":
                return "Sunday";
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }


    public ScheduleDate convertStringToTime(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int mins = Integer.parseInt(parts[1]);
        ScheduleDate convertedDate = new ScheduleDate();
        convertedDate.setHour(hour);
        convertedDate.setMins(mins);
        return convertedDate;
    }

    public int getHourFromFormatedString(String time) {
        String[] timeParts = time.split(":");
        return Integer.parseInt(timeParts[0]);
    }

    public int getMinsFromFormatedString(String time) {
        String[] timeParts = time.split(":");
        return Integer.parseInt(timeParts[1]);
    }
}
