package com.example.flashcard.controller;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flashcard.R;
import com.example.flashcard.models.Quizz;
import com.example.flashcard.models.Record;
import com.example.flashcard.repo.RecordsRepository;
import java.util.List;

public class ProfilePageActivity extends AppCompatActivity {

    private RecordsRepository recordsRepository;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize the RecordsRepository
        recordsRepository = new RecordsRepository(this);

        // Find the TableLayout in the layout
        tableLayout = findViewById(R.id.dynamicTable);

        // Fetch and display records
        populateTable();
        onReturnButton();
    }

    private void populateTable() {
        // Fetch all records from the database
        List<Record> records = recordsRepository.getAllRecords();

        // Loop through each record and add rows in the TableLayout
        for (Record record : records) {
            addRowsForRecord(record);  // Add main row with date and sub-rows for topics
            addSeparatorRow();  // Add a separator row after each date group
        }
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

            // Add Date only for the first row of this record
            if (i == 0) {
                TextView dateTextView = new TextView(this);
                dateTextView.setText(record.getDate());  // Assuming Record has a getDate() method
                dateTextView.setPadding(8, 8, 8, 8);
                dateTextView.setTextSize(16);
                dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                row.addView(dateTextView);
            } else {
                // Add an empty TextView to align topics under the date for subsequent rows
                TextView emptyDateTextView = new TextView(this);
                emptyDateTextView.setText(""); // Empty placeholder to align with date column
                emptyDateTextView.setPadding(8, 8, 8, 8);
                row.addView(emptyDateTextView);
            }

            // Create TextView for the topic
            TextView topicTextView = new TextView(this);
            topicTextView.setText(quiz.getCategory());  // Assuming Quizz has getCategory() method
            topicTextView.setPadding(8, 8, 8, 8);

            // Create TextView for score
            TextView scoreTextView = new TextView(this);
            scoreTextView.setText(String.valueOf(quiz.getTotal())); // Assuming Quizz has getTotal() method
            scoreTextView.setPadding(8, 8, 8, 8);

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

        TextView emptyTopicCell = new TextView(this);
        emptyTopicCell.setText(""); // Empty topic cell
        emptyTopicCell.setPadding(8, 8, 8, 8);

        TextView emptyScoreCell = new TextView(this);
        emptyScoreCell.setText(""); // Empty score cell
        emptyScoreCell.setPadding(8, 8, 8, 8);

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
