package ditd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;

import ditd.model.Question;

public class ImageGameQuestionAdapter extends RecyclerView.Adapter<ImageGameQuestionAdapter.QuestionViewHolder> {
    private List<Question> questions;
    private final OnQuestionActionListener listener;

    public interface OnQuestionActionListener {
        void onEdit(Question question);
        void onDelete(Question question);
        void onDetail(Question question);
    }

    public ImageGameQuestionAdapter(List<Question> questions, OnQuestionActionListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    public void updateList(List<Question> newList) {
        this.questions = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_game_question_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question q = questions.get(position);
        holder.tvQuestion.setText(q.questionText);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(q));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(q));
        holder.itemView.setOnClickListener(v -> listener.onDetail(q));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;
        ImageButton btnEdit, btnDelete;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestionText);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
