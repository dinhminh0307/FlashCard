package com.example.flashcard.repo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flashcard.models.Quizz;
import com.example.flashcard.models.Record;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordsRepository extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flashcard.db";
    private static final int DATABASE_VERSION = 2;

    // Table name
    private static final String TABLE_RECORDS = "Records";

    // Columns for the Records table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_QUIZZES = "quizzes"; // Quizzes stored as JSON string

    public RecordsRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECORDS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RECORDS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_QUIZZES + " TEXT NOT NULL" // Store quizzes as JSON string
                + ")";
        db.execSQL(CREATE_RECORDS_TABLE);
        Log.d("RecordsRepository", "Records table created.");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Check if the 'Records' table exists, create if missing
        db.execSQL("CREATE TABLE IF NOT EXISTS Records ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT NOT NULL, "
                + "quizzes TEXT NOT NULL" // Store quizzes as JSON string
                + ")");
        onCreate(db); // Calls onCreate to recreate the Records table
    }


    // Helper method to convert a list of Quizz objects to a JSON string
    private String quizzesToJson(List<Quizz> quizzes) {
        JSONArray jsonArray = new JSONArray();
        for (Quizz quizz : quizzes) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("total", quizz.getTotal());
                jsonObject.put("attempts", quizz.getAttempts());
                jsonObject.put("category", quizz.getCategory());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e("RecordsRepository", "Error converting quiz to JSON", e);
            }
        }
        return jsonArray.toString();
    }

    // Helper method to convert a JSON string to a list of Quizz objects
    private List<Quizz> jsonToQuizzes(String jsonString) {
        List<Quizz> quizzes = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Quizz quizz = new Quizz(
                        jsonObject.getInt("total"),
                        jsonObject.getInt("attempts"),
                        jsonObject.getString("category")
                );
                quizzes.add(quizz);
            }
        } catch (JSONException e) {
            Log.e("RecordsRepository", "Error parsing JSON to quizzes", e);
        }
        return quizzes;
    }

    // Insert a record with the date and a list of quizzes
    public void insertRecord(String date, List<Quizz> quizzes) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, date);
            values.put(COLUMN_QUIZZES, quizzesToJson(quizzes));

            long result = db.insert(TABLE_RECORDS, null, values);
            if (result == -1) {
                Log.e("RecordsRepository", "Failed to insert record");
            } else {
                Log.d("RecordsRepository", "Record inserted successfully with ID: " + result);
            }
        } catch (Exception e) {
            Log.e("RecordsRepository", "Error inserting record", e);
        } finally {
            db.close();
        }
    }

    // Retrieve all records from the database
    public List<Record> getAllRecords() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORDS, null);

            // Get column indexes, ensuring they are valid
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int quizzesIndex = cursor.getColumnIndex(COLUMN_QUIZZES);

            if (idIndex == -1 || dateIndex == -1 || quizzesIndex == -1) {
                throw new IllegalArgumentException("One or more columns not found in the table " + TABLE_RECORDS);
            }

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(idIndex);
                    String date = cursor.getString(dateIndex);
                    String quizzesJson = cursor.getString(quizzesIndex);

                    List<Quizz> quizzes = jsonToQuizzes(quizzesJson);
                    records.add(new Record(id, date, quizzes));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("RecordsRepository", "Error retrieving records", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return records;
    }


    // Clear all records from the table
    public void clearRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_RECORDS, null, null);
            Log.d("RecordsRepository", "All records cleared.");
        } catch (Exception e) {
            Log.e("RecordsRepository", "Error clearing records", e);
        } finally {
            db.close();
        }
    }

    public List<Quizz> getQuizzesByDate(String date) {
        List<Quizz> quizzes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Log the date to ensure it's being passed as expected
            Log.d("RecordsRepository", "Date for query: " + date);

            // Use parameterized query to avoid issues with direct String substitution
            cursor = db.rawQuery("SELECT quizzes FROM Records WHERE date = ?", new String[]{date});

            int quizzesIndex = cursor.getColumnIndex("quizzes");
            if (quizzesIndex == -1) {
                throw new IllegalArgumentException("Column 'quizzes' not found in the table Records");
            }

            if (cursor.moveToFirst()) {
                String quizzesJson = cursor.getString(quizzesIndex);
                quizzes = jsonToQuizzes(quizzesJson); // Convert JSON string to list of Quizz objects
            }
        } catch (Exception e) {
            Log.e("RecordsRepository", "Error retrieving quizzes by date" + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return quizzes;
    }


    public void deleteRecordByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Perform the deletion where the date matches
            int rowsDeleted = db.delete(TABLE_RECORDS, COLUMN_DATE + " = ?", new String[]{date});

            if (rowsDeleted == 0) {
                Log.d("RecordsRepository", "No records found with date: " + date);
            } else {
                Log.d("RecordsRepository", "Record(s) deleted successfully with date: " + date);
            }
        } catch (Exception e) {
            Log.e("RecordsRepository", "Error deleting record with date " + date, e);
        } finally {
            db.close();
        }
    }

    // Check if the Records table has any data
    // Check if a record exists for a specific date
    public boolean hasRecordForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean hasRecord = false;

        try {
            // Use a COUNT query to see if any rows match the given date
            String query = "SELECT COUNT(*) FROM " + TABLE_RECORDS + " WHERE " + COLUMN_DATE + " = ?";
            cursor = db.rawQuery(query, new String[]{date});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                hasRecord = (count > 0); // True if count is greater than 0
            }
        } catch (Exception e) {
            Log.e("RecordsRepository", "Error checking if record exists for date: " + date, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return hasRecord;
    }



}

