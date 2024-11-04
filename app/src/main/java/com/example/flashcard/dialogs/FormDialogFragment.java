package com.example.flashcard.dialogs;

import android.app.Dialog;
import android.content.Context;
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
import com.example.flashcard.exceptions.DuplicateQuestionException;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.repo.FlashCardRepository;
import com.example.flashcard.services.FlashCardServices;

public class FormDialogFragment extends DialogFragment {

    private FlashCardServices flashCardServices;
    private String tableName;
    private OnQuestionAddedListener questionAddedListener;

    public FormDialogFragment(String tableName) {
        this.tableName = tableName;
    }

    public interface OnQuestionAddedListener {
        void onQuestionAdded();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Ensure that the activity implements the listener
            questionAddedListener = (OnQuestionAddedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnQuestionAddedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        flashCardServices = new FlashCardServices(requireContext());

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

            if (TextUtils.isEmpty(question) || TextUtils.isEmpty(answer)) {
                Toast.makeText(getContext(), "Please enter both question and answer", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    flashCardServices.addQuestion(question, answer, tableName);
                    questionAddedListener.onQuestionAdded(); // Notify CardPageActivity if needed
                    Toast.makeText(getContext(), "Flashcard saved", Toast.LENGTH_SHORT).show();
                    dismiss(); // Close the dialog after saving
                } catch (DuplicateQuestionException e) {
                    // Display specific error message when duplicate question is found
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for the cancel button
        cancelButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}
