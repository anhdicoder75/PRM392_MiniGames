package namnq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.concurrent.Executors;

import com.example.prm392_minigames.activities.MainActivity;
import namnq.dao.SoundGameQuestionDao;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundQuestion;

public class SoundGameLobbyActivity extends AppCompatActivity {

    private Button btnStartGame, btnExit;
    private SoundGameQuestionDao questionDao;
    private List<SoundQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_lobby);

        btnStartGame = findViewById(R.id.btnStartGame);
        btnExit = findViewById(R.id.btnExit);

        SoundAppDatabase db = SoundAppDatabase.getInstance(this);
        questionDao = db.soundGameQuestionDao();

        loadQuestions();

        btnStartGame.setOnClickListener(v -> {
            if (questionList == null || questionList.isEmpty()) {
                Toast.makeText(this, "Không có câu hỏi để bắt đầu!", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, SoundGamePlayActivity.class));
            }
        });

        btnExit.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadQuestions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            questionList = questionDao.getAll();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions();
    }
}
