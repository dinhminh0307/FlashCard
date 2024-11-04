package com.example.flashcard;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcard.controller.CardPageActivity;
import com.example.flashcard.exceptions.DuplicateQuestionException;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.repo.FlashCardRepository;
import com.example.flashcard.services.FlashCardServices;
import com.example.flashcard.utils.Constant;

public class MainActivity extends AppCompatActivity {
    private FlashCardServices flashCardServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the FlashCardRepository
        flashCardServices = new FlashCardServices(this);

        // Open the database to ensure tables are created
//        SQLiteDatabase db = flashCardRepository.getWritableDatabase();
        //initializeTables();

        // Set up the function to navigate to the Math category
        navigateToMath(Constant.MATH_CONST);
        navigateToComputerScience(Constant.COMPUTER_SCIENCE_CONST);
        navigateToLanguage(Constant.LANGUAGE_CONST);
        navigateToPhysics(Constant.PHYSICS_CONST);
    }

//    private void initializeTables() {
//        // Create sample FlashCard objects
//        FlashCard mathFlashCard = new FlashCard(null, "What is 2 + 2?", "4");
//        FlashCard physicsFlashCard = new FlashCard(null, "What is the speed of light?", "299,792,458 meters per second");
//        FlashCard csFlashCard = new FlashCard(null, "What is a binary tree?", "A tree data structure in which each node has at most two children.");
//        FlashCard languageFlashCard = new FlashCard(null, "What is the synonym of 'happy'?", "Joyful");
//
//        // Insert the sample FlashCards into their respective tables
//        try {
//            flashCardRepository.insertQuestion("math_questions", mathFlashCard);
//            flashCardRepository.insertQuestion("physics_questions", physicsFlashCard);
//            flashCardRepository.insertQuestion("computer_science_questions", csFlashCard);
//            flashCardRepository.insertQuestion("language_questions", languageFlashCard);
//        } catch (DuplicateQuestionException e) {
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            Toast.makeText(this, "An error occurred while adding the flashcard", Toast.LENGTH_LONG).show();
//        }
//
//
//        // Show a toast message indicating that tables have been initialized and sample data has been added
//        Toast.makeText(this, "Tables created and sample data added", Toast.LENGTH_SHORT).show();
//    }



    private void navigateToMath(String category) {
        // Get the math grid layout
        LinearLayout math = findViewById(R.id.mathGrid);
        math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message for the Math category click
                Toast.makeText(getApplicationContext(), "Mathematics clicked", Toast.LENGTH_SHORT).show();

                // Navigate to CardPageActivity, passing the category as an extra
                Intent intent = new Intent(MainActivity.this, CardPageActivity.class);
                intent.putExtra("category_name", category);
                startActivity(intent);
            }
        });
    }

    private void navigateToPhysics(String category) {
        // Get the math grid layout
        LinearLayout physics = findViewById(R.id.physicsGrid);
        physics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message for the Math category click
                Toast.makeText(getApplicationContext(), "Physics clicked", Toast.LENGTH_SHORT).show();

                // Navigate to CardPageActivity, passing the category as an extra
                Intent intent = new Intent(MainActivity.this, CardPageActivity.class);
                intent.putExtra("category_name", category);
                startActivity(intent);
            }
        });
    }

    private void navigateToComputerScience(String category) {
        // Get the math grid layout
        LinearLayout computerScience = findViewById(R.id.csGrid);
        computerScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message for the Math category click
                Toast.makeText(getApplicationContext(), "CS clicked", Toast.LENGTH_SHORT).show();

                // Navigate to CardPageActivity, passing the category as an extra
                Intent intent = new Intent(MainActivity.this, CardPageActivity.class);
                intent.putExtra("category_name", category);
                startActivity(intent);
            }
        });
    }

    private void navigateToLanguage(String category) {
        // Get the math grid layout
        LinearLayout language = findViewById(R.id.langGrid);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message for the Math category click
                Toast.makeText(getApplicationContext(), "Language clicked", Toast.LENGTH_SHORT).show();

                // Navigate to CardPageActivity, passing the category as an extra
                Intent intent = new Intent(MainActivity.this, CardPageActivity.class);
                intent.putExtra("category_name", category);
                startActivity(intent);
            }
        });
    }
}
