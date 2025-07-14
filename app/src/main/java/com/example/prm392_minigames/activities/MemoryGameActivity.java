package com.example.prm392_minigames.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.models.MemoryCard;
import com.example.prm392_minigames.adapters.MemoryCardAdapter;
import java.util.*;

public class MemoryGameActivity extends AppCompatActivity {
    RecyclerView rvCards;
    TextView tvScore, tvTimer;
    List<MemoryCard> cards;
    MemoryCardAdapter adapter;
    int score = 0;
    int flippedPos = -1;
    Handler handler = new Handler();
    long startTime;
    Runnable timerRunnable;
    boolean isProcessing = false;

    int[] iconList = {
            R.drawable.ic_card_dog, R.drawable.ic_card_cat, R.drawable.ic_card_snake,
            R.drawable.ic_card_girl, R.drawable.ic_card_boy, R.drawable.ic_card_croc
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        rvCards = findViewById(R.id.rv_cards);
        tvScore = findViewById(R.id.tv_score);
        tvTimer = findViewById(R.id.tv_timer);

        setupGame();

        timerRunnable = new Runnable() {
            @Override public void run() {
                long s = (System.currentTimeMillis() - startTime) / 1000;
                tvTimer.setText(String.format("%02d:%02d", s/60, s%60));
                handler.postDelayed(this, 500);
            }
        };
        startTime = System.currentTimeMillis();
        handler.post(timerRunnable);
    }

    void setupGame() {
        cards = new ArrayList<>();
        int id = 0;
        List<Integer> icons = new ArrayList<>();
        for (int i : iconList) {
            icons.add(i); icons.add(i);
        }
        Collections.shuffle(icons);
        for (int i = 0; i < icons.size(); ++i) {
            cards.add(new MemoryCard(id++, icons.get(i)));
        }
        Collections.shuffle(cards);
        adapter = new MemoryCardAdapter(this, cards, this::onCardClick);
        rvCards.setLayoutManager(new GridLayoutManager(this, 3));
        rvCards.setAdapter(adapter);
        score = 0;
        tvScore.setText("Điểm: 0");
        flippedPos = -1;
        isProcessing = false;
    }

    void onCardClick(int pos) {
        if (isProcessing) return;
        if (cards.get(pos).isFlipped || cards.get(pos).isMatched) return;

        cards.get(pos).isFlipped = true;
        adapter.notifyItemChanged(pos);

        if (flippedPos == -1) {
            flippedPos = pos;
        } else {
            int first = flippedPos;
            int second = pos;
            isProcessing = true;
            handler.postDelayed(() -> {
                if (cards.get(first).iconRes == cards.get(second).iconRes) {
                    cards.get(first).isMatched = true;
                    cards.get(second).isMatched = true;
                    score += 10;
                    tvScore.setText("Điểm: " + score);
                } else {
                    cards.get(first).isFlipped = false;
                    cards.get(second).isFlipped = false;
                }
                adapter.notifyItemChanged(first);
                adapter.notifyItemChanged(second);
                isProcessing = false;
                if (isGameDone()) finishGame();
            }, 900);
            flippedPos = -1;
        }
    }

    boolean isGameDone() {
        for (MemoryCard c : cards) if (!c.isMatched) return false;
        return true;
    }

    void finishGame() {
        handler.removeCallbacks(timerRunnable);
        long totalTime = (System.currentTimeMillis() - startTime)/1000;
        int bonus = Math.max(0, 60 - (int)totalTime);
        new android.app.AlertDialog.Builder(this)
                .setTitle("Chiến thắng!")
                .setMessage("Điểm: " + score + "\nBonus tốc độ: " + bonus + "\nTổng cộng: " + (score + bonus))
                .setPositiveButton("Chơi lại", (d,w) -> {
                    setupGame();
                    startTime = System.currentTimeMillis();
                    handler.post(timerRunnable);
                })
                .setNegativeButton("Thoát", (d,w) -> finish())
                .show();
    }
}
