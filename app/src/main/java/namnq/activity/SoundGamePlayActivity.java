package namnq.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
import java.util.Collections;
import java.util.concurrent.Executors;

import namnq.adapter.SoundGameUrlAdapter;
import namnq.dao.SoundGameCategoryDao;
import namnq.dao.SoundGameQuestionDao;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundCategory;
import namnq.model.SoundQuestion;

public class SoundGamePlayActivity extends AppCompatActivity {

    private static final String TAG = "SoundGamePlayActivity";

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
    private boolean isQuestionAnswered = false; // 🔥 Thêm flag để track trạng thái câu hỏi

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
            Log.d(TAG, "Total questions in DB: " + allQuestions.size());

            // 🔥 Random list câu hỏi
            Collections.shuffle(allQuestions);

            // 🔥 Lấy tối đa 5 câu hỏi
            if (allQuestions.size() > 5) {
                questionList = allQuestions.subList(0, 5);
            } else {
                questionList = allQuestions;
            }

            Log.d(TAG, "Selected questions for game: " + questionList.size());

            runOnUiThread(() -> {
                if (questionList == null || questionList.isEmpty()) {
                    Toast.makeText(this, "Không có câu hỏi âm thanh nào! Hãy tạo câu hỏi mới.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, SoundGameAddQuestionsActivity.class));
                    finish();
                } else {
                    showQuestion();
                }
            });
        });
    }

    private void showQuestion() {
        Log.d(TAG, "Showing question " + (currentQuestionIndex + 1) + " of " + questionList.size());

        // Reset trạng thái cho câu hỏi mới
        attemptsLeft = 2;
        isQuestionAnswered = false; // 🔥 Reset flag

        // Cancel timer nếu có
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        // Release MediaPlayer nếu có
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        SoundQuestion q = questionList.get(currentQuestionIndex);

        // Hiển thị hint
        if (q.hint != null && !q.hint.isEmpty()) {
            tvHint.setText("Gợi ý: " + q.hint);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvHint.setVisibility(View.GONE);
        }

        // Hiển thị category
        if (q.categoryId > 0) {
            Executors.newSingleThreadExecutor().execute(() -> {
                SoundCategory c = categoryDao.getById(q.categoryId);
                runOnUiThread(() -> tvCategoryName.setText(c != null ? c.name : "Không rõ chủ đề"));
            });
        }

        // Hiển thị sounds
        rvSounds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSounds.setAdapter(new SoundGameUrlAdapter(this, q.soundUrls));

        // Auto play sound đầu tiên nếu có
        if (q.soundUrls != null && !q.soundUrls.isEmpty()) {
            playSound(q.soundUrls.get(0));
        }

        // Countdown timer - 🔥 Cải thiện logic timer
        if (q.timeLimitSeconds > 0) {
            countDownTimer = new CountDownTimer(q.timeLimitSeconds * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    if (!isQuestionAnswered) { // 🔥 Chỉ update countdown nếu chưa trả lời
                        tvCountdown.setText("⏱️ " + millisUntilFinished / 1000 + "s");
                    }
                }

                public void onFinish() {
                    if (!isQuestionAnswered) { // 🔥 Chỉ xử lý timeout nếu chưa trả lời
                        tvCountdown.setText("Hết giờ!");
                        handleTimeUp(q);
                    }
                }
            }.start();
        }

        // Load options
        optionsContainer.removeAllViews();
        for (String option : q.options) {
            Button btnOption = new Button(this);
            btnOption.setText(option);
            btnOption.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            btnOption.setOnClickListener(v -> {
                if (!isQuestionAnswered) { // 🔥 Chỉ cho phép trả lời nếu chưa hoàn thành
                    handleAnswer(q, option, btnOption);
                }
            });

            optionsContainer.addView(btnOption);
        }

        btnNextQuestion.setEnabled(false);
    }

    private void handleAnswer(SoundQuestion q, String selectedOption, Button btnOption) {
        if (selectedOption.equals(q.correctAnswer)) {
            Toast.makeText(this, "✅ Chính xác!", Toast.LENGTH_SHORT).show();
            score++;
            finishCurrentQuestion(); // 🔥 Gọi method riêng để hoàn thành câu hỏi
        } else {
            attemptsLeft--;
            btnOption.setEnabled(false);
            if (attemptsLeft > 0) {
                Toast.makeText(this, "❌ Sai rồi! Thử lại lần nữa.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "❌ Sai. Đáp án đúng: " + q.correctAnswer, Toast.LENGTH_SHORT).show();
                finishCurrentQuestion(); // 🔥 Gọi method riêng để hoàn thành câu hỏi
            }
        }
    }

    // 🔥 Method mới để xử lý khi hoàn thành câu hỏi
    private void finishCurrentQuestion() {
        isQuestionAnswered = true;
        lockOptions();
        btnNextQuestion.setEnabled(true);

        // Stop countdown timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        // Update countdown display
        tvCountdown.setText("Hoàn thành!");
    }

    // 🔥 Method mới để xử lý khi hết thời gian
    private void handleTimeUp(SoundQuestion q) {
        Toast.makeText(this, "⏰ Hết giờ! Đáp án đúng: " + q.correctAnswer, Toast.LENGTH_SHORT).show();
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
            mediaPlayer.setOnPreparedListener(mp -> mp.start());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                Toast.makeText(this, "Không thể phát âm thanh.", Toast.LENGTH_SHORT).show();
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể phát âm thanh.", Toast.LENGTH_SHORT).show();
        }
    }

    private void lockOptions() {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            optionsContainer.getChildAt(i).setEnabled(false);
        }
    }

    private void nextQuestion() {
        Log.d(TAG, "Moving to next question. Current index: " + currentQuestionIndex);

        currentQuestionIndex++;

        if (currentQuestionIndex < questionList.size()) {
            Log.d(TAG, "Showing next question");
            showQuestion();
        } else {
            // 🔥 Hoàn thành game - chuyển sang màn hình kết quả
            Log.d(TAG, "Game completed. Score: " + score + "/" + questionList.size());

            Intent intent = new Intent(this, SoundGameResultActivity.class);
            intent.putExtra("correctCount", score);
            intent.putExtra("totalQuestions", questionList.size());

            // 🔥 Thêm log để debug
            Log.d(TAG, "Starting result activity with correctCount=" + score + ", totalQuestions=" + questionList.size());

            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔥 Chỉ reload nếu thực sự cần thiết
        if (questionList == null || questionList.isEmpty()) {
            checkDataAndLoadQuestions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 🔥 Cleanup tốt hơn
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