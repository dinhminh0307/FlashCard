package com.example.flashcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.R;
import com.example.flashcard.services.QuizzServices;

import java.util.ArrayList;
import java.util.List;

public class QuizzActivity extends AppCompatActivity {
    private String categoryName = "";


    private int questionCursor = 0;

    private List<String> listOfQuestion = new ArrayList<>();

    public QuizzActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quizz);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category_name");

        if (categoryName == null) {
            throw new IllegalArgumentException("Category name is null");
        }

        QuizzServices quizzServices = new QuizzServices(this);

//        //get quizz form the database

//        Log.println(Log.ASSERT, "Print", categoryName);
        quizzServices.setQuizz(categoryName);
        listOfQuestion = quizzServices.getQuestions();

        fetchTheQuestion();
        setReturnButton();
    }

    private void fetchTheQuestion() {
        TextView ques = findViewById(R.id.questionId);
        ques.setText(listOfQuestion.get(questionCursor));
    }

    private void setReturnButton() {
        ImageView returnBtn = findViewById(R.id.exitButton);
        returnBtn.setOnClickListener(v -> finish());
    }
}