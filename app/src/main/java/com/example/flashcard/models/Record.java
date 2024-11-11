package com.example.flashcard.models;

import java.util.List;

public class Record {
    private int id;
    private String date;
    private List<Quizz> quizzes;

    // Constructor
    public Record(int id, String date, List<Quizz> quizzes) {
        this.id = id;
        this.date = date;
        this.quizzes = quizzes;
    }

    // Getter and Setter for 'id'
    public int getId() {
        return id;
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

    // Getter and Setter for 'quizzes'
    public List<Quizz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quizz> quizzes) {
        this.quizzes = quizzes;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", quizzes=" + quizzes +
                '}';
    }
}
