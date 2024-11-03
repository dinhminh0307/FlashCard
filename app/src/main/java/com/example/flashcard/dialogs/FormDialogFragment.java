package com.example.flashcard.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.repo.FlashCardRepository;

public class FormDialogFragment extends DialogFragment {

    private FlashCardRepository flashCardRepository;
    private String tableName;

    public FormDialogFragment(String tableName) {
        this.tableName = tableName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        flashCardRepository = new FlashCardRepository(requireContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_form_dialog, null);

        // Retrieve UI elements
        EditText questionInput = view.findViewById(R.id.questionInput);
        EditText answerInput = view.findViewById(R.id.answerInput);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        // Set click listener for the save button
        saveButton.setOnClickListener(v -> {
            String question = questionInput.getText().toString().trim();
            String answer = answerInput.getText().toString().trim();

            FlashCard card = new FlashCard();
            card.setAnswers(answer);
            card.setQuestions(question);

            if (TextUtils.isEmpty(question) || TextUtils.isEmpty(answer)) {
                Toast.makeText(getContext(), "Please enter both question and answer", Toast.LENGTH_SHORT).show();
            } else {
                // Save to database
                flashCardRepository.insertQuestion(tableName, card);
                Toast.makeText(getContext(), "Flashcard saved", Toast.LENGTH_SHORT).show();
                dismiss(); // Close the dialog after saving
            }
        });

        // Set click listener for the cancel button
        cancelButton.setOnClickListener(v -> {
            dismiss(); // Simply close the dialog without saving
        });

        // Set the custom view for the dialog
        builder.setView(view);

        return builder.create();
    }
}
