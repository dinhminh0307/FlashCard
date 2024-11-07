package com.example.flashcard.services;

import android.content.Context;
import android.util.Log;

import com.example.flashcard.exceptions.NoResourceFound;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.repo.FlashCardRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizzServices {
    private FlashCardRepository flashCardRepository;

    private List<FlashCard> quizz = new ArrayList<>();

    public QuizzServices(Context context) {
        flashCardRepository = new FlashCardRepository(context);
    }

    public void setQuizz(String tableName) {
        try {
            quizz = flashCardRepository.getQuestionsAndAnswers(tableName);

        } catch (Exception e) {
            Log.e("CardPageActivity", "Error fetching flashcards", e);
        }
    }

    private FlashCard checkAnswerInDatabase(FlashCard answer) {
        for(FlashCard f : quizz) {
            if(f.getQuestions().equals(answer.getQuestions())) {
                return f;
            }
        }
        return null;
    }

    public boolean verifyAnswer(FlashCard answer) throws NoResourceFound {
        FlashCard foundFlashCard = checkAnswerInDatabase(answer);
        if(foundFlashCard == null) {
            throw new NoResourceFound("Find no question: " + answer.getQuestions());
        }

        return foundFlashCard.getAnswers().equals(answer.getAnswers());
    }

    public List<String> getQuestions() {
        List<String> questions = new ArrayList<>();
        for(FlashCard c : quizz) {
            questions.add(c.getQuestions());
        }
        return questions;
    }
}
