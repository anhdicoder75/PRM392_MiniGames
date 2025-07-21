package namnq.adapter;

import com.example.prm392_minigames.R;

import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.widget.TextView;


import java.util.List;

import java.io.IOException;
import java.util.List;

public class SoundGameSelectedSoundsAdapter
        extends RecyclerView.Adapter<SoundGameSelectedSoundsAdapter.SoundViewHolder> {

    private final List<Uri> soundUris;
    private final Context context;
    private MediaPlayer mediaPlayer;

    public SoundGameSelectedSoundsAdapter(Context context, List<Uri> soundUris) {
        this.context = context;
        this.soundUris = soundUris;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_sound, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        Uri uri = soundUris.get(position);
        String name = uri.getLastPathSegment(); // Lấy tên file

        holder.tvSoundName.setText(name != null ? name : "Âm thanh #" + (position + 1));
        holder.btnPlaySound.setOnClickListener(v -> playSound(uri));
    }

    @Override
    public int getItemCount() {
        return soundUris.size();
    }

    private void playSound(Uri uri) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
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