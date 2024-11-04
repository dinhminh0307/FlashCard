package com.example.flashcard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.models.FlashCard;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private final List<FlashCard> flashCards;
    private boolean isAnswerVisible; // Visibility flag

    public FlashcardAdapter(List<FlashCard> flashCards) {
        this.flashCards = flashCards;
        this.isAnswerVisible = false; // Default: answer not visible
    }

    // Update the answer visibility and notify the adapter to refresh views
    public void setAnswerVisibility(boolean isVisible) {
        this.isAnswerVisible = isVisible;
        notifyDataSetChanged(); // Refresh the adapter to apply the change
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        FlashCard flashCard = flashCards.get(position);
        if(flashCard == null || flashCard.getQuestions().isEmpty()) {
            holder.questionText.setText("Empty Quesntion");
            holder.answerText.setText("Empty");
        } else {
            holder.questionText.setText(flashCard.getQuestions());
            holder.answerText.setText(flashCard.getAnswers());
        }
        // Set the visibility of the answer text based on the isAnswerVisible flag
        holder.answerText.setVisibility(isAnswerVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return flashCards.size();
    }

    public static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        TextView answerText;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.content);
            answerText = itemView.findViewById(R.id.answerText);
        }
    }
}
