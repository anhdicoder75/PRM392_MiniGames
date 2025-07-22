package ditd.adapter;

import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_minigames.R;

import java.util.List;

public class ImageGameUrlAdapter extends RecyclerView.Adapter<ImageGameUrlAdapter.ImageViewHolder>{
    private List<String> imageUrls;

    public ImageGameUrlAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, parent.getResources().getDisplayMetrics());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(8, 8, 8, 8);
        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String url = imageUrls.get(position);

        // Calculate available height (RecyclerView height - padding)
        // RecyclerView is 200dp with 8dp padding on each side = 184dp available
        int availableHeight = (int) (184 * holder.imageView.getContext().getResources().getDisplayMetrics().density);

        // Set proper layout parameters for the ImageView
        ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
        layoutParams.width = availableHeight; // Make it square
        layoutParams.height = availableHeight; // Fit the available height
        holder.imageView.setLayoutParams(layoutParams);

        // Set scale type for better image display
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load image with Glide
        Glide.with(holder.imageView.getContext())
                .load(url)
                .override(184, 184) // Match the ImageView size
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }
}
