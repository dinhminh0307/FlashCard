package com.example.flashcard.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FlashCardRepository extends SQLiteOpenHelper {
    // Database Name and Version
    private static final String DATABASE_NAME = "flashcard.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names for each category
    private static final String TABLE_MATH = "math_questions";
    private static final String TABLE_PHYSICS = "physics_questions";
    private static final String TABLE_COMPUTER_SCIENCE = "computer_science_questions";
    private static final String TABLE_LANGUAGE = "language_questions";

    // Columns for questions tables
    private static final String COLUMN_QUESTION_ID = "question_id";
    private static final String COLUMN_QUESTION_TEXT = "question_text";

    public FlashCardRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Math Questions Table
        String CREATE_MATH_TABLE = "CREATE TABLE " + TABLE_MATH + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_MATH_TABLE);

        // Create Physics Questions Table
        String CREATE_PHYSICS_TABLE = "CREATE TABLE " + TABLE_PHYSICS + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_PHYSICS_TABLE);

        // Create Computer Science Questions Table
        String CREATE_COMPUTER_SCIENCE_TABLE = "CREATE TABLE " + TABLE_COMPUTER_SCIENCE + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_COMPUTER_SCIENCE_TABLE);

        // Create Language Questions Table
        String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + TABLE_LANGUAGE + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_LANGUAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHYSICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPUTER_SCIENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
        onCreate(db);
    }

    // Insert question into the specified category table
    public void insertQuestion(String tableName, String questionText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_TEXT, questionText);
        db.insert(tableName, null, values);
        db.close();
    }

    // Retrieve all questions from a specified category table
    public List<String> getQuestionsList(String tableName) {
        List<String> questionsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Check if COLUMN_QUESTION_TEXT is a valid column in the table
        String query = "SELECT " + COLUMN_QUESTION_TEXT + " FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);

        // Verify that the column exists in the Cursor
        int columnIndex = cursor.getColumnIndex(COLUMN_QUESTION_TEXT);
        if (columnIndex == -1) {
            // Log an error if the column is not found
            throw new IllegalArgumentException("Column '" + COLUMN_QUESTION_TEXT + "' does not exist in table " + tableName);
        }

        // Iterate over the Cursor and add each question to the list
        if (cursor.moveToFirst()) {
            do {
                String question = cursor.getString(columnIndex);
                questionsList.add(question);
            } while (cursor.moveToNext());
        }

        // Close the Cursor and database
        cursor.close();
        db.close();

        return questionsList;
    }
}
