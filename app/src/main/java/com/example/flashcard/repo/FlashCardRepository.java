package com.example.flashcard.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flashcard.exceptions.DuplicateQuestionException;
import com.example.flashcard.exceptions.NoResourceFound;
import com.example.flashcard.models.FlashCard;

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
    private static final String COLUMN_ANSWER_TEXT = "answer_text";

    public FlashCardRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MATH_TABLE = "CREATE TABLE " + TABLE_MATH + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                + COLUMN_ANSWER_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_MATH_TABLE);

        String CREATE_PHYSICS_TABLE = "CREATE TABLE " + TABLE_PHYSICS + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                + COLUMN_ANSWER_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_PHYSICS_TABLE);

        String CREATE_COMPUTER_SCIENCE_TABLE = "CREATE TABLE " + TABLE_COMPUTER_SCIENCE + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                + COLUMN_ANSWER_TEXT + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_COMPUTER_SCIENCE_TABLE);

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

    // Insert question and answer
    public void insertQuestion(String tableName, FlashCard flashCard) throws DuplicateQuestionException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + COLUMN_QUESTION_TEXT + " = ?";
            cursor = db.rawQuery(query, new String[]{flashCard.getQuestions()});

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                throw new DuplicateQuestionException("Question already exists in the database: " + flashCard.getQuestions());
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_QUESTION_TEXT, flashCard.getQuestions());
            values.put(COLUMN_ANSWER_TEXT, flashCard.getAnswers());

            long result = db.insert(tableName, null, values);
            if (result == -1) {
                throw new RuntimeException("Failed to insert question into table: " + tableName);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // Update only the question text for a specific flashcard
    public void updateQuestionText(String tableName, String questionId, FlashCard flashCard) throws DuplicateQuestionException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            // Check if the updated question already exists in the table (excluding the current question ID)
            String duplicateQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE " + COLUMN_QUESTION_TEXT + " = ? AND " + COLUMN_QUESTION_ID + " != ?";
            cursor = db.rawQuery(duplicateQuery, new String[]{flashCard.getQuestions(), questionId});

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                // Throw the custom exception if a duplicate question is found
                throw new DuplicateQuestionException("Question already exists in the database: " + flashCard.getQuestions());
            }

            // If no duplicate is found, proceed with updating the question text
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUESTION_TEXT, flashCard.getQuestions());

            int rowsUpdated = db.update(tableName, values, COLUMN_QUESTION_ID + " = ?", new String[]{questionId});
            if (rowsUpdated == 0) {
                Log.e("FlashCardRepository", "No rows updated, question with ID " + questionId + " not found.");
            } else {
                Log.d("FlashCardRepository", "Question text updated successfully. Rows affected: " + rowsUpdated);
            }
        } catch (DuplicateQuestionException e) {
            // Re-throw DuplicateQuestionException so it can be handled by the caller
            throw e;
        } catch (Exception e) {
            Log.e("FlashCardRepository", "Error updating question text", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }


    // Update only the answer text for a specific flashcard
    public void updateAnswerText(String tableName, String questionId, FlashCard flashCard) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ANSWER_TEXT, flashCard.getAnswers());

            int rowsUpdated = db.update(tableName, values, COLUMN_QUESTION_ID + " = ?", new String[]{questionId});
            if (rowsUpdated == 0) {
                Log.e("FlashCardRepository", "No rows updated, answer with ID " + questionId + " not found.");
            } else {
                Log.d("FlashCardRepository", "Answer text updated successfully. Rows affected: " + rowsUpdated);
            }
        } catch (Exception e) {
            Log.e("FlashCardRepository", "Error updating answer text", e);
        } finally {
            db.close();
        }
    }

    // Retrieve all questions and answers
    public List<FlashCard> getQuestionsAndAnswers(String tableName) {
        List<FlashCard> flashCards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT question_id, question_text, answer_text FROM " + tableName;
            cursor = db.rawQuery(query, null);

            int idIndex = cursor.getColumnIndex(COLUMN_QUESTION_ID);
            int questionIndex = cursor.getColumnIndex(COLUMN_QUESTION_TEXT);
            int answerIndex = cursor.getColumnIndex(COLUMN_ANSWER_TEXT);

            if (idIndex == -1 || questionIndex == -1 || answerIndex == -1) {
                throw new IllegalArgumentException("One or more columns not found in table " + tableName);
            }

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(idIndex);
                    String question = cursor.getString(questionIndex);
                    String answer = cursor.getString(answerIndex);

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


    // Delete a question by its ID
    public void deleteQuestionById(String tableName, String questionId) throws NoResourceFound {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            int rowsDeleted = db.delete(tableName, COLUMN_QUESTION_ID + " = ?", new String[]{questionId});

            if (rowsDeleted == 0) {
                Log.e("FlashCardRepository", "No rows deleted, question with ID " + questionId + " not found.");
                throw new NoResourceFound("No ID found");
            } else {
                Log.d("FlashCardRepository", "Question deleted successfully. Rows affected: " + rowsDeleted);
            }
        } catch (Exception e) {
            Log.e("FlashCardRepository", "Error deleting question with ID " + questionId, e);
        } finally {
            db.close();
        }
    }

    // Clear all records in a specific table
    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            int rowsDeleted = db.delete(tableName, null, null); // Delete all rows without a WHERE clause

            if (rowsDeleted == 0) {
                Log.d("FlashCardRepository", "No records found in table " + tableName + " to delete.");
            } else {
                Log.d("FlashCardRepository", "All records cleared from table " + tableName + ". Rows affected: " + rowsDeleted);
            }
        } catch (Exception e) {
            Log.e("FlashCardRepository", "Error clearing records from table " + tableName, e);
        } finally {
            db.close();
        }
    }

}
