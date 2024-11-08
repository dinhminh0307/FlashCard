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
import com.example.flashcard.exceptions.NoResourceFound;
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
        // Initialize FlashCardServices
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

        // Handle Save Button
        saveButton.setOnClickListener(v -> {
            String updatedQuestion = questionEdit.getText().toString().trim();
            String updatedAnswer = answerEdit.getText().toString().trim();

            if (updatedQuestion.equals(flashCard.getQuestions())) {
                updatedQuestion = ""; // Leave question unchanged
            }
            if (updatedAnswer.equals(flashCard.getAnswers())) {
                updatedAnswer = ""; // Leave answer unchanged
            }

            flashCard.setQuestions(updatedQuestion);
            flashCard.setAnswers(updatedAnswer);

            try {
                // Attempt to update the flashcard in the database
                flashCardServices.updateFlashCard(flashCard, tableName);
                Toast.makeText(getContext(), "Flashcard updated successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } catch (DuplicateQuestionException e) {
                // Handle duplicate question error
                Toast.makeText(getContext(), "Duplicate question found", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Delete Button
        deleteButton.setOnClickListener(v -> {
            try {
                // Attempt to delete the flashcard
                flashCardServices.deleteQuestion(flashCard, tableName);
                Toast.makeText(getContext(), "Flashcard deleted successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } catch (NoResourceFound e) {
                // Handle case where the question ID was not found
                Toast.makeText(getContext(), "Question not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Cancel Button
        cancelButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }


}
