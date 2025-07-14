package com.example.prm392_minigames;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Xử lý WindowInsets để hỗ trợ EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các nút
        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnHighScores = findViewById(R.id.btnHighScores);
        Button btnExit = findViewById(R.id.btnExit);

        // Xử lý sự kiện nhấn nút "Bắt đầu chơi"
        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HangmanGameActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút "Xem điểm cao"
        btnHighScores.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameScoreActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút "Thoát"
        btnExit.setOnClickListener(v -> finish());
    }
}