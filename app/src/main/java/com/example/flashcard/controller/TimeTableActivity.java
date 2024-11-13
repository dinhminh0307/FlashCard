package com.example.flashcard.controller;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.R;

import java.util.HashMap;
import java.util.Map;

public class TimeTableActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private Map<String, Map<String, String>> eventsMap; // Map<Day, Map<Time, EventName>>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        tableLayout = findViewById(R.id.tableLayout);
        eventsMap = new HashMap<>();

        generateTimeTable();
    }

    private void generateTimeTable() {
        // Generate time slots from 0h to 24h
        for (int hour = 0; hour < 24; hour++) {
            String timeLabel = String.format("%02d:00", hour);

            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(8, 8, 8, 8);

            // Time column
            TextView timeTextView = new TextView(this);
            timeTextView.setText(timeLabel);
            timeTextView.setGravity(Gravity.CENTER);
            timeTextView.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            timeTextView.setPadding(4, 4, 4, 4);
            tableRow.addView(timeTextView);

            // Add cells for each day
            for (String day : daysOfWeek) {
                TextView cell = new TextView(this);
                cell.setId(View.generateViewId());
                cell.setBackgroundColor(Color.parseColor("#E0E0E0")); // Default background color
                cell.setGravity(Gravity.CENTER);
                cell.setPadding(8, 8, 8, 8);
                cell.setClickable(true);
                cell.setFocusable(true);

                // Set tag to identify the cell
                cell.setTag(day + "_" + timeLabel);

                // Set click listener
                cell.setOnClickListener(v -> onTimeSlotClick(v));

                tableRow.addView(cell);
            }

            // Add the row to the table
            tableLayout.addView(tableRow);
        }
    }

    // Handle cell clicks
    public void onTimeSlotClick(View view) {
        String tag = (String) view.getTag();
        if (tag != null) {
            String[] parts = tag.split("_");
            String day = parts[0];
            String time = parts[1];

            showCreateEventDialog((TextView) view, day, time);
        }
    }

    private void showCreateEventDialog(TextView cellView, String day, String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Event");
        builder.setMessage("Create an event for " + day + " at " + time + "?");

        // Input field for event name
        final EditText input = new EditText(this);
        input.setHint("Event Name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String eventName = input.getText().toString().trim();
            if (!eventName.isEmpty()) {
                // Update the cell with the event name
                cellView.setText(eventName);

                // Highlight the cell by changing its background color
                cellView.setBackgroundColor(Color.parseColor("#B3E5FC")); // Light blue background

                // Save the event
                Map<String, String> dayEvents = eventsMap.getOrDefault(day, new HashMap<>());
                dayEvents.put(time, eventName);
                eventsMap.put(day, dayEvents);

                Toast.makeText(this, "Event '" + eventName + "' created for " + day + " at " + time, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Event name cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}



