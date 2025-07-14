package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.db.AppDatabaseHelper;
import com.example.prm392_minigames.adapters.MiniGameAdapter;
import com.example.prm392_minigames.models.MiniGame;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.*;

public class MainActivity extends Activity {
    TextView tvWelcome;
    Button btnPlay;
    RecyclerView rvGames;
    FloatingActionButton fabSync;
    MiniGameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tv_welcome);
        btnPlay = findViewById(R.id.btn_play);
        rvGames = findViewById(R.id.rv_games);
        fabSync = findViewById(R.id.fab_sync);

        // Hiển thị tên người dùng nếu có
        AppDatabaseHelper db = new AppDatabaseHelper(this);
        Cursor c = db.getProfile();
        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_NAME));
            tvWelcome.setText("Xin chào, " + name + "!");
        } else {
            tvWelcome.setText("Xin chào!");
        }

        // Danh sách game
        List<MiniGame> games = Arrays.asList(
                new MiniGame(R.drawable.ic_quiz, "Trắc nghiệm", "20 câu hỏi kiểm tra kiến thức!"),
                new MiniGame(R.drawable.ic_brush, "Vẽ Scribble", "Vẽ theo chủ đề, đoán tranh vui nhộn!"),
                new MiniGame(R.drawable.ic_volume, "Đoán Âm Thanh", "Nghe tiếng, đoán tên vật thể."),
                new MiniGame(R.drawable.ic_image, "Đoán Hình Ảnh", "Nhìn hình đoán đáp án đúng."),
                new MiniGame(R.drawable.ic_hangman, "Hangman", "Đoán chữ cái, cứu người treo cổ!")
        );
        adapter = new MiniGameAdapter(games, pos -> {
            Toast.makeText(this, "Bạn chọn: " + games.get(pos).title, Toast.LENGTH_SHORT).show();
        });
        rvGames.setLayoutManager(new LinearLayoutManager(this));
        rvGames.setAdapter(adapter);

        // Animation: ẩn games lúc đầu, chỉ hiện nút "Chơi ngay!"
        rvGames.setVisibility(View.GONE);

        btnPlay.setOnClickListener(v -> {
            btnPlay.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> btnPlay.setVisibility(View.GONE))
                    .start();
            rvGames.setAlpha(0f);
            rvGames.setVisibility(View.VISIBLE);
            rvGames.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .start();
        });

        fabSync.setOnClickListener(v -> {
            startActivity(new Intent(this, SyncActivity.class));
        });
    }
}
