package com.example.flashcard.services;

import android.content.Context;
import android.util.Log;

import com.example.flashcard.models.Quizz;
import com.example.flashcard.repo.RecordsRepository;
import com.example.flashcard.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordServices {
    private RecordsRepository recordsRepository;
    private List<Quizz> quizzList;
    private DateUtils dateUtils;

    // Constructor to initialize RecordsRepository and DateUtils
    public RecordServices(Context context) {
        this.recordsRepository = new RecordsRepository(context);
        this.quizzList = new ArrayList<>();  // Initialize quizzList to an empty list
        dateUtils = new DateUtils();
    }

    public void initializeDatabase() {
        recordsRepository.getWritableDatabase(); // This will create the database if it doesn't exist
    }

    private void fetchQuizzList() {
        // Fetch the list from the database only if it is currently empty
        if (quizzList.isEmpty()) {
            quizzList = recordsRepository.getQuizzesByDate(dateUtils.getDate());
        }
    }

    public void savedToDatabase(Quizz quizz) {
        fetchQuizzList();  // Populate quizzList if it's empty
        String date = dateUtils.getDate();
        if(recordsRepository.hasRecordForDate(date)) {
            recordsRepository.deleteRecordByDate(date); // Clear existing record for the date
        }
        quizzList.add(quizz);  // Add the new quiz to the list
        recordsRepository.insertRecord(date, quizzList);  // Save updated list to the database
    }
}
