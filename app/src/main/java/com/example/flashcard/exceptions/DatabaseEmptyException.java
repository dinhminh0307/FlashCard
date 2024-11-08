package com.example.flashcard.exceptions;

public class DatabaseEmptyException extends RuntimeException {
    public DatabaseEmptyException(String message) {
        super(message);
    }
}
