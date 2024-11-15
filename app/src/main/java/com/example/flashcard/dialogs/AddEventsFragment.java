package com.example.flashcard.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.flashcard.R;

import java.util.ArrayList;
import java.util.List;

public class AddEventsFragment extends DialogFragment {

    public interface AddEventsListener {
        void onEventCreated(String eventName, List<String> selectedTags);
    }

    private AddEventsListener listener;
    private List<String> selectedTags; // Pass the selected time slots to the fragment

    // Factory method to create a new instance of the fragment with selected tags
    public static AddEventsFragment newInstance(List<String> selectedTags) {
        AddEventsFragment fragment = new AddEventsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("selectedTags", new ArrayList<>(selectedTags));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventsListener) {
            listener = (AddEventsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddEventsListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Retrieve selected tags from arguments
        if (getArguments() != null) {
            selectedTags = getArguments().getStringArrayList("selectedTags");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create Event");

        // Inflate the dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_events, null);
        final EditText input = view.findViewById(R.id.editTextEventName);

        // Display selected time slots summary
        TextView textViewSummary = view.findViewById(R.id.textViewTimeSlotsSummary);
        StringBuilder timeSlots = new StringBuilder();
        if (selectedTags != null) {
            for (String tag : selectedTags) {
                String[] parts = tag.split("_");
                if (parts.length == 2) {
                    String day = parts[0];
                    String time = parts[1];
                    timeSlots.append(day).append(" at ").append(time).append("\n");
                }
            }
        }
        textViewSummary.setText(timeSlots.toString());

        builder.setView(view)
                .setPositiveButton("Create", (dialog, which) -> {
                    String eventName = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(eventName)) {
                        listener.onEventCreated(eventName, selectedTags);
                    } else {
                        Toast.makeText(getActivity(), "Event name cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User cancelled the dialog
                    dialog.cancel();
                });

        return builder.create();
    }
}
