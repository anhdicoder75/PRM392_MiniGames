package com.example.prm392_minigames.son.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.entities.Question;

import java.util.ArrayList;
import java.util.List;

public class AdminQuestionAdapter extends RecyclerView.Adapter<AdminQuestionAdapter.ViewHolder> {
    private List<Question> questions = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Question question);
        void onDeleteClick(Question question);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_question, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        TextView tvCorrectAnswer;
        TextView tvPoints;
        Button btnEdit;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            tvCorrectAnswer = itemView.findViewById(R.id.tv_correct_answer);
            tvPoints = itemView.findViewById(R.id.tv_points);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(questions.get(position));
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(questions.get(position));
                    }
                }
            });
        }

        void bind(Question question) {
            tvQuestionText.setText(question.getQuestionText());
            tvPoints.setText(question.getPoints() + " pts");

            String correctAnswer = "";
            switch (question.getCorrectAnswer()) {
                case 1:
                    correctAnswer = question.getOption1();
                    break;
                case 2:
                    correctAnswer = question.getOption2();
                    break;
                case 3:
                    correctAnswer = question.getOption3();
                    break;
                case 4:
                    correctAnswer = question.getOption4();
                    break;
            }
            tvCorrectAnswer.setText("Correct: " + correctAnswer);
        }
    }
}