package com.example.flashcard.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcard.R;
import com.example.flashcard.dialogs.AddEventsFragment;
import com.example.flashcard.dialogs.DeleteEventsFragment;
import com.example.flashcard.models.Events;
import com.example.flashcard.services.EventServices;
import com.example.flashcard.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * TimeTableActivity manages the display and interaction of a weekly timetable.
 * Users can add, delete, and view events within the timetable.
 */
public class TimeTableActivity extends AppCompatActivity implements
        AddEventsFragment.AddEventsListener,
        DeleteEventsFragment.DeleteEventsListener {

    private TableLayout tableLayout;
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private final Map<String, Map<String, String>> eventsMap = new HashMap<>(); // Map<Day, Map<Time, EventName>>

    // Variables to track selection
    private boolean isSelecting = false;
    private final HashSet<TextView> selectedCells = new HashSet<>();

    private final EventServices eventServices = new EventServices(this);

    // Store references to cells for updating
    private final Map<String, TextView> cellMap = new HashMap<>(); // Key: "Day_Time", Value: TextView

    private final DateUtils dateUtils = new DateUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        tableLayout = findViewById(R.id.tableLayout);

        generateTimeTable();
        loadEventsAndDisplay();
        setupReturnButton();
    }

    /**
     * Generates the entire timetable by adding the header and time slot rows.
     */
    private void generateTimeTable() {
        // Add header row with days of the week
        addHeaderRow();

        // Generate time slots from 00:00 to 23:00
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
        headerCell.setBackgroundResource(R.drawable.header_cell_background); // Use header background drawable
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
                    if (cell.getText().toString().isEmpty()) {
                        handleActionDown(cell);
                    } else {
                        handleEventCellClick(cell);
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (isSelecting) {
                        handleActionMove(event);
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isSelecting) {
                        handleActionUp();
                    }
                    return true;

                default:
                    return false;
            }
        }
    };

    /**
     * Handles the click event on a cell that already contains an event.
     *
     * @param cell The cell that was clicked.
     */
    private void handleEventCellClick(TextView cell) {
        String eventName = cell.getText().toString();
        String tag = (String) cell.getTag();
        if (tag == null) {
            return;
        }
        // Retrieve the day and time from the cell's tag
        String[] parts = tag.split("_");
        if (parts.length != 2) {
            Log.e("TimeTableActivity", "Invalid cell tag format: " + tag);
            return;
        }
        String day = parts[0];
        String time = parts[1];

        // Show confirmation dialog using DeleteEventsFragment
        showDeleteEventDialog(cell, eventName, day, time);
    }

    /**
     * Launches the DeleteEventsFragment to confirm event deletion.
     *
     * @param cell      The cell containing the event.
     * @param eventName The name of the event.
     * @param day       The day of the event.
     * @param time      The time of the event.
     */
    private void showDeleteEventDialog(TextView cell, String eventName, String day, String time) {
        DeleteEventsFragment deleteEventsFragment = DeleteEventsFragment.newInstance(eventName, day, time);
        deleteEventsFragment.show(getSupportFragmentManager(), "DeleteEventsFragment");
    }

    /**
     * Handles the deletion of an event after user confirmation.
     *
     * @param eventName The name of the event to delete.
     * @param day       The day of the event.
     * @param time      The time of the event.
     */
    @Override
    public void onDeleteConfirmed(String eventName, String day, String time) {
        // Retrieve the corresponding cell
        String tag = day + "_" + time;
        TextView cell = cellMap.get(tag);
        if (cell != null) {
            // Delete the event from the database and update the UI
            deleteEvent(cell, eventName, day, time);
        } else {
            Log.e("TimeTableActivity", "Cell not found for tag: " + tag);
            Toast.makeText(this, "Failed to delete event: Cell not found.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Deletes the event from the database and updates the UI.
     *
     * @param cell      The cell containing the event.
     * @param eventName The name of the event.
     * @param day       The day of the event.
     * @param time      The time of the event.
     */
    private void deleteEvent(TextView cell, String eventName, String day, String time) {
        // Remove the event from the database
        try {
            eventServices.deleteEvent(eventName, day, time);
        } catch (Exception e) {
            Log.e("TimeTableActivity", "Failed to delete event: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Update the UI
        cell.setText("");
        cell.setBackgroundResource(R.drawable.cell_border);

        // Remove from eventsMap
        Map<String, String> dayEvents = eventsMap.get(day);
        if (dayEvents != null) {
            dayEvents.remove(time);
            if (dayEvents.isEmpty()) {
                eventsMap.remove(day);
            }
        }

        Toast.makeText(this, "Event '" + eventName + "' deleted.", Toast.LENGTH_SHORT).show();
    }

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
     * Displays the AddEventsFragment dialog for creating an event.
     */
    private void showCreateEventDialogForSelection() {
        // Collect selected tags
        List<String> selectedTagsList = new ArrayList<>();
        for (TextView cell : selectedCells) {
            String tag = (String) cell.getTag();
            if (tag != null) {
                selectedTagsList.add(tag);
            }
        }

        // Create and show the AddEventsFragment
        AddEventsFragment addEventsFragment = AddEventsFragment.newInstance(selectedTagsList);
        addEventsFragment.show(getSupportFragmentManager(), "AddEventsFragment");
    }

    /**
     * Callback method when an event is created from the AddEventsFragment.
     *
     * @param eventName    The name of the created event.
     * @param selectedTags The list of selected time slot tags.
     */
    @Override
    public void onEventCreated(String eventName, List<String> selectedTags) {
        updateCellsWithEvent(eventName, selectedTags);
        Toast.makeText(this, "Event '" + eventName + "' created.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the selected cells with the event name, removes their borders, and saves the event to the database.
     *
     * @param eventName    The name of the event to display.
     * @param selectedTags The list of selected cell tags representing time slots.
     */
    private void updateCellsWithEvent(String eventName, List<String> selectedTags) {
        // Map to group times by day
        Map<String, List<String>> dayTimesMap = new HashMap<>();

        for (String tag : selectedTags) {
            TextView cell = cellMap.get(tag);
            if (cell != null) {
                cell.setText(eventName);
                cell.setTextColor(Color.WHITE); // Set text color to white for visibility
                // Remove border to indicate the cell is occupied
                cell.setBackgroundColor(Color.argb(255, 93, 58, 141)); // Example: Purple color

                // Retrieve the day and time from the cell's tag
                String[] parts = tag.split("_");
                if (parts.length != 2) {
                    Log.e("TimeTableActivity", "Invalid tag format: " + tag);
                    continue;
                }

                String day = parts[0];
                String time = parts[1];
                time = dateUtils.formatTime(time);

                // Add time to the corresponding day in the map
                List<String> times = dayTimesMap.getOrDefault(day, new ArrayList<>());
                times.add(time);
                dayTimesMap.put(day, times);

                // Update eventsMap
                Map<String, String> dayEvents = eventsMap.getOrDefault(day, new HashMap<>());
                dayEvents.put(time, eventName);
                eventsMap.put(day, dayEvents);
            } else {
                Log.e("TimeTableActivity", "Cell not found for tag: " + tag);
            }
        }

        // Clear the selection set
        resetSelectedCells();

        // Save events to the database for each day
        for (Map.Entry<String, List<String>> entry : dayTimesMap.entrySet()) {
            String day = entry.getKey();
            List<String> times = entry.getValue();

            Events event = new Events();
            event.setName(eventName);
            event.setDate(day);
            event.setTime(times);

            try {
                eventServices.saveEventTimeTable(event);
            } catch (Exception e) {
                Log.e("TimeTableActivity", "Failed to save event: " + e.getMessage(), e);
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
                    cell.setTextColor(Color.WHITE);
                    cell.setBackgroundColor(Color.argb(255, 93, 58, 141)); // Example: Purple color
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

    /**
     * Sets up the return button to finish the activity when clicked.
     */
    private void setupReturnButton() {
        ImageView returnButton = findViewById(R.id.exitButton);
        returnButton.setOnClickListener(v -> finish());
    }
}
