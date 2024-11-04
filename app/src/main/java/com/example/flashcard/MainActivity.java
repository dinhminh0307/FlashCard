package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcard.controller.CardPageActivity;
import com.example.flashcard.controller.QuizzActivity;
import com.example.flashcard.dialogs.OptionDialogFragment;
import com.example.flashcard.services.FlashCardServices;
import com.example.flashcard.utils.Constant;

public class MainActivity extends AppCompatActivity implements OptionDialogFragment.OptionDialogListener {
    private FlashCardServices flashCardServices;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashCardServices = new FlashCardServices(this);

        // Set up the function to navigate to each category
        setupCategoryListeners();
    }

    /**
     * des: this function used to parse the view id to be selected
     */
    private void setupCategoryListeners() {
        setupCategoryClickListener(R.id.mathGrid, Constant.MATH_CONST);
        setupCategoryClickListener(R.id.physicsGrid, Constant.PHYSICS_CONST);
        setupCategoryClickListener(R.id.csGrid, Constant.COMPUTER_SCIENCE_CONST);
        setupCategoryClickListener(R.id.langGrid, Constant.LANGUAGE_CONST);
    }

    /**
     * This function used to add the modal before switching scene, every function selected will be implemented
     * below since this activity implement the interface from fragment
     * @param viewId
     * @param category
     */
    private void setupCategoryClickListener(int viewId, String category) {
        LinearLayout categoryLayout = findViewById(viewId);
        categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = category; // change the global var so that the implemented function use as param
                OptionDialogFragment dialog = new OptionDialogFragment(MainActivity.this);
                dialog.show(getSupportFragmentManager(), "OptionDialogFragment");
            }
        });
    }

    @Override
    public void onQuizSelected() {
        // Handle Quiz option - Start the quiz activity if you have one
        Toast.makeText(this, "Quiz selected for " + selectedCategory, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, QuizzActivity.class);
        intent.putExtra("category_name", selectedCategory);
        startActivity(intent);
    }

    @Override
    public void onViewSelected() {
        // Navigate to CardPageActivity for the selected category
        Intent intent = new Intent(MainActivity.this, CardPageActivity.class);
        intent.putExtra("category_name", selectedCategory);
        startActivity(intent);
    }

    @Override
    public void onDeleteSelected() {
        // Handle Delete option (delete data for the selected category if necessary)
        Toast.makeText(this, "Delete selected for " + selectedCategory, Toast.LENGTH_SHORT).show();
        // Implement delete functionality if needed
    }

    @Override
    public void onCancelSelected() {
        // Simply dismiss the dialog without any further action
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
    }
}
