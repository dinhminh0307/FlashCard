package com.example.flashcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.flashcard.R;
import com.example.flashcard.adapters.FlashcardAdapter;
import com.example.flashcard.dialogs.FlashCardEditFragment;
import com.example.flashcard.dialogs.FormDialogFragment;
import com.example.flashcard.exceptions.NoResourceFound;
import com.example.flashcard.models.FlashCard;
import com.example.flashcard.repo.FlashCardRepository;
import com.example.flashcard.services.FlashCardServices;
import com.example.flashcard.utils.Constant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.media.AudioManager;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CardPageActivity extends AppCompatActivity implements FormDialogFragment.OnQuestionAddedListener {
//    private FlashCardRepository flashCardRepository;
    private FlashCardServices flashCardServices;
    private ViewPager2 viewPager;
    private FlashcardAdapter adapter;
    private boolean isAnswerVisible = false;

    private String categoryName = "";

    private Button speakBtn;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_page);

        try {
            flashCardServices = new FlashCardServices(this);
            viewPager = findViewById(R.id.viewPager);

            Intent intent = getIntent();
            categoryName = intent.getStringExtra("category_name");

            if (categoryName == null) {
                throw new IllegalArgumentException("Category name is null");
            }
            speakBtn = findViewById(R.id.speakBtn);
            enableSpeakButton();
            initAnswerField();
            initTextToSpeech();
            onSpeakButtonClick();
            navigateFlashCardByCategories(categoryName);
            setReturnButton();
            cardPageSlidingListener();
            editQuestion(categoryName);
            addQuestion(categoryName);

        } catch (Exception e) {
            Log.e("CardPageActivity", "Error initializing CardPageActivity", e);
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void enableSpeakButton() {
        if(Objects.equals(categoryName, Constant.LANGUAGE_CONST)) {
            speakBtn.setVisibility(View.VISIBLE);
        } else {
            speakBtn.setVisibility(View.GONE);
        }
    }

    private void onSpeakButtonClick() {
        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    speak();
                } catch (NoResourceFound e) {
                    Toast.makeText(CardPageActivity.this, "Please add card to speak", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Method to convert text to speech
    private void speak() {
        int current = getCurrentCardPosition(); // get current position
        setMaxVolume();
        // Check if the flashCards list in the adapter is empty
        if (adapter.getItemCount() == 0) {

            throw new NoResourceFound("No flashcards available to edit.");
        }

        FlashCard currentCard = adapter.getFlashCardAt(current);


        String text = currentCard.getQuestions();
        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter text to speak", Toast.LENGTH_SHORT).show();
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported or missing data");
                    } else {
                        speakBtn.setEnabled(true); // Enable button if TTS is available

                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }
    private void cardPageSlidingListener() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                showCurrentCardPosition(); // Call the method to update the position display
            }
        });
    }

    private void setMaxVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
        );
    }

    @Override
    public void onQuestionAdded() {
        // Called when a new question is added, so refresh data
        navigateFlashCardByCategories(categoryName);
    }

    private void initAnswerField() {
        MaterialButton expandButton = findViewById(R.id.roundedSquareButton);
        expandButton.setOnClickListener(v -> toggleAnswerVisibility());
    }

    private void addQuestion(String tableName) {
        FloatingActionButton addInButton = findViewById(R.id.fabAdd);
        addInButton.setOnClickListener(v -> {
            FormDialogFragment modal = new FormDialogFragment(tableName);
            modal.show(getSupportFragmentManager(), "FormDialogFragment");
        });
    }

    private void editQuestion(String tableName) {
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            try {
                int currentPosition = getCurrentCardPosition();

                // Check if the flashCards list in the adapter is empty
                if (adapter.getItemCount() == 0) {
                    throw new NoResourceFound("No flashcards available to edit.");
                }

                FlashCard currentCard = adapter.getFlashCardAt(currentPosition);

                // Open the FlashCardEditFragment with the current flashcard data
                FlashCardEditFragment editFragment = new FlashCardEditFragment(currentCard, tableName, currentCard.getId());
                editFragment.show(getSupportFragmentManager(), "FlashCardEditFragment");

            } catch (NoResourceFound e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this, "No flashcard selected.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void toggleAnswerVisibility() {
        isAnswerVisible = !isAnswerVisible;
        if (adapter != null) {
            adapter.setAnswerVisibility(isAnswerVisible);
        }
    }

    private void navigateFlashCardByCategories(String category) {
        switch (category) {
            case Constant.MATH_CONST:
                fetchFlashCardPages("math_questions", "Math Flashcard");
                break;
            case Constant.PHYSICS_CONST:
                fetchFlashCardPages("physics_questions", "Physics Flashcard");
                break;
            case Constant.COMPUTER_SCIENCE_CONST:
                fetchFlashCardPages("computer_science_questions", "Computer Science");
                break;
            case Constant.LANGUAGE_CONST:
                fetchFlashCardPages("language_questions", "Language Flashcard");
                break;
            default:
                Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void fetchFlashCardPages(String tableName, String titleText) {
        try {
            List<FlashCard> questions = flashCardServices.getFlashCardContent(tableName, titleText);
            Log.d("FlashCardRepository", "Questions: " + questions.get(0).getQuestions());

            adapter = new FlashcardAdapter(questions);
            viewPager.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("CardPageActivity", "Error fetching flashcards", e);
            Toast.makeText(this, "Error loading flashcards", Toast.LENGTH_LONG).show();
        }
    }

    private int getCurrentCardPosition() {
        return viewPager.getCurrentItem(); // This will return the index of the current card
    }

    // Example usage of getting the current card position
    private void showCurrentCardPosition() {
        int currentPosition = getCurrentCardPosition() + 1; // +1 for 1-based index
        int totalCards = adapter.getItemCount();

        // Update the text progress indicator
        TextView progress = findViewById(R.id.progressText);
        progress.setText(currentPosition + "/" + totalCards);

        // Set the progress bar value
        ProgressBar progressBar = findViewById(R.id.progressBar);
        int progressPercentage = (int) (((float) currentPosition / totalCards) * 100); // Calculate percentage
        progressBar.setProgress(progressPercentage, true);
    }


    private void setReturnButton() {
        ImageView returnBtn = findViewById(R.id.backButton);
        returnBtn.setOnClickListener(v -> finish());
    }
}
