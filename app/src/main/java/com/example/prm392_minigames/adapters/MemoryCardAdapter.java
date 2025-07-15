package com.example.prm392_minigames.adapters;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.models.MemoryCard;
import java.util.List;

public class MemoryCardAdapter extends RecyclerView.Adapter<MemoryCardAdapter.CardViewHolder> {
    private final List<MemoryCard> cards;
    private final OnCardClickListener listener;
    private final Context context;

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    public MemoryCardAdapter(Context context, List<MemoryCard> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int size = parent.getMeasuredWidth() / 3 - 24;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        v.setLayoutParams(new RecyclerView.LayoutParams(size, size));
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        MemoryCard card = cards.get(position);

        // Luôn set lại hình cho cả 2 mặt
        holder.imgCardBack.setImageResource(R.drawable.ic_card_back);
        holder.imgCardFront.setImageResource(card.iconRes);

        if (card.isFlipped || card.isMatched) {
            holder.imgCardFront.setVisibility(View.VISIBLE);
            holder.imgCardBack.setVisibility(View.GONE);
            holder.imgCardFront.setAlpha(card.isMatched ? 0.5f : 1f);
        } else {
            holder.imgCardFront.setVisibility(View.GONE);
            holder.imgCardBack.setVisibility(View.VISIBLE);
            holder.imgCardFront.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> {
            // Chỉ cho click khi thẻ chưa lật và chưa matched
            if (!card.isFlipped && !card.isMatched && listener != null) {
                listener.onCardClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() { return cards.size(); }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCardFront, imgCardBack;
        public CardViewHolder(View v) {
            super(v);
            imgCardFront = v.findViewById(R.id.img_card_front);
            imgCardBack = v.findViewById(R.id.img_card_back);
        }
    }
}
