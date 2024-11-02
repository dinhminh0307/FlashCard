package com.example.flashcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.MainActivity;
import com.example.flashcard.R;
import com.example.flashcard.utils.Constant;

public class CardPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_card_page);

        // Retrieve the category name passed from MainActivity
        Intent intent = getIntent();
        String categoryName = intent.getStringExtra("category_name");

        // navigate to the pages by category
        navigateFlashCardByCategories(categoryName);

        // call the feature
        returnButton();
    }

    private void navigateFlashCardByCategories(String category) {
        switch (category) {
            case "MATH":
                fetchMathPages();
            default:
                break;
        }
    }

    private void fetchMathPages() {
        TextView title = findViewById(R.id.titleText);
        title.setText("Math Flashcard");
    }
    private void returnButton() {
        ImageView returnBtn = findViewById(R.id.backButton);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}