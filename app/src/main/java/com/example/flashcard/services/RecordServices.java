package com.example.flashcard.services;

import android.content.Context;
import android.util.Log;

import com.example.flashcard.models.Quizz;
import com.example.flashcard.repo.RecordsRepository;
import com.example.flashcard.utils.DateUtils;
import com.example.flashcard.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecordServices {
    private RecordsRepository recordsRepository;
    private HashMap<String, Quizz> quizzList;
    private DateUtils dateUtils;
    private Util util;

    // Constructor to initialize RecordsRepository and DateUtils
    public RecordServices(Context context) {
        this.recordsRepository = new RecordsRepository(context);
        this.quizzList = new HashMap<>();  // Initialize quizzList to an empty HashMap
        this.dateUtils = new DateUtils();
        this.util = new Util();
    }

    // Initializes the database if it doesn’t exist
    public void initializeDatabase() {
        recordsRepository.getWritableDatabase(); // Creates the database if it doesn't exist
    }

    // Fetches and populates the quizzList only if it’s empty
    private void fetchQuizzList() {
        if (quizzList.isEmpty()) {
            try {
                List<Quizz> container = recordsRepository.getQuizzesByDate(dateUtils.getDate());
                quizzList = util.convertListQuizzToHashMap(container);
            } catch (Exception e) {
                Log.e("RecordServices", "Error fetching quizzes from database", e);
            }
        }
    }

    // Saves or updates a Quizz in the database, consolidating by category
    public void saveToDatabase(Quizz quizz) {
        fetchQuizzList();  // Populate quizzList if it's empty

        String category = quizz.getCategory();
        Quizz foundByCategory = quizzList.get(category);

        if (foundByCategory != null) {
            // Consolidate attempts and total for the same category
            foundByCategory.setAttempts(foundByCategory.getAttempts() + quizz.getAttempts());
            foundByCategory.setTotal(foundByCategory.getTotal() + quizz.getTotal());
            quizzList.put(category, foundByCategory); // Update in HashMap
        } else {
            quizzList.put(category, quizz); // Add new category entry if it doesn’t exist
        }

        String date = dateUtils.getDate();
        try {
            // Check if there’s already a record for this date and clear it if necessary
            if (recordsRepository.hasRecordForDate(date)) {
                recordsRepository.deleteRecordByDate(date);
            }
            // Save the updated list to the database
            List<Quizz> result = util.convertHashMapToListQuizz(quizzList);
            recordsRepository.insertRecord(date, result);
        } catch (Exception e) {
            Log.e("RecordServices", "Error saving quizzes to database", e);
        }
    }
}
