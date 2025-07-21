package com.example.prm392_minigames.hangmangame;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;

import com.example.prm392_minigames.R;

public class HangmanMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.hangman_main);

        // Handle WindowInsets for EdgeToEdge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        // Initialize buttons
        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnHighScores = findViewById(R.id.btnHighScores);
        Button btnExit = findViewById(R.id.btnExit);

        // Handle "Start Game" button click
        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(HangmanMainActivity.this, HangmanGameActivity.class);
            startActivity(intent);
        });

        // Handle "High Scores" button click
        btnHighScores.setOnClickListener(v -> {
            Intent intent = new Intent(HangmanMainActivity.this, GameScoreActivity.class);
            startActivity(intent);
        });

        // Handle "Exit" button click
        btnExit.setOnClickListener(v -> finish());
    }
}