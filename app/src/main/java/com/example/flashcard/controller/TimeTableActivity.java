package com.example.flashcard.controller;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcard.R;
import com.example.flashcard.models.Events;
import com.example.flashcard.services.EventServices;
import com.example.flashcard.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TimeTableActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private Map<String, Map<String, String>> eventsMap; // Map<Day, Map<Time, EventName>>

    // Variables to track selection
    private boolean isSelecting = false;
    private HashSet<TextView> selectedCells = new HashSet<>();

    private EventServices eventServices;

    // Store references to cells for updating
    private Map<String, TextView> cellMap; // Key: "Day_Time", Value: TextView

    private DateUtils dateUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        tableLayout = findViewById(R.id.tableLayout);
        eventsMap = new HashMap<>();
        dateUtils = new DateUtils();
        eventServices = new EventServices(this);

        cellMap = new HashMap<>();

        generateTimeTable();
        loadEventsAndDisplay();
        onReturnBtn();
    }

    /**
     * Generates the entire timetable by adding the header and time slot rows.
     */
    private void generateTimeTable() {
        // Add header row with days of the week
        addHeaderRow();

        // Generate time slots from 0h to 24h
        for (int hour = 0; hour < 24; hour++) {
            String timeLabel = String.format("%02d:00", hour);
            TableRow tableRow = createTimeSlotRow(timeLabel);
            tableLayout.addView(tableRow);
        }
    }

    /**
     * Adds the header row containing the days of the week to the timetable.
     */
    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        // Empty cell at the top-left corner
        TextView emptyCell = new TextView(this);
        emptyCell.setBackgroundResource(R.drawable.cell_border);
        emptyCell.setPadding(8, 8, 8, 8);
        headerRow.addView(emptyCell);

        // Add day headers
        for (String day : daysOfWeek) {
            TextView dayHeader = createHeaderCell(day);
            headerRow.addView(dayHeader);
        }

        tableLayout.addView(headerRow);
    }

    /**
     * Creates a header cell with the given text.
     *
     * @param text The text to display in the header cell.
     * @return A TextView configured as a header cell.
     */
    private TextView createHeaderCell(String text) {
        TextView headerCell = new TextView(this);
        headerCell.setText(text);
        headerCell.setGravity(Gravity.CENTER);
        headerCell.setTypeface(null, android.graphics.Typeface.BOLD);
        headerCell.setPadding(8, 8, 8, 8);
        headerCell.setTextColor(Color.WHITE); // Set text color to white
        headerCell.setBackgroundResource(R.drawable.header_cell_background); // Use new drawable
        return headerCell;
    }




    /**
     * Creates a table row for a specific time slot, including time and day cells.
     *
     * @param timeLabel The label for the time slot (e.g., "09:00").
     * @return A TableRow containing the time slot data.
     */
    private TableRow createTimeSlotRow(String timeLabel) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 0, 0, 0); // Ensure no extra padding
        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        // Time cell
        TextView timeTextView = createTimeCell(timeLabel);
        tableRow.addView(timeTextView);

        // Day cells
        for (String day : daysOfWeek) {
            TextView cell = createTimeSlotCell(day, timeLabel);
            tableRow.addView(cell);
        }

        return tableRow;
    }


    /**
     * Creates a time cell for the leftmost column.
     *
     * @param timeLabel The label for the time slot (e.g., "09:00").
     * @return A TextView configured as a time cell.
     */
    private TextView createTimeCell(String timeLabel) {
        TextView timeCell = new TextView(this);
        timeCell.setText(timeLabel);
        timeCell.setGravity(Gravity.CENTER);
        timeCell.setPadding(8, 8, 8, 8);
        timeCell.setBackgroundResource(R.drawable.cell_border);
        return timeCell;
    }

    /**
     * Creates a cell for a specific day and time.
     *
     * @param day       The day of the week.
     * @param timeLabel The time slot label.
     * @return A TextView configured as a time slot cell.
     */
    private TextView createTimeSlotCell(String day, String timeLabel) {
        TextView cell = new TextView(this);
        cell.setId(View.generateViewId());
        cell.setBackgroundResource(R.drawable.cell_border);
        cell.setGravity(Gravity.CENTER);
        cell.setPadding(8, 8, 8, 8);
        cell.setClickable(true);
        cell.setFocusable(true);

        // Set tag to identify the cell
        String tag = day + "_" + timeLabel;
        cell.setTag(tag);

        // Add cell to cellMap for easy access
        cellMap.put(tag, cell);

        // Set touch listener
        cell.setOnTouchListener(cellTouchListener);

        return cell;
    }

    /**
     * Touch listener for the timetable cells to handle multi-selection.
     */
    private final View.OnTouchListener cellTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            TextView cell = (TextView) view;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    handleActionDown(cell);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    handleActionMove(event);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handleActionUp();
                    return true;

                default:
                    return false;
            }
        }
    };

    /**
     * Handles the ACTION_DOWN touch event.
     *
     * @param cell The cell where the touch event started.
     */
    private void handleActionDown(TextView cell) {
        if (cell.getText().toString().isEmpty()) {
            isSelecting = true;
            selectCell(cell);
        }
    }

    /**
     * Handles the ACTION_MOVE touch event.
     *
     * @param event The MotionEvent containing touch coordinates.
     */
    private void handleActionMove(MotionEvent event) {
        if (isSelecting) {
            TextView currentCell = getCellAtPosition(event);
            if (currentCell != null && currentCell.getText().toString().isEmpty()
                    && !selectedCells.contains(currentCell)) {
                selectCell(currentCell);
            }
        }
    }

    /**
     * Handles the ACTION_UP and ACTION_CANCEL touch events.
     */
    private void handleActionUp() {
        if (isSelecting) {
            isSelecting = false;
            if (!selectedCells.isEmpty()) {
                showCreateEventDialogForSelection();
            }
        }
    }

    /**
     * Selects a cell and visually indicates the selection.
     *
     * @param cell The cell to select.
     */
    private void selectCell(TextView cell) {
        String tag = (String) cell.getTag();
        if (tag != null && !selectedCells.contains(cell)) {
            // Visually indicate selection
            cell.setBackgroundResource(R.drawable.cell_border_highlighted);
            selectedCells.add(cell);
        }
    }

    /**
     * Retrieves the cell at the current touch position.
     *
     * @param event The MotionEvent containing touch coordinates.
     * @return The TextView cell at the touch position, or null if none found.
     */
    private TextView getCellAtPosition(MotionEvent event) {
        // Get touch coordinates
        float x = event.getRawX();
        float y = event.getRawY();

        // Iterate over the table rows
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View rowView = tableLayout.getChildAt(i);
            if (rowView instanceof TableRow) {
                TableRow tableRow = (TableRow) rowView;

                // Iterate over the cells in the row
                for (int j = 1; j < tableRow.getChildCount(); j++) { // Skip the time column
                    View cellView = tableRow.getChildAt(j);
                    if (cellView instanceof TextView) {
                        TextView cell = (TextView) cellView;
                        if (isPointInsideView(x, y, cell)) {
                            return cell;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a point is inside a given view.
     *
     * @param x    The x-coordinate of the point.
     * @param y    The y-coordinate of the point.
     * @param view The view to check against.
     * @return True if the point is inside the view, false otherwise.
     */
    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();

        return x >= left && x <= right && y >= top && y <= bottom;
    }

    /**
     * Displays a dialog for creating an event with the selected time slots.
     */
    private void showCreateEventDialogForSelection() {
        // Collect day and time information from selected cells
        List<String> selectedTags = new ArrayList<>();
        for (TextView cell : selectedCells) {
            selectedTags.add((String) cell.getTag());
        }

        // Sort the selected tags for consistent display
        Collections.sort(selectedTags);

        // Build a summary of selected time slots
        StringBuilder timeSlots = new StringBuilder();
        for (String tag : selectedTags) {
            String[] parts = tag.split("_");
            String day = parts[0];
            String time = parts[1];
            timeSlots.append(day).append(" at ").append(time).append("\n");
        }

        // Show the event creation dialog
        createEventDialog(timeSlots.toString());
    }

    /**
     * Creates and displays the event creation dialog.
     *
     * @param timeSlotsSummary A summary of the selected time slots.
     */
    private void createEventDialog(String timeSlotsSummary) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Event");
        builder.setMessage("Create an event for the following time slots?\n" + timeSlotsSummary);

        // Input field for event name
        final EditText input = new EditText(this);
        input.setHint("Event Name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String eventName = input.getText().toString().trim();
            if (!eventName.isEmpty()) {
                updateCellsWithEvent(eventName);
                Toast.makeText(this, "Event '" + eventName + "' created.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Event name cannot be empty.", Toast.LENGTH_SHORT).show();
                resetSelectedCells();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            resetSelectedCells();
        });

        builder.show();
    }

    /**
     * Updates the selected cells with the event name, removes their borders, and saves the event to the database.
     *
     * @param eventName The name of the event to display.
     */
    private void updateCellsWithEvent(String eventName) {
        // Map to group times by day
        Map<String, List<String>> dayTimesMap = new HashMap<>();

        for (TextView cell : selectedCells) {
            cell.setText(eventName);
            // Remove border to indicate the cell is occupied
            cell.setBackgroundColor(Color.TRANSPARENT);

            // Retrieve the day and time from the cell's tag
            String tag = (String) cell.getTag();
            String[] parts = tag.split("_");
            String day = parts[0];
            String time = parts[1];
            time = dateUtils.formatTime(time);

            // Add time to the corresponding day in the map
            List<String> times = dayTimesMap.getOrDefault(day, new ArrayList<>());
            times.add(time);
            dayTimesMap.put(day, times);

            // Update eventsMap (if needed for your application logic)
            Map<String, String> dayEvents = eventsMap.getOrDefault(day, new HashMap<>());
            dayEvents.put(time, eventName);
            eventsMap.put(day, dayEvents);
        }
        // Clear the selection set
        selectedCells.clear();

        // Save events to the database for each day
        for (Map.Entry<String, List<String>> entry : dayTimesMap.entrySet()) {
            String day = entry.getKey();
            List<String> times = entry.getValue();

            Events event = new Events();
            event.setName(eventName);
            event.setDate(day); // You may need to map 'day' to an actual date
            event.setTime(times);

            try {
                eventServices.saveEventTimeTable(event);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save event for " + day + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Loads events from the database and displays them on the timetable.
     */
    private void loadEventsAndDisplay() {
        List<Events> eventsList = eventServices.getAllEvents();
        Log.d("TimeTableActivity", "Number of events loaded: " + eventsList.size());

        for (Events event : eventsList) {
            Log.d("TimeTableActivity", "Event loaded: " + event.getName() + ", Date: " + event.getDate() + ", Times: " + event.getTime());

            String eventName = event.getName();
            String day = event.getDate(); // Ensure day names are consistent
            List<String> times = event.getTime();

            for (String time : times) {
                time = dateUtils.formatTime(time); // Ensure time format consistency
                String tag = day + "_" + time;

                TextView cell = cellMap.get(tag);
                if (cell != null) {
                    cell.setText(eventName);
                    cell.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    Log.d("TimeTableActivity", "Cell not found for tag: " + tag);
                }
            }
        }
    }


    /**
     * Resets the selected cells to their default appearance if event creation is canceled.
     */
    private void resetSelectedCells() {
        for (TextView cell : selectedCells) {
            cell.setBackgroundResource(R.drawable.cell_border);
        }
        selectedCells.clear();
    }

    private void onReturnBtn() {
        ImageView returnButn = findViewById(R.id.exitButton);
        returnButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
