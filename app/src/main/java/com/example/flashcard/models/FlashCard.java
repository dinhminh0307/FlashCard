package com.example.flashcard.models;

public class FlashCard {
    private String id;
    private String questions;
    private String answers;

    public FlashCard() {}

    public FlashCard(String id, String questions, String answers) {
        this.id = id;
        this.questions = questions;
        this.answers = answers;
    }

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for questions
    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    // Getter and Setter for answers
    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }
}
