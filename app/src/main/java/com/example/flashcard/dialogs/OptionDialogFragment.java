package com.example.flashcard.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.flashcard.R;

public class OptionDialogFragment extends DialogFragment {

    public interface OptionDialogListener {
        void onQuizSelected();
        void onViewSelected();
        void onDeleteSelected();
        void onCancelSelected();
    }

    private OptionDialogListener listener;

    public OptionDialogFragment(OptionDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_option_dialog, container, false);

        Button quizButton = view.findViewById(R.id.quizButton);
        Button viewButton = view.findViewById(R.id.viewButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        quizButton.setOnClickListener(v -> {
            if (listener != null) listener.onQuizSelected();
            dismiss();
        });

        viewButton.setOnClickListener(v -> {
            if (listener != null) listener.onViewSelected();
            dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteSelected();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            if (listener != null) listener.onCancelSelected();
            dismiss();
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(onCreateView(requireActivity().getLayoutInflater(), null, savedInstanceState));
        return builder.create();
    }
}
