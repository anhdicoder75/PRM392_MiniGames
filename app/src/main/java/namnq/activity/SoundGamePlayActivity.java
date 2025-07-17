package namnq.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.concurrent.Executors;

import namnq.adapter.SoundGameCategoryAdapter;
import namnq.adapter.SoundGameSelectedSoundsAdapter;
import namnq.adapter.SoundGameUrlAdapter;
import namnq.dao.SoundGameCategoryDao;
import namnq.dao.SoundGameQuestionDao;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundCategory;
import namnq.model.SoundQuestion;

public class SoundGamePlayActivity extends AppCompatActivity {

    private RecyclerView rvSounds;
    private LinearLayout optionsContainer;
    private Button btnNextQuestion;
    private TextView tvHint;
    private TextView tvCategoryName;

    private List<SoundQuestion> questionList;
    private int currentQuestionIndex = 0;

    private SoundGameQuestionDao questionDao;
    private SoundGameCategoryDao categoryDao;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_play);

        rvSounds = findViewById(R.id.rvQuestionSounds);
        optionsContainer = findViewById(R.id.optionsContainer);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        tvHint = findViewById(R.id.tvHint);
        tvCategoryName = findViewById(R.id.tvCategoryName);

        SoundAppDatabase db = SoundAppDatabase.getInstance(this);
        questionDao = db.soundGameQuestionDao();
        categoryDao = db.soundGameCategoryDao();

        Executors.newSingleThreadExecutor().execute(() -> {
            questionList = questionDao.getAll();

            runOnUiThread(() -> {
                if (questionList == null || questionList.isEmpty()) {
                    Toast.makeText(this, "Không có câu hỏi âm thanh nào!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showQuestion();
                }
            });
        });

        btnNextQuestion.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                showQuestion();
            } else {
                Toast.makeText(this, "Bạn đã hoàn thành trò chơi!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showQuestion() {
        SoundQuestion q = questionList.get(currentQuestionIndex);

        // Hint
        if (q.hint != null && !q.hint.isEmpty()) {
            tvHint.setText("Gợi ý: " + q.hint);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvHint.setVisibility(View.GONE);
        }

        // Category name
        if (q.categoryId > 0) {
            Executors.newSingleThreadExecutor().execute(() -> {
                SoundCategory c = categoryDao.getById(q.categoryId);
                runOnUiThread(() -> {
                    if (c != null && c.name != null) {
                        tvCategoryName.setText(c.name);
                    } else {
                        tvCategoryName.setText("Không rõ chủ đề");
                    }
                });
            });
        }

        // Load sounds (list URL)
        rvSounds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSounds.setAdapter(new SoundGameUrlAdapter(this, q.soundUrls));

        // Load options
        optionsContainer.removeAllViews();
        for (String option : q.options) {
            Button btnOption = new Button(this);
            btnOption.setText(option);
            btnOption.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            btnOption.setOnClickListener(v -> {
                boolean isCorrect = option.equals(q.correctAnswer);
                Toast.makeText(this, isCorrect ? "Chính xác!" : "Sai rồi!", Toast.LENGTH_SHORT).show();
            });
            optionsContainer.addView(btnOption);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}