package com.example.prm392_minigames.son.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.quizapp.R;
//import com.example.quizapp.entities.Category;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
        void onViewScoreClick(Category category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category currentCategory = categories.get(position);
        holder.bind(currentCategory);

        // Add animation
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),
                R.anim.slide_in_right));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private TextView tvCategoryDescription;
        private TextView tvQuestionCount;
        private TextView tvScore;
        private TextView tvViewScore;
        private CardView cardView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDescription = itemView.findViewById(R.id.tv_category_description);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvViewScore = itemView.findViewById(R.id.tv_view_score);
            cardView = itemView.findViewById(R.id.card_category);

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(categories.get(position));
                    }
                }
            });

            tvViewScore.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onViewScoreClick(categories.get(position));
                    }
                }
            });
        }

        public void bind(Category category) {
            tvCategoryName.setText(category.getName());
            tvCategoryDescription.setText(category.getDescription());
            tvQuestionCount.setText(category.getTotalQuestions() + " Questions");
            tvScore.setText(category.getUserScore() + "/" + category.getMaxScore());
        }
    }
}

