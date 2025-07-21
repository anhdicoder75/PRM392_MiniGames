package ditd.adapter;

import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        Glide.with(holder.imageView.getContext())
                .load(url)
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
