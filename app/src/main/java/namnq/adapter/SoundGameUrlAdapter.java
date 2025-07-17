package namnq.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.io.IOException;
import java.util.List;

public class SoundGameUrlAdapter extends RecyclerView.Adapter<SoundGameUrlAdapter.SoundViewHolder> {

    private final List<String> soundUrls;
    private final Context context;
    private MediaPlayer mediaPlayer;

    public SoundGameUrlAdapter(Context context, List<String> soundUrls) {
        this.context = context;
        this.soundUrls = soundUrls;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_selected_sound, parent, false); // Dùng lại layout sound
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        String url = soundUrls.get(position);

        String name = url.substring(url.lastIndexOf('/') + 1); // Lấy tên file từ URL
        holder.tvSoundName.setText(name);
        holder.btnPlaySound.setOnClickListener(v -> playSoundFromUrl(url));
    }

    private void playSoundFromUrl(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return soundUrls.size();
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        TextView tvSoundName;
        ImageView btnPlaySound;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSoundName = itemView.findViewById(R.id.tvSoundName);
            btnPlaySound = itemView.findViewById(R.id.btnPlaySound);
        }
    }
}