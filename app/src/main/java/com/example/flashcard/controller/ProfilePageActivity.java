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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePageActivity extends AppCompatActivity {

    private RecordServices recordServices;
    private TableLayout tableLayout;
    private PieChart pieChart;
    private Button nextButton, prevButton, pieChartBtn;

    private List<Record> records;
    private int currentPage = 0;
    private final int recordsPerPage = 2; // Display 2 days per page

    private boolean isPieChartShown = false; // Track whether the pie chart is currently shown

    private  TextView tableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize the RecordsRepository
        recordServices = new RecordServices(this);

        // Find the TableLayout, PieChart, and pagination buttons in the layout
        tableLayout = findViewById(R.id.dynamicTable);
        pieChart = findViewById(R.id.pieChart);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        pieChartBtn = findViewById(R.id.pieChartBtn);
        tableNumber = findViewById(R.id.tableNumber);

        // Fetch all records once
        records = recordServices.getUserRecords();

        // Display the first page
        populateTable(currentPage);

        // Set up pagination controls
        setupPaginationButtons();
        setupPieChartButton();

        onReturnButton();
    }

    private void setupPieChartButton() {
        // Handle View Pie Chart button click
        pieChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between the pie chart and the table view
                if (isPieChartShown) {
                    // Show the table view
                    showTableView();
                } else {
                    // Show the pie chart view
                    displayPieChartForCurrentPage();
                }
            }
        });
    }

    private void displayPieChartForCurrentPage() {
        // Hide the table and navigation buttons, show the pie chart
        tableLayout.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        prevButton.setVisibility(View.GONE);
        tableNumber.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
        pieChartBtn.setText("View Table"); // Change button text to "View Table"
        isPieChartShown = true;

        // Aggregate average scores by category for the current page (2 days)
        int start = currentPage * recordsPerPage;
        int end = Math.min(start + recordsPerPage, records.size());

        // Map to store the total scores per category and their counts for averaging
        HashMap<String, Integer> categoryScores = new HashMap<>();
        HashMap<String, Integer> categoryCounts = new HashMap<>();

        for (int i = start; i < end; i++) {
            Record record = records.get(i);

            // Sum the scores for each quiz in the record by category
            for (Quizz quiz : record.getQuizzes()) {
                int totalScore = (int) (((float) quiz.getTotal() / (quiz.getAttempts() + quiz.getTotal())) * 100);
                String category = quiz.getCategory();

                // Update score total and count for averaging
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + totalScore);
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            }
        }

        // Prepare data entries for the pie chart using average scores
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryScores.entrySet()) {
            String category = entry.getKey();
            int averageScore = entry.getValue() / categoryCounts.get(category); // Calculate the average
            pieEntries.add(new PieEntry(averageScore, category));
        }

        // Create the pie data set and configure it
        PieDataSet dataSet = new PieDataSet(pieEntries, "Average Scores per Category (2 Days)");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh the chart

        // Customize pie chart appearance
        pieChart.setCenterText("Average Scores by Category");
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
    }

    private void showTableView() {
        // Show the table and navigation buttons, hide the pie chart
        tableLayout.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        prevButton.setVisibility(View.VISIBLE);
        tableNumber.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);
        pieChartBtn.setText("View Pie Chart"); // Change button text to "View Pie Chart"
        isPieChartShown = false;
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

        // Update the page indicator in tableNumber TextView

        int totalPages = getTotalPages();
        tableNumber.setText("Page " + (currentPage + 1) + " of " + totalPages);
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
                dateTextView.setText(record.getDate());
                dateTextView.setPadding(8, 8, 8, 8);
                dateTextView.setTextSize(16);
                dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                dateTextView.setBackgroundResource(R.drawable.border);
                row.addView(dateTextView);
            } else {
                TextView emptyDateTextView = new TextView(this);
                emptyDateTextView.setText("");
                emptyDateTextView.setPadding(8, 8, 8, 8);
                emptyDateTextView.setBackgroundResource(R.drawable.border);
                row.addView(emptyDateTextView);
            }

            TextView topicTextView = new TextView(this);
            topicTextView.setText(quiz.getCategory());
            topicTextView.setPadding(8, 8, 8, 8);
            topicTextView.setBackgroundResource(R.drawable.border);

            TextView scoreTextView = new TextView(this);
            int total_score = (int) (((float) quiz.getTotal() / (quiz.getAttempts() + quiz.getTotal())) * 100);
            scoreTextView.setText(String.valueOf(total_score));
            scoreTextView.setPadding(8, 8, 8, 8);
            scoreTextView.setBackgroundResource(R.drawable.border);

            row.addView(topicTextView);
            row.addView(scoreTextView);

            tableLayout.addView(row);
        }
    }

    private void addSeparatorRow() {
        TableRow separatorRow = new TableRow(this);
        separatorRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView emptyDateCell = new TextView(this);
        emptyDateCell.setText("");
        emptyDateCell.setPadding(8, 8, 8, 8);
        emptyDateCell.setBackgroundResource(R.drawable.border);

        TextView emptyTopicCell = new TextView(this);
        emptyTopicCell.setText("");
        emptyTopicCell.setPadding(8, 8, 8, 8);
        emptyTopicCell.setBackgroundResource(R.drawable.border);

        TextView emptyScoreCell = new TextView(this);
        emptyScoreCell.setText("");
        emptyScoreCell.setPadding(8, 8, 8, 8);
        emptyScoreCell.setBackgroundResource(R.drawable.border);

        separatorRow.addView(emptyDateCell);
        separatorRow.addView(emptyTopicCell);
        separatorRow.addView(emptyScoreCell);

        tableLayout.addView(separatorRow);
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) records.size() / recordsPerPage);
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
