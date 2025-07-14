package com.example.prm392_minigames;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class GameScoreAdapter extends BaseAdapter {
    private Context context;
    private List<GameScore> scoresList;
    private LayoutInflater inflater;

    public GameScoreAdapter(Context context, List<GameScore> scoresList) {
        this.context = context;
        this.scoresList = scoresList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return scoresList.size();
    }

    @Override
    public Object getItem(int position) {
        return scoresList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_game_score, parent, false);
            holder = new ViewHolder();
            holder.tvRank = convertView.findViewById(R.id.tvRank);
            holder.tvPlayerName = convertView.findViewById(R.id.tvPlayerName);
            holder.tvScore = convertView.findViewById(R.id.tvScore);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GameScore score = scoresList.get(position);
        holder.tvRank.setText("#" + (position + 1));
        holder.tvPlayerName.setText(score.getPlayerName());
        holder.tvScore.setText(score.getScore() + " điểm");

        // Format ngày giờ
        String date = score.getDate();
        if (date != null && date.length() > 10) {
            date = date.substring(0, 16).replace("T", " ");
        }
        holder.tvDate.setText(date);

        // Đổi màu cho top 3
        if (position == 0) {
            holder.tvRank.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else if (position == 1) {
            holder.tvRank.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        } else if (position == 2) {
            holder.tvRank.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
        } else {
            holder.tvRank.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvRank;
        TextView tvPlayerName;
        TextView tvScore;
        TextView tvDate;
    }
}
