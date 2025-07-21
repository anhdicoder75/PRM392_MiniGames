package namnq.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;

import namnq.model.SoundQuestion;

public class SoundQuestionAdapter extends RecyclerView.Adapter<SoundQuestionAdapter.QuestionViewHolder> {

    private final Context context;
    private final List<SoundQuestion> questions;
    private final OnQuestionActionListener listener;

    public interface OnQuestionActionListener {
        void onUpdate(SoundQuestion question);
        void onDelete(SoundQuestion question);
    }

    public SoundQuestionAdapter(Context context, List<SoundQuestion> questions, OnQuestionActionListener listener) {
        this.context = context;
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        SoundQuestion q = questions.get(position);
        holder.tvQuestionId.setText("ID: " + q.id);
        holder.tvAnswer.setText("Đáp án: " + q.correctAnswer);


        if (listener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                showActionDialog(q);
                return true;
            });
        } else {
            holder.itemView.setOnLongClickListener(null);
        }
    }

    private void showActionDialog(SoundQuestion question) {
        if (listener == null) return;
        String[] options = {"Cập nhật", "Xóa"};
        new AlertDialog.Builder(context)
                .setTitle("Chọn hành động")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) listener.onUpdate(question);
                    else if (which == 1) listener.onDelete(question);
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionId, tvAnswer;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionId = itemView.findViewById(R.id.tvQuestionId);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
        }
    }
}
