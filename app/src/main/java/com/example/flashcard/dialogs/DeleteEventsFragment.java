package com.example.flashcard.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.flashcard.R;

/**
 * DeleteEventsFragment is a DialogFragment that confirms the deletion of an event.
 * It communicates the user's choice back to the hosting activity through the DeleteEventsListener interface.
 */
public class DeleteEventsFragment extends DialogFragment {

    /**
     * Interface to communicate deletion confirmation back to the activity.
     */
    public interface DeleteEventsListener {
        /**
         * Called when the user confirms the deletion of an event.
         *
         * @param eventName The name of the event to delete.
         * @param day       The day of the event.
         * @param time      The time of the event.
         */
        void onDeleteConfirmed(String eventName, String day, String time);
    }

    private DeleteEventsListener listener;

    // Keys for passing data through arguments
    private static final String ARG_EVENT_NAME = "eventName";
    private static final String ARG_DAY = "day";
    private static final String ARG_TIME = "time";

    /**
     * Factory method to create a new instance of DeleteEventsFragment with the necessary parameters.
     *
     * @param eventName The name of the event to delete.
     * @param day       The day of the event.
     * @param time      The time of the event.
     * @return A new instance of DeleteEventsFragment.
     */
    public static DeleteEventsFragment newInstance(String eventName, String day, String time) {
        DeleteEventsFragment fragment = new DeleteEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_NAME, eventName);
        args.putString(ARG_DAY, day);
        args.putString(ARG_TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteEventsListener) {
            listener = (DeleteEventsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DeleteEventsListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Retrieve event details from arguments
        String eventName;
        String day;
        String time;

        if (getArguments() != null) {
            eventName = getArguments().getString(ARG_EVENT_NAME, "");
            day = getArguments().getString(ARG_DAY, "");
            time = getArguments().getString(ARG_TIME, "");
        } else {
            eventName = "";
            day = "";
            time = "";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Event");
        builder.setMessage("Do you want to delete the event '" + eventName + "' on " + day + " at " + time + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (listener != null) {
                listener.onDeleteConfirmed(eventName, day, time);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User cancelled the dialog
            dialog.dismiss();
        });

        return builder.create();
    }
}
