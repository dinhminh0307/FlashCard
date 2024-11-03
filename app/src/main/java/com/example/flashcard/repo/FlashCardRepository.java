package com.example.flashcard.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flashcard.exceptions.DuplicateQuestionException;
import com.example.flashcard.models.FlashCard;

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
    public void insertQuestion(String tableName, FlashCard flashCard) throws DuplicateQuestionException {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the question already exists in the database
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + COLUMN_QUESTION_TEXT + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{flashCard.getQuestions()});

        try {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                // Question already exists
                throw new DuplicateQuestionException("Question already exists in the database: " + flashCard.getQuestions());
            }

            // If no duplicate found, insert the question
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUESTION_TEXT, flashCard.getQuestions());
            values.put(COLUMN_ANSWER_TEXT, flashCard.getAnswers());

            long result = db.insert(tableName, null, values);
            if (result == -1) {
                throw new Exception("Failed to insert question into table: " + tableName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    // Retrieve all questions and answers from a specified category table
    public List<FlashCard> getQuestionsAndAnswers(String tableName) {
        List<FlashCard> flashCards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT question_id, question_text, answer_text FROM " + tableName;
            cursor = db.rawQuery(query, null);

            int idIndex = cursor.getColumnIndex("question_id");
            int questionIndex = cursor.getColumnIndex("question_text");
            int answerIndex = cursor.getColumnIndex("answer_text");

            // If any index is -1, the column does not exist in the result set
            if (idIndex == -1 || questionIndex == -1 || answerIndex == -1) {
                throw new IllegalArgumentException("One or more columns not found in table " + tableName);
            }

            // Populate the list if columns are found
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(idIndex);
                    String question = cursor.getString(questionIndex);
                    String answer = cursor.getString(answerIndex);

                    // Create FlashCard object with id, question, and answer
                    FlashCard flashCard = new FlashCard(id, question, answer);
                    flashCards.add(flashCard);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("FlashCardRepository", "Error retrieving data from table: " + tableName, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return flashCards;
    }



}
