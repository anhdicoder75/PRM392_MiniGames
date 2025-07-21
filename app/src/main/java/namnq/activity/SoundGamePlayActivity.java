package namnq.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.Executors;

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
    private TextView tvHint, tvCategoryName, tvCountdown;

    private List<SoundQuestion> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private SoundGameQuestionDao questionDao;
    private SoundGameCategoryDao categoryDao;

    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;

    private int attemptsLeft = 2;
    private boolean isQuestionAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_play);

        rvSounds = findViewById(R.id.rvQuestionSounds);
        optionsContainer = findViewById(R.id.optionsContainer);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        tvHint = findViewById(R.id.tvHint);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvCountdown = findViewById(R.id.tvCountdown);

        SoundAppDatabase db = SoundAppDatabase.getInstance(this);
        questionDao = db.soundGameQuestionDao();
        categoryDao = db.soundGameCategoryDao();

        checkDataAndLoadQuestions();

        btnNextQuestion.setOnClickListener(v -> nextQuestion());
    }

    private void checkDataAndLoadQuestions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<SoundQuestion> allQuestions = questionDao.getAll();
            Collections.shuffle(allQuestions);
            questionList = allQuestions.size() > 5 ? allQuestions.subList(0, 5) : allQuestions;

            runOnUiThread(() -> {
                if (questionList == null || questionList.isEmpty()) {
                    startActivity(new Intent(this, SoundGameAddQuestionsActivity.class));
                    finish();
                } else {
                    showQuestion();
                }
            });
        });
    }

    private void showQuestion() {
        attemptsLeft = 2;
        isQuestionAnswered = false;

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        SoundQuestion q = questionList.get(currentQuestionIndex);

        if (q.hint != null && !q.hint.isEmpty()) {
            tvHint.setText("Gợi ý: " + q.hint);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvHint.setVisibility(View.GONE);
        }

        if (q.categoryId > 0) {
            Executors.newSingleThreadExecutor().execute(() -> {
                SoundCategory c = categoryDao.getById(q.categoryId);
                runOnUiThread(() -> tvCategoryName.setText(c != null ? c.name : "Không rõ chủ đề"));
            });
        }

        rvSounds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSounds.setAdapter(new SoundGameUrlAdapter(this, q.soundUrls));

        if (q.soundUrls != null && !q.soundUrls.isEmpty()) {
            playSound(q.soundUrls.get(0));
        }

        if (q.timeLimitSeconds > 0) {
            countDownTimer = new CountDownTimer(q.timeLimitSeconds * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    if (!isQuestionAnswered) {
                        tvCountdown.setText("⏱️ " + millisUntilFinished / 1000 + "s");
                    }
                }

                public void onFinish() {
                    if (!isQuestionAnswered) {
                        tvCountdown.setText("Hết giờ!");
                        handleTimeUp(q);
                    }
                }
            }.start();
        }

        optionsContainer.removeAllViews();
        for (String option : q.options) {
            Button btnOption = new Button(this);
            btnOption.setText(option);
            btnOption.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            btnOption.setOnClickListener(v -> {
                if (!isQuestionAnswered) {
                    handleAnswer(q, option, btnOption);
                }
            });

            optionsContainer.addView(btnOption);
        }

        btnNextQuestion.setEnabled(false);
    }

    private void handleAnswer(SoundQuestion q, String selectedOption, Button btnOption) {
        if (selectedOption.equals(q.correctAnswer)) {
            score++;
            finishCurrentQuestion();
        } else {
            attemptsLeft--;
            btnOption.setEnabled(false);
            if (attemptsLeft <= 0) {
                finishCurrentQuestion();
            }
        }
    }

    private void finishCurrentQuestion() {
        isQuestionAnswered = true;
        lockOptions();
        btnNextQuestion.setEnabled(true);

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        tvCountdown.setText("Hoàn thành!");
    }

    private void handleTimeUp(SoundQuestion q) {
        finishCurrentQuestion();
    }

    private void playSound(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnErrorListener((mp, what, extra) -> true);
        } catch (Exception ignored) {
        }
    }

    private void lockOptions() {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            optionsContainer.getChildAt(i).setEnabled(false);
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            showQuestion();
        } else {
            Intent intent = new Intent(this, SoundGameResultActivity.class);
            intent.putExtra("correctCount", score);
            intent.putExtra("totalQuestions", questionList.size());
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (questionList == null || questionList.isEmpty()) {
            checkDataAndLoadQuestions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
