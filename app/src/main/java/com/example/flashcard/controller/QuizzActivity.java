package com.example.flashcard.controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.R;

public class QuizzActivity extends AppCompatActivity {
    private String categoryName = "";

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
    }
}