package namnq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.concurrent.Executors;

import namnq.dao.SoundGameQuestionDao;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundQuestion;

public class SoundGameLobbyActivity extends AppCompatActivity {

    private Button btnStartGame, btnAddQuestion;

    private SoundGameQuestionDao questionDao;
    private List<SoundQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_lobby);

        btnStartGame = findViewById(R.id.btnStartGame);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);

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

        btnAddQuestion.setOnClickListener(v ->
                startActivity(new Intent(this, ManageQuestionsActivity.class)));
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
