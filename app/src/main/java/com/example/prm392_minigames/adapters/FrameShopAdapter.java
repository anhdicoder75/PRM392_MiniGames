package com.example.prm392_minigames.adapters;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.models.AvatarFrame;
import com.example.prm392_minigames.db.AppDatabaseHelper;
import java.util.List;

public class FrameShopAdapter extends RecyclerView.Adapter<FrameShopAdapter.FrameViewHolder> {
    private final List<AvatarFrame> frames;
    private final Context context;
    private final int userPoint;
    private final int currentFrameId;
    private final java.util.Set<Integer> owned;

    public interface OnActionListener {
        void onAction(AvatarFrame frame);
    }

    private final OnActionListener listener;

    public FrameShopAdapter(Context ctx, List<AvatarFrame> frames, int userPoint, int currentFrameId, java.util.Set<Integer> owned, OnActionListener l) {
        this.context = ctx;
        this.frames = frames;
        this.userPoint = userPoint;
        this.currentFrameId = currentFrameId;
        this.owned = owned;
        this.listener = l;
    }

    @NonNull
    @Override
    public FrameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_frame, parent, false);
        return new FrameViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FrameViewHolder holder, int position) {
        AvatarFrame frame = frames.get(position);
        holder.tvName.setText(frame.name);
        holder.tvCost.setText(frame.cost > 0 ? frame.cost + " điểm" : "Miễn phí");
        holder.img.setImageResource(frame.iconRes);

        if (frame.id == 1 || frame.id == 2) {
            AnimationDrawable anim = (AnimationDrawable) holder.img.getDrawable();
            holder.img.post(anim::start);
        }

        holder.btnAction.setScaleX(1f);
        holder.btnAction.setScaleY(1f);

        if (frame.id == currentFrameId) {
            holder.btnAction.setText("Đang dùng");
            holder.btnAction.setEnabled(false);
        } else if (owned.contains(frame.id)) {
            holder.btnAction.setText("Sử dụng");
            holder.btnAction.setEnabled(true);
        } else if (userPoint >= frame.cost) {
            holder.btnAction.setText("Mua");
            holder.btnAction.setEnabled(true);
        } else {
            holder.btnAction.setText("Thiếu điểm");
            holder.btnAction.setEnabled(false);
        }

        holder.btnAction.setOnClickListener(v -> {
            holder.btnAction.animate().scaleX(1.1f).scaleY(1.1f).setDuration(80)
                    .withEndAction(() -> {
                        holder.btnAction.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
                        listener.onAction(frame);
                    }).start();
        });
    }

    @Override
    public int getItemCount() {
        return frames.size();
    }

    public static class FrameViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvCost;
        Button btnAction;
        public FrameViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.img_frame);
            tvName = v.findViewById(R.id.tv_name);
            tvCost = v.findViewById(R.id.tv_cost);
            btnAction = v.findViewById(R.id.btn_action);
        }
    }
}
