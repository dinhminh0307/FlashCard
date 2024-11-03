package com.example.flashcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcard.MainActivity;
import com.example.flashcard.R;
import com.example.flashcard.dialogs.FormDialogFragment;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.repo.FlashCardRepository;
import com.example.flashcard.utils.Constant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

public class CardPageActivity extends AppCompatActivity {
    private FlashCardRepository flashCardRepository;
    private LinearLayout answerLayout;
    private boolean isAnswerVisible = false;

    // Keys for accessing the map entries
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWER = "answer";

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

        // add question form
        addQuestion(categoryName);

        // Initialize answerLayout
        answerLayout = findViewById(R.id.answerLayout);

        // Set up the expand/collapse button
        MaterialButton expandButton = findViewById(R.id.roundedSquareButton);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAnswerVisibility();
            }
        });
    }

    private void addQuestion(String tableName) {
        FloatingActionButton addInButton = findViewById(R.id.fabAdd);
        addInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormDialogFragment modal = new FormDialogFragment(tableName);
                modal.show(getSupportFragmentManager(), "FormDialogFragment");
            }
        });
    }

    // Toggle the visibility of the answer layout
    private void toggleAnswerVisibility() {
        answerLayout.setVisibility(isAnswerVisible ? View.GONE : View.VISIBLE);
        isAnswerVisible = !isAnswerVisible;
    }

    private void navigateFlashCardByCategories(String category) {
        if (category == null) {
            Toast.makeText(this, "Category is null", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (category) {
            case Constant.MATH_CONST:
                fetchFlashCardPages("math_questions", "Math Flashcard");
                break;
            case Constant.PHYSICS_CONST:
                fetchFlashCardPages("physics_questions", "Physics Flashcard");
                break;
            case Constant.COMPUTER_SCIENCE_CONST:
                fetchFlashCardPages("computer_science_questions", "Computer Science Flashcard");
                break;
            case Constant.LANGUAGE_CONST:
                fetchFlashCardPages("language_questions", "Language Flashcard");
                break;
            default:
                Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Generic method to fetch pages for a category
    private void fetchFlashCardPages(String tableName, String titleText) {

        List<FlashCard> questions = flashCardRepository.getQuestionsAndAnswers(tableName);

        TextView title = findViewById(R.id.titleText);
        TextView content = findViewById(R.id.content);

        TextView answer = findViewById(R.id.answerText);

        title.setText(titleText);


        if (questions != null && !questions.isEmpty()) {
            String q = questions.get(0).getQuestions();
            String a = questions.get(0).getQuestions();

            content.setText(q != null ? q : "Question not available");
            answer.setText(a != null ? a : "Answer not available");
        } else {
            content.setText("No questions available");
            answer.setText("");
            Toast.makeText(this, "No questions found for " + titleText, Toast.LENGTH_SHORT).show();
        }
    }

    private void setReturnButton() {
        ImageView returnBtn = findViewById(R.id.backButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // End this activity to go back to the previous screen
            }
        });
    }
}
