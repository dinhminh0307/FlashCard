package com.example.flashcard.controller;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeTableActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private Map<String, Map<String, String>> eventsMap; // Map<Day, Map<Time, EventName>>

    // Variables to track selection
    private boolean isSelecting = false;
    private Set<TextView> selectedCells = new HashSet<>();

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
            tableRow.setPadding(0, 0, 0, 0); // Ensure no extra padding
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Time column
            TextView timeTextView = new TextView(this);
            timeTextView.setText(timeLabel);
            timeTextView.setGravity(Gravity.CENTER);
            timeTextView.setPadding(8, 8, 8, 8);
            timeTextView.setBackgroundResource(R.drawable.cell_border); // Apply border
            tableRow.addView(timeTextView);

            // Add cells for each day
            for (String day : daysOfWeek) {
                TextView cell = new TextView(this);
                cell.setId(View.generateViewId());
                cell.setBackgroundResource(R.drawable.cell_border); // Apply border
                cell.setGravity(Gravity.CENTER);
                cell.setPadding(8, 8, 8, 8);
                cell.setClickable(true);
                cell.setFocusable(true);

                // Set tag to identify the cell
                cell.setTag(day + "_" + timeLabel);

                // Set touch listener
                cell.setOnTouchListener(cellTouchListener);

                tableRow.addView(cell);
            }

            // Add the row to the table
            tableLayout.addView(tableRow);
        }
    }

    // Define the touch listener for cells
    private View.OnTouchListener cellTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            TextView cell = (TextView) view;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (cell.getText().toString().isEmpty()) {
                        isSelecting = true;
                        selectCell(cell);
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (isSelecting) {
                        // Get the cell under the current touch point
                        TextView currentCell = getCellAtPosition(event);
                        if (currentCell != null && currentCell.getText().toString().isEmpty()
                                && !selectedCells.contains(currentCell)) {
                            selectCell(currentCell);
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isSelecting) {
                        isSelecting = false;
                        if (!selectedCells.isEmpty()) {
                            // Prompt to create event for selected cells
                            showCreateEventDialogForSelection();
                        }
                    }
                    return true;

                default:
                    return false;
            }
        }
    };

    private void selectCell(TextView cell) {
        String tag = (String) cell.getTag();
        if (tag != null && !selectedCells.contains(cell)) {
            // Visually indicate selection
            cell.setBackgroundResource(R.drawable.cell_border_highlighted);
            selectedCells.add(cell);
        }
    }

    private TextView getCellAtPosition(MotionEvent event) {
        // Get the location of the touch event on the screen
        int[] location = new int[2];
        tableLayout.getLocationOnScreen(location);
        float x = event.getRawX();
        float y = event.getRawY();

        // Iterate through the cells to find which one contains the touch point
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View row = tableLayout.getChildAt(i);
            if (row instanceof TableRow) {
                TableRow tableRow = (TableRow) row;
                for (int j = 1; j < tableRow.getChildCount(); j++) { // Start from 1 to skip the time column
                    View cellView = tableRow.getChildAt(j);
                    if (cellView instanceof TextView) {
                        TextView cell = (TextView) cellView;
                        int[] cellLocation = new int[2];
                        cell.getLocationOnScreen(cellLocation);
                        int cellX = cellLocation[0];
                        int cellY = cellLocation[1];
                        int cellWidth = cell.getWidth();
                        int cellHeight = cell.getHeight();

                        Rect cellRect = new Rect(cellX, cellY, cellX + cellWidth, cellY + cellHeight);
                        if (cellRect.contains((int) x, (int) y)) {
                            return cell;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void showCreateEventDialogForSelection() {
        // Collect day and time information from selected cells
        List<String> selectedTags = new ArrayList<>();
        for (TextView cell : selectedCells) {
            selectedTags.add((String) cell.getTag());
        }

        // Sort the selected tags (optional)
        Collections.sort(selectedTags);

        // Build a summary of selected time slots
        StringBuilder timeSlots = new StringBuilder();
        Set<String> daysSet = new HashSet<>();
        for (String tag : selectedTags) {
            String[] parts = tag.split("_");
            String day = parts[0];
            String time = parts[1];
            daysSet.add(day);
            timeSlots.append(day).append(" at ").append(time).append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Event");
        builder.setMessage("Create an event for the following time slots?\n" + timeSlots.toString());

        // Input field for event name
        final EditText input = new EditText(this);
        input.setHint("Event Name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String eventName = input.getText().toString().trim();
            if (!eventName.isEmpty()) {
                // Update each selected cell
                for (TextView cell : selectedCells) {
                    cell.setText(eventName);
                    // Remove border to indicate the cell is occupied
                    cell.setBackgroundColor(Color.TRANSPARENT);

                    // Save the event
                    String tag = (String) cell.getTag();
                    String[] parts = tag.split("_");
                    String day = parts[0];
                    String time = parts[1];

                    Map<String, String> dayEvents = eventsMap.getOrDefault(day, new HashMap<>());
                    dayEvents.put(time, eventName);
                    eventsMap.put(day, dayEvents);
                }
                // Clear the selection set
                selectedCells.clear();

                Toast.makeText(this, "Event '" + eventName + "' created.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Event name cannot be empty.", Toast.LENGTH_SHORT).show();
                // Reset cell backgrounds
                resetSelectedCells();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Reset cell backgrounds
            resetSelectedCells();
        });

        builder.show();
    }

    private void resetSelectedCells() {
        for (TextView cell : selectedCells) {
            cell.setBackgroundResource(R.drawable.cell_border);
        }
        selectedCells.clear();
    }
}




