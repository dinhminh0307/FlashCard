package com.example.flashcard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.flashcard.R;
import com.example.flashcard.exceptions.DuplicateQuestionException;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.services.FlashCardServices;

public class FlashCardEditFragment extends DialogFragment {

    private EditText questionEdit, answerEdit;
    private Button saveButton, deleteButton, cancelButton;
    private FlashCard flashCard; // Current flashcard data to edit
    private String tableName;

    private String questionId;

    private FlashCardServices flashCardServices;

    public FlashCardEditFragment(FlashCard flashCard, String tableName, String questionId) {
        this.flashCard = flashCard; // Receive the flashcard to edit
        this.tableName = tableName;
        this.questionId = questionId;
    }

    @NonNull
    @Override

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Initialize flashCardServices with the context
        flashCardServices = new FlashCardServices(requireContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_flash_card_edit, null);

        // Initialize UI elements
        questionEdit = view.findViewById(R.id.questionEdit);
        answerEdit = view.findViewById(R.id.answerEdit);
        saveButton = view.findViewById(R.id.saveEdit);
        deleteButton = view.findViewById(R.id.deleteButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        // Populate fields with existing data
        questionEdit.setText(flashCard.getQuestions());
        answerEdit.setText(flashCard.getAnswers());

        // Set up button actions
        saveButton.setOnClickListener(v -> {
            String updatedQuestion = questionEdit.getText().toString().trim();
            String updatedAnswer = answerEdit.getText().toString().trim();

            if(updatedQuestion.equals(flashCard.getQuestions())) {
                updatedQuestion = "";
            } else if(updatedAnswer.equals(flashCard.getAnswers())) {
                updatedAnswer = "";
            }
            // Update flashcard data and notify the calling activity if needed
            flashCard.setQuestions(updatedQuestion);
            flashCard.setAnswers(updatedAnswer);
            flashCard.setId(questionId);

            try {
                flashCardServices.updateFlashCard(flashCard, tableName);
                Toast.makeText(getContext(), "Flashcard updated", Toast.LENGTH_SHORT).show();
            } catch (DuplicateQuestionException e) {
                Toast.makeText(getContext(), "Duplicate Question", Toast.LENGTH_SHORT).show();
            }

            // Dismiss the dialog
            dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            // Implement deletion logic here
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        // Set custom view to the dialog
        builder.setView(view);

        return builder.create();
    }

}
