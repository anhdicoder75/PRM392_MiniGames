package com.example.prm392_minigames;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class GameScoreActivity extends AppCompatActivity {
    private GameDatabaseHelper dbHelper;
    private ListView lvScores;
    private TextView tvNoScores;
    private Button btnBack, btnClearScores;
    private GameScoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_score);

        dbHelper = new GameDatabaseHelper(this);
        initializeViews();
        loadScores();
    }

    private void initializeViews() {
        lvScores = findViewById(R.id.lvScores);
        tvNoScores = findViewById(R.id.tvNoScores);
        btnBack = findViewById(R.id.btnBack);
        btnClearScores = findViewById(R.id.btnClearScores);

        btnBack.setOnClickListener(v -> finish());
        btnClearScores.setOnClickListener(v -> showClearConfirmDialog());
    }

    private void loadScores() {
        List<GameScore> scoresList = dbHelper.getAllScores();

        if (scoresList.isEmpty()) {
            lvScores.setVisibility(ListView.GONE);
            tvNoScores.setVisibility(TextView.VISIBLE);
            btnClearScores.setEnabled(false);
        } else {
            lvScores.setVisibility(ListView.VISIBLE);
            tvNoScores.setVisibility(TextView.GONE);
            btnClearScores.setEnabled(true);

            adapter = new GameScoreAdapter(this, scoresList);
            lvScores.setAdapter(adapter);
        }
    }

    private void showClearConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả điểm số?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.clearAllScores();
                    loadScores();
                    Toast.makeText(this, "Đã xóa tất cả điểm số!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
