package com.example.prm392_minigames.son.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.quizapp.R;
//import com.example.quizapp.entities.Question;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.entities.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionScoreAdapter extends RecyclerView.Adapter<QuestionScoreAdapter.ViewHolder> {
    private List<Question> questions = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        TextView tvStatus;
        TextView tvPoints;
        CardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPoints = itemView.findViewById(R.id.tv_points);
            cardView = itemView.findViewById(R.id.card_question);
        }

        void bind(Question question) {
            tvQuestionText.setText(question.getQuestionText());

            if (question.isAnswered()) {
                if (question.isCorrect()) {
                    tvStatus.setText("✓ Correct");
                    tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    tvPoints.setText("+" + question.getPoints() + " pts");
                    cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
                } else {
                    tvStatus.setText("✗ Wrong");
                    tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    tvPoints.setText("0 pts");
                    cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
                }
            } else {
                tvStatus.setText("Not Answered");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                tvPoints.setText("0 pts");
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.background_light));
            }
        }
    }
}
