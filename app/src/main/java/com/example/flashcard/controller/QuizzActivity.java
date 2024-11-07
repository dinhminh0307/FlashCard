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

//        //get quizz form the database

//        Log.println(Log.ASSERT, "Print", categoryName);
        quizzServices.setQuizz(categoryName);
        listOfQuestion = quizzServices.getQuestions();

        fetchTheQuestion();
        onNextBtn();
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
                    // Display correct answer feedback
                    Toast.makeText(QuizzActivity.this, "Correct", Toast.LENGTH_LONG).show();
                } else if (!ansResult) {
                    // Display incorrect answer feedback
                    Toast.makeText(QuizzActivity.this, "Incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}