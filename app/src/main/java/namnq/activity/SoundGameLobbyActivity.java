package namnq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.concurrent.Executors;

import namnq.adapter.SoundQuestionAdapter;
import namnq.dao.SoundGameQuestionDao;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundQuestion;

public class SoundGameLobbyActivity extends AppCompatActivity {

    private RecyclerView rvQuestions;
    private Button btnStartGame, btnAddQuestion;
    private ProgressBar progressBar;

    private SoundGameQuestionDao questionDao;
    private List<SoundQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_lobby);

        rvQuestions = findViewById(R.id.rvQuestions);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        progressBar = findViewById(R.id.progressBar);

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

            runOnUiThread(() -> {
                rvQuestions.setLayoutManager(new LinearLayoutManager(this));
                rvQuestions.setAdapter(new SoundQuestionAdapter(this, questionList, new SoundQuestionAdapter.OnQuestionActionListener() {
                    @Override
                    public void onUpdate(SoundQuestion question) {
                        // TODO: Mở activity update (sẽ implement sau)
                        Toast.makeText(SoundGameLobbyActivity.this, "Update ID: " + question.id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDelete(SoundQuestion question) {
                        // TODO: Xóa question (sẽ implement sau)
                        Toast.makeText(SoundGameLobbyActivity.this, "Delete ID: " + question.id, Toast.LENGTH_SHORT).show();
                    }
                }));
                progressBar.setMax(questionList.size());
                progressBar.setProgress(0);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions();
    }
}
