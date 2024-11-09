package com.example.flashcard.models;

public class Quizz {
    private int total;
    private int attempts;
    private String category;

    public Quizz() {}

    public Quizz(int total, int attempts, String category) {
        this.total = total;
        this.attempts = attempts;
        this.category = category;
    }

    // Getter and Setter for 'total'
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // Getter and Setter for 'attempts'
    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    // Getter and Setter for 'category'
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
