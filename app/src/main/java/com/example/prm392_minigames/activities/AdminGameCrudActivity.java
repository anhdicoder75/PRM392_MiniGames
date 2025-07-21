package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.*;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.adapters.AdminGameAdapter;
import com.example.prm392_minigames.hangmangame.HangmanMainActivity;
import com.example.prm392_minigames.models.AdminGame;
import java.util.*;

public class AdminGameCrudActivity extends Activity {
    RecyclerView rvGames;
    AdminGameAdapter adapter;
    ArrayList<AdminGame> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_game_crud);

        rvGames = findViewById(R.id.rv_admin_games);

        // 4 game (trừ Memory)
        games = new ArrayList<>();
        games.add(new AdminGame(1, R.drawable.ic_quiz, "Trắc nghiệm kiến thức", "Quiz game hỏi đáp, tổng kết điểm."));
        games.add(new AdminGame(2, R.drawable.ic_volume, "Sound Guess", "Nghe âm thanh, chọn đáp án đúng, có BXH."));
        games.add(new AdminGame(3, R.drawable.ic_image, "Image Guess", "Đoán hình ảnh: chọn đúng đáp án từ ảnh random."));
        games.add(new AdminGame(4, R.drawable.ic_hangman, "Hangman", "Đoán chữ cái, có hint, sai nhiều thua, trừ điểm."));

        adapter = new AdminGameAdapter(games, position -> {
            // Tùy game mà vào từng CRUD tương ứng
            switch (position) {
                case 0: // Quiz
                    startActivity(new Intent(this, AdminQuizCrudActivity.class));
                    break;
                case 1: // Sound Guess
                    startActivity(new Intent(this, AdminSoundCrudActivity.class));
                    break;
                case 2: // Image Guess
                    startActivity(new Intent(this, AdminImageCrudActivity.class));
                    break;
                case 3: // Hangman
                    startActivity(new Intent(this, HangmanMainActivity.class));
                    break;
            }
        });
        rvGames.setLayoutManager(new LinearLayoutManager(this));
        rvGames.setAdapter(adapter);
    }
}
