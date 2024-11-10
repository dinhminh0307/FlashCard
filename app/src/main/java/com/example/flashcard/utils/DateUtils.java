package com.example.flashcard.utils;


import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

public class DateUtils {
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }
}
