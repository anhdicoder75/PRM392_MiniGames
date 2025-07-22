package namnq.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.database.Cursor;


import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.hangmangame.db.AppDatabaseHelper;
import com.example.prm392_minigames.R;

public class SoundGameResultActivity extends AppCompatActivity {

    private TextView tvResult, tvScorePercent, tvSummary;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_result);

        // Bind views
        tvResult = findViewById(R.id.tvResult);
        tvScorePercent = findViewById(R.id.tvScorePercent);
        tvSummary = findViewById(R.id.tvSummary);
        btnBack = findViewById(R.id.btnBackToLobby);

        // Get data from intent
        int correctCount = getIntent().getIntExtra("correctCount", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        // Calculate
        int incorrectCount = totalQuestions - correctCount;
        int gameScore = correctCount * 50;
        double percent = ((double) correctCount / totalQuestions) * 100;

        // Display result
        tvResult.setText("Số câu đúng: " + correctCount + " / " + totalQuestions);
        tvSummary.setText("Sai: " + incorrectCount + " câu");
        tvScorePercent.setText("Điểm lượt chơi: " + gameScore + " điểm");

        // Cộng điểm vào profile
        AppDatabaseHelper dbHelper = new AppDatabaseHelper(this);
        Cursor cursor = dbHelper.getProfile();

        if (cursor != null && cursor.moveToFirst()) {
            int currentTotalPoint = cursor.getInt(cursor.getColumnIndexOrThrow("point"));
            int newTotalPoint = currentTotalPoint + gameScore;
            dbHelper.updatePoint(newTotalPoint);

            tvScorePercent.append("\nTổng điểm tích hiện tại: " + newTotalPoint + " điểm");
        }

        // Back button
        btnBack.setOnClickListener(v -> finish());
    }
}
