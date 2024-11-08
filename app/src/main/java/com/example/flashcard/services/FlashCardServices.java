package com.example.flashcard.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.flashcard.adapters.FlashcardAdapter;
import com.example.flashcard.exceptions.DuplicateQuestionException;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.repo.FlashCardRepository;

import java.util.List;

public class FlashCardServices {
    private FlashCardRepository flashCardRepository;

    public FlashCardServices(Context context) {
        flashCardRepository = new FlashCardRepository(context);
    }

    public List<FlashCard> getFlashCardContent(String tableName, String titleText) {
        try {
            return flashCardRepository.getQuestionsAndAnswers(tableName);

        } catch (Exception e) {
            Log.e("CardPageActivity", "Error fetching flashcards", e);
        }
        return null;
    }

    public FlashCard addQuestion(String question, String answer, String tableName) throws DuplicateQuestionException {
        FlashCard newCard = new FlashCard();
        newCard.setQuestions(question);
        newCard.setAnswers(answer);

        // Call insertQuestion and let DuplicateQuestionException propagate if thrown
        flashCardRepository.insertQuestion(tableName, newCard);

        return newCard; // Return the FlashCard if successfully added
    }

    public void updateFlashCard(FlashCard flashCard, String tableName) throws DuplicateQuestionException{
        if(flashCard.getQuestions().isEmpty()) {
            flashCardRepository.updateAnswerText(tableName, flashCard.getId(), flashCard);
        } else if(flashCard.getAnswers().isEmpty()) {
            flashCardRepository.updateQuestionText(tableName, flashCard.getId(), flashCard);
        }
    }
}
