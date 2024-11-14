package com.example.flashcard.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flashcard.models.Events;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class EventsRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flashcard.db";
    private static final int DATABASE_VERSION = 1;

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
                    int idIndex = cursor.getColumnIndex(COLUMN_ID);
                    int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                    int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
                    int timesIndex = cursor.getColumnIndex(COLUMN_TIMES);

                    if (idIndex == -1 || nameIndex == -1 || dateIndex == -1 || timesIndex == -1) {
                        throw new IllegalArgumentException("One or more columns not found in the table " + TABLE_EVENTS);
                    }

                    int id = cursor.getInt(idIndex);
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
     * Retrieves events for a specific date.
     *
     * @param date The date to filter events by.
     * @return A list of Events occurring on the specified date.
     */
    public List<Events> getEventsByDate(String date) {
        List<Events> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_EVENTS,
                    null,
                    COLUMN_DATE + " = ?",
                    new String[]{date},
                    null,
                    null,
                    null);

            if (cursor.moveToFirst()) {
                do {
                    // Get column indexes safely
                    int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                    int timesIndex = cursor.getColumnIndex(COLUMN_TIMES);

                    if (nameIndex == -1 || timesIndex == -1) {
                        throw new IllegalArgumentException("One or more columns not found in the table " + TABLE_EVENTS);
                    }

                    String name = cursor.getString(nameIndex);
                    String timesJson = cursor.getString(timesIndex);

                    List<String> times = jsonToTimesList(timesJson);
                    Events event = new Events(name, date, times);
                    eventsList.add(event);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("EventsRepository", "Error retrieving events by date: " + date, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return eventsList;
    }

    /**
     * Deletes an event by its name and date.
     *
     * @param name The name of the event.
     * @param date The date of the event.
     */
    public void deleteEvent(String name, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(TABLE_EVENTS,
                    COLUMN_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                    new String[]{name, date});

            if (rowsDeleted == 0) {
                Log.d("EventsRepository", "No events found with name: " + name + " and date: " + date);
                throw new NoSuchFieldException("No event found");
            } else {
                Log.d("EventsRepository", "Event deleted successfully with name: " + name + " and date: " + date);
            }
        } catch (Exception e) {
            Log.e("EventsRepository", "Error deleting event with name " + name + " and date " + date, e);
        } finally {
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
                        throw new IllegalArgumentException("Column '" + COLUMN_TIMES + "' not found in the table " + TABLE_EVENTS);
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
}
