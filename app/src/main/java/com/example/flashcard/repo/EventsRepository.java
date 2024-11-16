package com.example.flashcard.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flashcard.models.Events;
import com.example.flashcard.exceptions.NoResourceFound;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flashcard.db";
    private static final int DATABASE_VERSION = 2;

    // Table name
    private static final String TABLE_EVENTS = "Events";

    // Columns for the Events table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";   // Event name
    private static final String COLUMN_DATE = "date";   // Event date
    private static final String COLUMN_TIMES = "times"; // Event times stored as JSON string

    public EventsRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the Events table
        String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_TIMES + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
        Log.d("EventsRepository", "Events table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement schema changes and data massage here when upgrading
        Log.d("EventsRepository", "Database upgraded from version " + oldVersion + " to " + newVersion);
        // For now, do nothing to preserve data
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement schema changes and data massage here when downgrading
        Log.d("EventsRepository", "Database downgraded from version " + oldVersion + " to " + newVersion);
        // For now, do nothing to preserve data
    }

    /**
     * Inserts an event into the database.
     *
     * @param event The Events object to insert.
     */
    public void insertEvent(Events event) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, event.getName());
            values.put(COLUMN_DATE, event.getDate());
            values.put(COLUMN_TIMES, timesListToJson(event.getTime()));

            long result = db.insert(TABLE_EVENTS, null, values);
            if (result == -1) {
                Log.e("EventsRepository", "Failed to insert event");
            } else {
                Log.d("EventsRepository", "Event inserted with ID: " + result);
            }
        } catch (Exception e) {
            Log.e("EventsRepository", "Error inserting event", e);
        } finally {
            db.close();
        }
    }

    /**
     * Retrieves all events from the database.
     *
     * @return A list of Events objects.
     */
    public List<Events> getAllEvents() {
        List<Events> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);

            if (cursor.moveToFirst()) {
                do {
                    // Get column indexes safely
                    int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                    int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
                    int timesIndex = cursor.getColumnIndex(COLUMN_TIMES);

                    if (nameIndex == -1 || dateIndex == -1 || timesIndex == -1) {
                        throw new IllegalArgumentException("One or more columns not found in table " + TABLE_EVENTS);
                    }

                    String name = cursor.getString(nameIndex);
                    String date = cursor.getString(dateIndex);
                    String timesJson = cursor.getString(timesIndex);

                    List<String> times = jsonToTimesList(timesJson);
                    Events event = new Events(name, date, times);
                    eventsList.add(event);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("EventsRepository", "Error retrieving events", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return eventsList;
    }

    /**
     * Deletes a specific time slot from an event. If no time slots remain, deletes the entire event.
     *
     * @param name The name of the event.
     * @param date The date of the event.
     * @param time The specific time slot to delete.
     * @throws Exception If the event is not found or any database error occurs.
     */
    public void deleteEventTimeSlot(String name, String date, String time) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            // Fetch the event
            String selectQuery = "SELECT " + COLUMN_TIMES + " FROM " + TABLE_EVENTS
                    + " WHERE " + COLUMN_NAME + " = ? AND " + COLUMN_DATE + " = ?";
            cursor = db.rawQuery(selectQuery, new String[]{name, date});

            if (cursor.moveToFirst()) {
                // Use getColumnIndexOrThrow for better error messages
                String timesJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMES));
                List<String> times = jsonToTimesList(timesJson);

                // Remove the specific time
                boolean removed = times.remove(time);
                if (!removed) {
                    throw new Exception("Time slot not found in the event.");
                }

                if (times.isEmpty()) {
                    // Delete the entire event
                    int rowsDeleted = db.delete(TABLE_EVENTS,
                            COLUMN_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                            new String[]{name, date});
                    if (rowsDeleted == 0) {
                        throw new Exception("Failed to delete the event.");
                    }
                } else {
                    // Update the event with remaining times
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TIMES, timesListToJson(times));

                    int rowsUpdated = db.update(TABLE_EVENTS, values,
                            COLUMN_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                            new String[]{name, date});
                    if (rowsUpdated == 0) {
                        throw new Exception("Failed to update the event.");
                    }
                }
            } else {
                throw new Exception("Event not found.");
            }
        } catch (IllegalArgumentException e) {
            Log.e("EventsRepository", "Column not found: " + COLUMN_TIMES, e);
            throw new Exception("Database schema error.", e);
        } catch (Exception e) {
            Log.e("EventsRepository", "Error deleting event time slot", e);
            throw e;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }


    /**
     * Checks if an event exists for a specific date and time.
     *
     * @param date The date of the event.
     * @param time The time slot to check.
     * @return True if an event exists at the specified date and time, false otherwise.
     */
    public boolean hasEventAtDateTime(String date, String time) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean hasEvent = false;

        try {
            cursor = db.query(TABLE_EVENTS,
                    new String[]{COLUMN_TIMES},
                    COLUMN_DATE + " = ?",
                    new String[]{date},
                    null,
                    null,
                    null);

            if (cursor.moveToFirst()) {
                do {
                    int timesIndex = cursor.getColumnIndex(COLUMN_TIMES);

                    if (timesIndex == -1) {
                        throw new IllegalArgumentException("Column '" + COLUMN_TIMES + "' not found in table " + TABLE_EVENTS);
                    }

                    String timesJson = cursor.getString(timesIndex);
                    List<String> times = jsonToTimesList(timesJson);
                    if (times.contains(time)) {
                        hasEvent = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("EventsRepository", "Error checking event at date and time: " + date + " " + time, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return hasEvent;
    }

    /**
     * Converts a list of times to a JSON string for storage.
     *
     * @param times The list of time strings.
     * @return A JSON string representing the list of times.
     */
    private String timesListToJson(List<String> times) {
        JSONArray jsonArray = new JSONArray();
        for (String time : times) {
            jsonArray.put(time);
        }
        return jsonArray.toString();
    }

    /**
     * Converts a JSON string back to a list of times.
     *
     * @param jsonString The JSON string representing the list of times.
     * @return A list of time strings.
     */
    private List<String> jsonToTimesList(String jsonString) {
        List<String> times = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                times.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e("EventsRepository", "Error parsing times JSON", e);
        }
        return times;
    }

    /**
     * Updates an existing event.
     *
     * @param event The event with updated information.
     */
    public void updateEvent(Events event) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, event.getName());
            values.put(COLUMN_DATE, event.getDate());
            values.put(COLUMN_TIMES, timesListToJson(event.getTime()));

            int rowsUpdated = db.update(TABLE_EVENTS,
                    values,
                    COLUMN_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                    new String[]{event.getName(), event.getDate()});

            if (rowsUpdated == 0) {
                Log.d("EventsRepository", "No events found to update with name: " + event.getName() + " and date: " + event.getDate());
            } else {
                Log.d("EventsRepository", "Event updated successfully with name: " + event.getName() + " and date: " + event.getDate());
            }
        } catch (Exception e) {
            Log.e("EventsRepository", "Error updating event", e);
        } finally {
            db.close();
        }
    }

    /**
     * Retrieves all events that occur on the specified date.
     *
     * @param date The date for which to retrieve events (e.g., "2024-04-27").
     * @return A list of Events objects occurring on the specified date.
     * @throws Exception If any database error occurs.
     */
    public List<Events> getEventsByDate(String date) throws Exception {
        List<Events> eventsList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            // Define the columns to retrieve
            String[] columns = {COLUMN_NAME, COLUMN_DATE, COLUMN_TIMES};

            // Define the selection criteria
            String selection = COLUMN_DATE + " = ?";
            String[] selectionArgs = {date};

            // Query the database
            cursor = db.query(
                    TABLE_EVENTS,   // The table to query
                    columns,        // The columns to return
                    selection,      // The columns for the WHERE clause
                    selectionArgs,  // The values for the WHERE clause
                    null,           // Group by
                    null,           // Having
                    null            // Order by
            );

            // Iterate through the results and construct Events objects
            if (cursor.moveToFirst()) {
                do {
                    // Retrieve column indices safely
                    int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME);
                    int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE);
                    int timesIndex = cursor.getColumnIndexOrThrow(COLUMN_TIMES);

                    String name = cursor.getString(nameIndex);
                    String eventDate = cursor.getString(dateIndex);
                    String timesJson = cursor.getString(timesIndex);

                    List<String> times = jsonToTimesList(timesJson);

                    Events event = new Events(name, eventDate, times);
                    eventsList.add(event);
                } while (cursor.moveToNext());
            }
        } catch (IllegalArgumentException e) {
            Log.e("EventsRepository", "Column not found while retrieving events by date.", e);
            throw new Exception("Database schema error.", e);
        } catch (Exception e) {
            Log.e("EventsRepository", "Error retrieving events by date: " + date, e);
            throw e;
        } finally {
            // Close the cursor and database to free resources
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return eventsList;
    }

}
