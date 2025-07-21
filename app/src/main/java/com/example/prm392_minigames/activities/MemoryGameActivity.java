package com.example.prm392_minigames.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.models.MemoryCard;
import com.example.prm392_minigames.adapters.MemoryCardAdapter;
import com.example.prm392_minigames.hangmangame.db.AppDatabaseHelper;
import java.util.*;

public class MemoryGameActivity extends AppCompatActivity {
    RecyclerView rvCards;
    TextView tvScore, tvTimer;
    List<MemoryCard> cards;
    MemoryCardAdapter adapter;
    int score = 0;
    int flippedPos = -1;
    Handler handler = new Handler();
    Runnable timerRunnable;
    boolean isProcessing = false;

    // ==== TIMER LOGIC ====
    static final int TIME_LIMIT = 60; // 60 giây giới hạn
    int timeLeft = TIME_LIMIT;

    // ==== PROFILE POINT ====
    AppDatabaseHelper dbHelper;
    int oldPoint = 0;

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

        // Lấy dữ liệu profile để biết điểm cũ
        dbHelper = new AppDatabaseHelper(this);
        Cursor c = dbHelper.getProfile();
        if (c != null && c.moveToFirst()) {
            oldPoint = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
            c.close();
        }

        setupGame();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    tvTimer.setText(String.format("⏳ %02d:%02d", timeLeft / 60, timeLeft % 60));
                    handler.postDelayed(this, 1000);
                } else {
                    finishGame();
                }
            }
        };
        timeLeft = TIME_LIMIT;
        tvTimer.setText(String.format("⏳ %02d:%02d", timeLeft / 60, timeLeft % 60));
        handler.postDelayed(timerRunnable, 1000);
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
        timeLeft = TIME_LIMIT;
        tvTimer.setText(String.format("⏳ %02d:%02d", timeLeft / 60, timeLeft % 60));
    }

    void onCardClick(int pos) {
        if (isProcessing || timeLeft <= 0) return;
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
        boolean win = isGameDone();
        int reward = 0;

        if (win) {
            // Điểm thưởng = điểm đạt được + thời gian dư
            reward = score + timeLeft;
        } else {
            // Thua: chỉ cộng điểm kiếm được
            reward = score;
        }

        // Cộng dồn vào profile
        int totalPoint = oldPoint + reward;
        dbHelper.updatePoint(totalPoint);

        new android.app.AlertDialog.Builder(this)
                .setTitle(win ? "Chiến thắng!" : "Hết giờ!")
                .setMessage(
                        "Điểm: " + score +
                                (win ? ("\nBonus thời gian: " + timeLeft) : "") +
                                "\nReward: " + reward +
                                "\n\nTổng điểm: " + totalPoint
                )
                .setPositiveButton("Chơi lại", (d, w) -> {
                    // Cập nhật lại điểm profile mới nhất
                    Cursor c = dbHelper.getProfile();
                    if (c != null && c.moveToFirst()) {
                        oldPoint = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
                        c.close();
                    }
                    setupGame();
                    handler.postDelayed(timerRunnable, 1000);
                })
                .setNegativeButton("Thoát", (d, w) -> finish())
                .show();
    }
}
