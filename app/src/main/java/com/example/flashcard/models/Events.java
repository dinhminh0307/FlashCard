package com.example.flashcard.models;

import java.util.ArrayList;
import java.util.List;

/**
 * this class instance is used as DTO to mark the id of specific timeslots
 */
public class Events {
    int id; // for object that needs to be stored in database, do not set id
    private String name;
    private String date;
    private List<String> time = new ArrayList<>();

    public Events() {}

    public Events(String name, String date, List<String> time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    // Getter and Setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for 'date'
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getter and Setter for 'time'
    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }
}
