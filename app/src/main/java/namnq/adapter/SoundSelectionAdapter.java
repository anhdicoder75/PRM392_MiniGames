package namnq.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.io.IOException;
import java.util.List;

import namnq.model.SoundItem;

public class SoundSelectionAdapter extends RecyclerView.Adapter<SoundSelectionAdapter.ViewHolder> {
    private final Context context;
    private final List<SoundItem> soundItems;
    private MediaPlayer mediaPlayer;
    private final OnSoundSelectionListener listener;

    public interface OnSoundSelectionListener {
        void onSoundSelected(SoundItem soundItem, boolean isSelected);
    }

    public SoundSelectionAdapter(Context context, List<SoundItem> soundItems, OnSoundSelectionListener listener) {
        this.context = context;
        this.soundItems = soundItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoundItem item = soundItems.get(position);

        holder.tvSoundName.setText(item.getName());
        holder.tvSoundCategory.setText(item.getCategory());
        holder.cbSelect.setChecked(item.isSelected());

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (listener != null) {
                listener.onSoundSelected(item, isChecked);
            }
        });

        holder.btnPlay.setOnClickListener(v -> playSound(item.getAssetPath()));
    }

    @Override
    public int getItemCount() {
        return soundItems.size();
    }

    private void playSound(String assetPath) {
        try {
            releaseMediaPlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context.getAssets().openFd(assetPath).getFileDescriptor(),
                    context.getAssets().openFd(assetPath).getStartOffset(),
                    context.getAssets().openFd(assetPath).getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
        } catch (IOException e) {
            Toast.makeText(context, "Error playing sound: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSoundName, tvSoundCategory;
        CheckBox cbSelect;
        ImageButton btnPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSoundName = itemView.findViewById(R.id.tvSoundName);
            tvSoundCategory = itemView.findViewById(R.id.tvSoundCategory);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}
