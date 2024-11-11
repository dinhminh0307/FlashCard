package com.example.flashcard.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.flashcard.R;
import com.example.flashcard.models.Quizz;
import com.example.flashcard.models.Record;
import com.example.flashcard.services.RecordServices;

import java.util.List;

public class ProfilePageActivity extends AppCompatActivity {

    private RecordServices recordServices;
    private TableLayout tableLayout;
    private Button nextButton, prevButton;

    private List<Record> records;
    private int currentPage = 0;
    private final int recordsPerPage = 2; // Display 2 days per page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize the RecordsRepository
        recordServices = new RecordServices(this);

        // Find the TableLayout and pagination buttons in the layout
        tableLayout = findViewById(R.id.dynamicTable);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);

        // Fetch all records once
        records = recordServices.getUserRecords();

        // Display the first page
        populateTable(currentPage);

        // Set up pagination controls
        setupPaginationButtons();

        onReturnButton();
    }

    private void setupPaginationButtons() {
        // Next Button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((currentPage + 1) * recordsPerPage < records.size()) {
                    currentPage++;
                    populateTable(currentPage);
                }
                Toast.makeText(ProfilePageActivity.this, "next pressed", Toast.LENGTH_LONG).show();
                Log.d("ProfilePageActivity", "Previous button clicked. Current page: " + currentPage);
            }
        });

        // Previous Button
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    populateTable(currentPage);
                }
                Toast.makeText(ProfilePageActivity.this, "prev pressed", Toast.LENGTH_LONG).show();
                Log.d("ProfilePageActivity", "Next button clicked. Current page: " + currentPage);
            }
        });

        // Initially disable the Previous button since we start on the first page
        prevButton.setEnabled(false);
    }

    private void populateTable(int page) {
        // Clear existing rows from the table layout
        tableLayout.removeAllViews();

        // Calculate the starting index and ending index for the current page
        int start = page * recordsPerPage;
        int end = Math.min(start + recordsPerPage, records.size());

        // Loop through the records for the current page and display them
        for (int i = start; i < end; i++) {
            addRowsForRecord(records.get(i));
            addSeparatorRow(); // Add a separator row after each date group
        }

        // Enable or disable pagination buttons based on the page index
        prevButton.setEnabled(page > 0);
        nextButton.setEnabled(end < records.size());
    }

    private void addRowsForRecord(Record record) {
        List<Quizz> quizzes = record.getQuizzes();

        for (int i = 0; i < quizzes.size(); i++) {
            Quizz quiz = quizzes.get(i);

            // Create a new TableRow for each topic in the record
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            // Display Date only for the first row of this record
            if (i == 0) {
                TextView dateTextView = new TextView(this);
                dateTextView.setText(record.getDate());  // Display the date only once per group
                dateTextView.setPadding(8, 8, 8, 8);
                dateTextView.setTextSize(16);
                dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                dateTextView.setBackgroundResource(R.drawable.border);  // Set border background
                row.addView(dateTextView);
            } else {
                // Add an empty TextView to align the topics under the date for subsequent rows
                TextView emptyDateTextView = new TextView(this);
                emptyDateTextView.setText(""); // Empty placeholder to align with date column
                emptyDateTextView.setPadding(8, 8, 8, 8);
                emptyDateTextView.setBackgroundResource(R.drawable.border); // Set border background
                row.addView(emptyDateTextView);
            }

            // Create TextView for the topic with border
            TextView topicTextView = new TextView(this);
            topicTextView.setText(quiz.getCategory());  // Assuming Quizz has getCategory() method
            topicTextView.setPadding(8, 8, 8, 8);
            topicTextView.setBackgroundResource(R.drawable.border);  // Set border background for topic

            // Create TextView for score with border
            TextView scoreTextView = new TextView(this);
            scoreTextView.setText(String.valueOf(quiz.getTotal())); // Assuming Quizz has getTotal() method
            scoreTextView.setPadding(8, 8, 8, 8);
            scoreTextView.setBackgroundResource(R.drawable.border);  // Set border background for score

            // Add Topic and Score TextViews to row
            row.addView(topicTextView);  // Topic column
            row.addView(scoreTextView);  // Score column

            // Add the row to the TableLayout
            tableLayout.addView(row);
        }
    }

    private void addSeparatorRow() {
        // Create a separator row with empty cells for spacing
        TableRow separatorRow = new TableRow(this);
        separatorRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        // Add three empty cells for Date, Topic, and Score columns to create a gap
        TextView emptyDateCell = new TextView(this);
        emptyDateCell.setText(""); // Empty date cell
        emptyDateCell.setPadding(8, 8, 8, 8);
        emptyDateCell.setBackgroundResource(R.drawable.border);  // Set border background

        TextView emptyTopicCell = new TextView(this);
        emptyTopicCell.setText(""); // Empty topic cell
        emptyTopicCell.setPadding(8, 8, 8, 8);
        emptyTopicCell.setBackgroundResource(R.drawable.border);  // Set border background

        TextView emptyScoreCell = new TextView(this);
        emptyScoreCell.setText(""); // Empty score cell
        emptyScoreCell.setPadding(8, 8, 8, 8);
        emptyScoreCell.setBackgroundResource(R.drawable.border);  // Set border background

        // Add the empty cells to the separator row
        separatorRow.addView(emptyDateCell);
        separatorRow.addView(emptyTopicCell);
        separatorRow.addView(emptyScoreCell);

        // Add the separator row to the TableLayout
        tableLayout.addView(separatorRow);
    }

    private void onReturnButton() {
        ImageView returnBtn = findViewById(R.id.backButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
