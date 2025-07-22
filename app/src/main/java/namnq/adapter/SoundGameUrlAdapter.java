package namnq.adapter;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;

import namnq.utils.SoundPlayerUtil;

public class SoundGameUrlAdapter extends RecyclerView.Adapter<SoundGameUrlAdapter.SoundViewHolder> {

    private final List<String> soundPaths;
    private final Context context;

    public SoundGameUrlAdapter(Context context, List<String> soundPaths) {
        this.context = context;
        this.soundPaths = soundPaths;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_selected_sound, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        String assetPath = soundPaths.get(position);

        String name = assetPath.substring(assetPath.lastIndexOf('/') + 1);
//        holder.tvSoundName.setText(name);

        holder.btnPlaySound.setOnClickListener(v -> playSoundFromAssets(assetPath));
    }

    private void playSoundFromAssets(String assetPath) {
        SoundPlayerUtil.getInstance().playSoundFromAssets(context, assetPath);
    }

    @Override
    public int getItemCount() {
        return soundPaths.size();
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
//        TextView tvSoundName;
        ImageView btnPlaySound;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvSoundName = itemView.findViewById(R.id.tvSoundName);
            btnPlaySound = itemView.findViewById(R.id.btnPlaySound);
        }
    }
}
