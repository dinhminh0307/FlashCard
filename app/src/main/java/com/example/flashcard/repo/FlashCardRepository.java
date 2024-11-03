package com.example.flashcard.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String COLUMN_ANSWER_TEXT = "answer_text"; // New answer column

    public FlashCardRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Math Questions Table
        String CREATE_MATH_TABLE = "CREATE TABLE " + TABLE_MATH + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                + COLUMN_ANSWER_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_MATH_TABLE);

        // Create Physics Questions Table
        String CREATE_PHYSICS_TABLE = "CREATE TABLE " + TABLE_PHYSICS + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                + COLUMN_ANSWER_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_PHYSICS_TABLE);

        // Create Computer Science Questions Table
        String CREATE_COMPUTER_SCIENCE_TABLE = "CREATE TABLE " + TABLE_COMPUTER_SCIENCE + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                + COLUMN_ANSWER_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_COMPUTER_SCIENCE_TABLE);

        // Create Language Questions Table
        String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + TABLE_LANGUAGE + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                + COLUMN_ANSWER_TEXT + " TEXT NOT NULL"
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

    // Insert question and answer into the specified category table
    public void insertQuestion(String tableName, String questionText, String answerText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_TEXT, questionText);
        values.put(COLUMN_ANSWER_TEXT, answerText); // Insert answer
        db.insert(tableName, null, values);
        db.close();
    }

    // Retrieve all questions and answers from a specified category table
    public List<Map<String, String>> getQuestionsAndAnswers(String tableName) {
        List<Map<String, String>> questionsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT question_text, answer_text FROM " + tableName;
            cursor = db.rawQuery(query, null);

            // Check if columns exist in cursor to prevent getColumnIndex from returning -1
            int questionIndex = cursor.getColumnIndex("question_text");
            int answerIndex = cursor.getColumnIndex("answer_text");

            // If either index is -1, the column does not exist in the result set
            if (questionIndex == -1 || answerIndex == -1) {
                throw new IllegalArgumentException("Columns 'question_text' or 'answer_text' not found in table " + tableName);
            }

            // Populate the list if columns are found
            if (cursor.moveToFirst()) {
                do {
                    String question = cursor.getString(questionIndex);
                    String answer = cursor.getString(answerIndex);

                    Map<String, String> questionAnswerMap = new HashMap<>();
                    questionAnswerMap.put("question", question);
                    questionAnswerMap.put("answer", answer);
                    questionsList.add(questionAnswerMap);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("FlashCardRepository", "Error retrieving data from table: " + tableName, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return questionsList;
    }


}
