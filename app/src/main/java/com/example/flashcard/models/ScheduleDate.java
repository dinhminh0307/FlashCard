package com.example.flashcard.models;

public class ScheduleDate {
    private String dayOfWeeks;
    private int hour;
    private int mins;

    // Default constructor
    public ScheduleDate() {}

    // Parameterized constructor
    public ScheduleDate(String dayOfWeeks, int hour, int mins) {
        this.dayOfWeeks = dayOfWeeks;
        this.hour = hour;
        this.mins = mins;
    }

    // Getter for dayOfWeeks
    public String getDayOfWeeks() {
        return dayOfWeeks;
    }

    // Setter for dayOfWeeks
    public void setDayOfWeeks(String dayOfWeeks) {
        this.dayOfWeeks = dayOfWeeks;
    }

    // Getter for hour
    public int getHour() {
        return hour;
    }

    // Setter for hour
    public void setHour(int hour) {
        this.hour = hour;
    }

    // Getter for mins
    public int getMins() {
        return mins;
    }

    // Setter for mins
    public void setMins(int mins) {
        this.mins = mins;
    }

    // Utility method to get time as a formatted string
    public String getTime() {
        return String.format("%02d:%02d", hour, mins);
    }

    // Setter to set time using a string in "HH:MM" format
    public void setTime(String time) {
        String[] parts = time.split(":");
        this.hour = Integer.parseInt(parts[0]);
        this.mins = Integer.parseInt(parts[1]);
    }
}
