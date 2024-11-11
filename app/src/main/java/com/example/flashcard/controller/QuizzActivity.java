package com.example.flashcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.R;
import com.example.flashcard.exceptions.NoResourceFound;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.services.QuizzServices;

import java.util.ArrayList;
import java.util.List;

public class QuizzActivity extends AppCompatActivity {
    private String categoryName = "";


    private int questionCursor = 0;

    QuizzServices quizzServices;

    private List<String> listOfQuestion = new ArrayList<>();

    private int attemptCount = 0;
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

        quizzServices = new QuizzServices(this);

        quizzServices.setQuizz(categoryName);
        listOfQuestion = quizzServices.getQuestions();

        fetchTheQuestion();
        onNextBtn();
        onPreviousButton();
        setReturnButton();

        onSubmittButton();
        // check the state of the quizz

    }

    private void fetchTheQuestion() {
        setTheSubmitState();
        TextView ques = findViewById(R.id.questionId);
        ques.setText(listOfQuestion.get(questionCursor));
    }

    private void setTheSubmitState() {
        Button submitButton = findViewById(R.id.submitBtn);
        Button nextButton = findViewById(R.id.nextBtn);
        if(questionCursor == listOfQuestion.size() - 1 && submitButton.getVisibility() == View.GONE) {
            // end of the questions and display the submit button
            submitButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        } else {
            submitButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }
    private void moveToNextQuestion() {
        if(questionCursor < listOfQuestion.size()) {
            questionCursor++;
        }
        fetchTheQuestion();
    }

    private void onPreviousButton() {
        Button prev = findViewById(R.id.prevBtn);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionCursor > 0) {
                    questionCursor--;
                }
                fetchTheQuestion();
            }
        });
    }

    private void setReturnButton() {
        ImageView returnBtn = findViewById(R.id.exitButton);
        returnBtn.setOnClickListener(v -> finish());
    }

    private void onSubmittButton() {
        Button submitBtn = findViewById((R.id.submitBtn));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ansResult = false;
                try {
                    EditText ans = findViewById(R.id.editAnswer);
                    TextView ques = findViewById(R.id.questionId);

                    FlashCard currAns = new FlashCard();
                    currAns.setQuestions(ques.getText().toString());
                    currAns.setAnswers(ans.getText().toString());

                    ansResult = quizzServices.verifyAnswer(currAns);
                } catch (NoResourceFound e) {
                    Toast.makeText(QuizzActivity.this, "Question not found", Toast.LENGTH_LONG).show();
                }

                if (ansResult) {
                    // move the cursor to next question
                    quizzServices.submit(categoryName, attemptCount);
                    finish();
                    // Display correct answer feedback
                } else if (!ansResult) {
                    // Display incorrect answer feedback
                    attemptCount++;
                    Toast.makeText(QuizzActivity.this, "Incorrect", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void onNextBtn() {
        Button nextButton = findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ansResult = false;
                try {
                    EditText ans = findViewById(R.id.editAnswer);
                    TextView ques = findViewById(R.id.questionId);

                    FlashCard currAns = new FlashCard();
                    currAns.setQuestions(ques.getText().toString());
                    currAns.setAnswers(ans.getText().toString());

                    ansResult = quizzServices.verifyAnswer(currAns);
                } catch (NoResourceFound e) {
                    Toast.makeText(QuizzActivity.this, "Question not found", Toast.LENGTH_LONG).show();
                }

                if (ansResult) {
                    // move the cursor to next question
                    moveToNextQuestion();
                    // Display correct answer feedback
                } else if (!ansResult) {
                    // Display incorrect answer feedback
                    attemptCount++;
                    Toast.makeText(QuizzActivity.this, "Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}