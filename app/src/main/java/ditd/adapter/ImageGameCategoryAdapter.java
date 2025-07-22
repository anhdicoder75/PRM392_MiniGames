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

import ditd.model.Category;

public class ImageGameCategoryAdapter extends RecyclerView.Adapter<ImageGameCategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private final OnCategoryClickListener listener;
    private int selectedIndex = -1; // Không chọn gì lúc đầu

    public interface OnCategoryClickListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public ImageGameCategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void updateList(List<Category> newList) {
        this.categories = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_game_category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category c = categories.get(position);
        holder.tvName.setText(c.name);

        if (position == selectedIndex) {
            holder.itemView.setBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_blue_light)
            );
            holder.tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.itemView.setBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent)
            );
            holder.tvName.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            selectedIndex = currentPosition;
            notifyDataSetChanged();

            listener.onEdit(categories.get(currentPosition));
        });
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}