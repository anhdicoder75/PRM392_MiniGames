package com.example.prm392_minigames.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.models.MiniGame;
import java.util.List;

public class MiniGameAdapter extends RecyclerView.Adapter<MiniGameAdapter.GameViewHolder> {

    private final List<MiniGame> games;
    private final OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(int position);
    }

    public MiniGameAdapter(List<MiniGame> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_card, parent, false);
        return new GameViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        MiniGame g = games.get(position);
        holder.imgIcon.setImageResource(g.iconRes);
        holder.tvTitle.setText(g.title);
        holder.tvDesc.setText(g.desc);
        holder.btnGame.setOnClickListener(v -> {
            if (listener != null) listener.onGameClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvDesc;
        Button btnGame;
        public GameViewHolder(View v) {
            super(v);
            imgIcon = v.findViewById(R.id.img_icon);
            tvTitle = v.findViewById(R.id.tv_title);
            tvDesc = v.findViewById(R.id.tv_desc);
            btnGame = v.findViewById(R.id.btn_game);
        }
    }
}