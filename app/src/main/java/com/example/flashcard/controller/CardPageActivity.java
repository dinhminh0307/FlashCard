package com.example.flashcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcard.MainActivity;
import com.example.flashcard.R;
import com.example.flashcard.repo.FlashCardRepository;
import com.example.flashcard.utils.Constant;

import java.util.List;

public class CardPageActivity extends AppCompatActivity {
    private FlashCardRepository flashCardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_page);

        // Initialize the repository
        flashCardRepository = new FlashCardRepository(this);

        // Retrieve the category name passed from MainActivity
        Intent intent = getIntent();
        String categoryName = intent.getStringExtra("category_name");

        // Navigate to the pages by category
        navigateFlashCardByCategories(categoryName);

        // Set up return button functionality
        setReturnButton();
    }

    private void navigateFlashCardByCategories(String category) {
        // Make sure category is not null and matches the constants defined
        if (category == null) {
            Toast.makeText(this, "Category is null", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (category) {
            case Constant.MATH_CONST:
                fetchMathPages();
                break;
            case Constant.PHYSICS_CONST:
                fetchPhysicsPages();
                break;
            case Constant.COMPUTER_SCIENCE_CONST:
                fetchComputerSciencePages();
                break;
            case Constant.LANGUAGE_CONST:
                fetchLanguagePages();
                break;
            default:
                Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void fetchMathPages() {
        List<String> questions = flashCardRepository.getQuestionsList("math_questions"); // Ensure correct table name

        // Set title and display first question if available
        TextView title = findViewById(R.id.titleText);
        TextView content = findViewById(R.id.content);
        title.setText("Math Flashcard");

        if (questions != null && !questions.isEmpty()) {
            content.setText(questions.get(0));
        } else {
            content.setText("No questions available");
            Toast.makeText(this, "No questions found for Math category", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPhysicsPages() {
        List<String> questions = flashCardRepository.getQuestionsList("physics_questions");

        TextView title = findViewById(R.id.titleText);
        TextView content = findViewById(R.id.content);
        title.setText("Physics Flashcard");

        if (questions != null && !questions.isEmpty()) {
            content.setText(questions.get(0));
        } else {
            content.setText("No questions available");
            Toast.makeText(this, "No questions found for Physics category", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchComputerSciencePages() {
        List<String> questions = flashCardRepository.getQuestionsList("computer_science_questions");

        TextView title = findViewById(R.id.titleText);
        TextView content = findViewById(R.id.content);
        title.setText("Computer Science");

        if (questions != null && !questions.isEmpty()) {
            content.setText(questions.get(0));
        } else {
            content.setText("No questions available");
            Toast.makeText(this, "No questions found for Computer Science category", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchLanguagePages() {
        List<String> questions = flashCardRepository.getQuestionsList("language_questions");

        TextView title = findViewById(R.id.titleText);
        TextView content = findViewById(R.id.content);
        title.setText("Language Flashcard");

        if (questions != null && !questions.isEmpty()) {
            content.setText(questions.get(0));
        } else {
            content.setText("No questions available");
            Toast.makeText(this, "No questions found for Language category", Toast.LENGTH_SHORT).show();
        }
    }

    private void setReturnButton() {
        ImageView returnBtn = findViewById(R.id.backButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(CardPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
