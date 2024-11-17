package com.example.flashcard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.flashcard.controller.CardPageActivity;
import com.example.flashcard.controller.ProfilePageActivity;
import com.example.flashcard.controller.QuizzActivity;
import com.example.flashcard.controller.TimeTableActivity;
import com.example.flashcard.dialogs.OptionDialogFragment;
import com.example.flashcard.exceptions.DatabaseEmptyException;
import com.example.flashcard.services.FlashCardServices;
import com.example.flashcard.services.RecordServices;
import com.example.flashcard.threads.ThreadTasks;
import com.example.flashcard.utils.Constant;

public class MainActivity extends AppCompatActivity implements OptionDialogFragment.OptionDialogListener {
    private FlashCardServices flashCardServices;
    private String selectedCategory;

    private RecordServices recordServices;

    private ThreadTasks threadTasks;

    private static final int REQUEST_POST_NOTIFICATIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNotificationPermission();
        flashCardServices = new FlashCardServices(this);
        flashCardServices.initDB();
        threadTasks = new ThreadTasks(this);
        threadTasks.runThread();
        // Set up the function to navigate to each category
        setupCategoryListeners();
        onProfileClicked();
        onScheduleClicked();
    }

    private void requestNotificationPermission() {
        // Check if the device is running Android 13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the POST_NOTIFICATIONS permission is already granted
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {

                // If permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.POST_NOTIFICATIONS"},
                        REQUEST_POST_NOTIFICATIONS);
            } else {
                // Permission already granted
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Permission is automatically granted on older Android versions
            Toast.makeText(this, "Notifications are enabled by default on your device.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            // If request is cancelled, the result arrays are empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();

                // Optionally, inform the user why the permission is needed and prompt again or disable notifications
                // For example:
                // showPermissionDeniedDialog();
            }
        }
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
        try {
            // Handle Quiz option - Start the quiz activity if you have one
            if(flashCardServices.checkTableEmpty(selectedCategory)) {
                throw new DatabaseEmptyException("The table is empty, please add more data to start the quizz");
            }
            Toast.makeText(this, "Quiz selected for " + selectedCategory, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, QuizzActivity.class);
            intent.putExtra("category_name", selectedCategory);
            startActivity(intent);
        } catch (DatabaseEmptyException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

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
        flashCardServices.deleteAllQuestions(selectedCategory);
        Toast.makeText(this, "Delete selected for " + selectedCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelSelected() {
        // Simply dismiss the dialog without any further action
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void onProfileClicked() {
        ImageView profile = findViewById(R.id.profile_icon);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfilePageActivity.class);

                startActivity(intent);
            }
        });
    }

    private void onScheduleClicked() {
        Button timeTableBtn = findViewById(R.id.button);

        timeTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimeTableActivity.class);

                startActivity(intent);
            }
        });
    }
}
