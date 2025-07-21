package namnq.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        int incorrectCount = totalQuestions - correctCount;

        // Calculate percentage
        double percent = ((double) correctCount / totalQuestions) * 100;

        // Display results
        tvResult.setText("✅ Bạn trả lời đúng: " + correctCount + " / " + totalQuestions);
        tvSummary.setText("❌ Sai: " + incorrectCount + " câu");
        tvScorePercent.setText("🏆 Điểm: " + String.format("%.2f", percent) + " điểm");

        // Button back to lobby
        btnBack.setOnClickListener(v -> finish());
    }
}
