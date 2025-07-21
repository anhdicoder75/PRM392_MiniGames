package ditd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import ditd.adapter.ImageGameCategoryAdapter;
import ditd.adapter.ImageGameSelectedImagesAdapter;
import ditd.adapter.ImageGameUrlAdapter;
import ditd.dao.ImageGameCategoryDao;
import ditd.dao.ImageGameQuestionDao;
import ditd.db.AppDatabase;
import ditd.model.Category;
import ditd.model.Question;

public class ImageGamePlayActivity extends AppCompatActivity {
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private Button btnHint, btnSkip,btn5050;
    private RecyclerView rvQuestionImages;
    private LinearLayout optionsContainer;
    private TextView tvHint;
    private TextView tvCountdown;
    private TextView tvCategoryName;
    private CountDownTimer currentTimer;
    private TextView tvLives,tvScores;
    private AppDatabase db;
    private ImageGameQuestionDao imageGameQuestionDao;
    private ImageGameCategoryDao imageGameCategoryDao;
    private ImageGameHelpManager helpManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_activity_play);

        rvQuestionImages = findViewById(R.id.rvQuestionImages);
        optionsContainer = findViewById(R.id.optionsContainer);
        tvHint = findViewById(R.id.tvHint);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvLives = findViewById(R.id.tvLives);
        tvScores = findViewById(R.id.tvScore);

         btnHint = findViewById(R.id.btnHint);
         btnSkip = findViewById(R.id.btnSkip);
         btn5050 = findViewById(R.id.btn5050);

        helpManager = ImageGameHelpManager.getInstance();


        ScrollView scrollView = findViewById(R.id.image_game_activity_play);
        AnimationDrawable animationDrawable = (AnimationDrawable) scrollView.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();



        Intent intent = getIntent();
        int returnedIndex = intent.getIntExtra("questionIndex", -1);
        if (returnedIndex != -1) {
            currentQuestionIndex = returnedIndex;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            db = AppDatabase.getInstance(getApplicationContext());
            imageGameQuestionDao = db.imageGameQuestionDao();
            imageGameCategoryDao = db.imageGameCategoryDao();
            questionList = imageGameQuestionDao.getAll();

            runOnUiThread(() -> showQuestion());
        });

//        btnNextQuestion.setOnClickListener(v -> {
//            currentQuestionIndex++;
//            if (currentQuestionIndex < questionList.size()) {
//                showQuestion();
//            } else {
//                Toast.makeText(this, "You've completed all questions!", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void showQuestion() {
        updateScoreUI();
        Question q = questionList.get(currentQuestionIndex);
        if (q.timeLimitSeconds != null) {
            long totalTimeInSeconds = q.timeLimitSeconds;

            if (currentTimer != null) {
                currentTimer.cancel(); // Hủy timer cũ nếu có
            }

            currentTimer = new CountDownTimer(totalTimeInSeconds * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    int secondsRemaining = (int) millisUntilFinished / 1000;

                    tvCountdown.setText(secondsRemaining + "s");

                    // Đổi màu chữ khi còn dưới 5 giây
                    if (secondsRemaining <= 5) {
                        tvCountdown.setTextColor(Color.RED); // Màu cảnh báo

                        ScaleAnimation pulse = new ScaleAnimation(
                                1f, 1.2f, // fromX, toX
                                1f, 1.2f, // fromY, toY
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        pulse.setDuration(300);
                        pulse.setRepeatCount(1);
                        pulse.setRepeatMode(Animation.REVERSE);
                        tvCountdown.startAnimation(pulse);
                    } else {
                        tvCountdown.setTextColor(Color.BLACK); // Màu bình thường
                    }
                }

                public void onFinish() {
                    tvCountdown.setText("Hết giờ!");
                    tvCountdown.setTextColor(Color.GRAY); // Màu khi hết giờ
                }
            };
            currentTimer.start();
            updateLivesUI();
        }

        // Hint
        if (q.hint != null && !q.hint.isEmpty()) {
            tvHint.setText("Hint: " + q.hint);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvHint.setVisibility(View.GONE);
        }
        if (q.categoryId>0){
         Executors.newSingleThreadExecutor().execute(()->{
             Category c = imageGameCategoryDao.getById(q.categoryId);

             runOnUiThread(()->{
                 if (c!=null && c.name !=null){
                     tvCategoryName.setText(c.name);
                 }else{
                     tvCategoryName.setText("Unknown");
                 }
             });

         });
        }
        // Load images
        rvQuestionImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvQuestionImages.setAdapter(new ImageGameUrlAdapter(q.imageUrls));


        // Load options
        optionsContainer.removeAllViews();
        for (String option : q.options) {
            Button btnOption = new Button(this);
            btnOption.setText(option);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16); // margin bottom 16dp
            btnOption.setLayoutParams(params);

            // Style cho button - bo góc đẹp
            btnOption.setTextColor(Color.parseColor("#333333"));
            btnOption.setTextSize(16);
            btnOption.setTypeface(null, Typeface.NORMAL);
            btnOption.setPadding(32, 24, 32, 24); // padding cho button
            btnOption.setElevation(8f); // shadow
            btnOption.setStateListAnimator(null);
            btnOption.setOnClickListener(v -> {
                boolean isCorrect = option.equals(q.correctAnswer);

                if (isCorrect) {
                    helpManager.increaseScore();
                    updateScoreUI();
                    Intent intent = new Intent(this, ImageGameQuestionResultActivity.class);
                    intent.putExtra("questionId", q.id);
                    intent.putExtra("currentIndex", currentQuestionIndex);
                    intent.putExtra("questionSize",questionList.size());
                    startActivity(intent);
                    finish();


                } else {
                    boolean stillAlive = helpManager.loseLife();
                    updateLivesUI();
                    vibrateView(tvLives);
                    if (stillAlive) {
                        Snackbar.make(findViewById(android.R.id.content),
                                        "Sai rồi! Còn " + helpManager.getLives() + " mạng",
                                        Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(Color.RED)
                                .setTextColor(Color.WHITE)
                                .show();
                    } else {

                            Intent intent = new Intent(this, ImageGameGameOverActivity.class); // hoặc GameOverActivity.class
                            intent.putExtra("score", ImageGameHelpManager.getInstance().getScore()); // gửi score nếu muốn
                            startActivity(intent);
                            finish();
                    }
                }
            });
            optionsContainer.addView(btnOption);
        }
        btnHint.setOnClickListener(v -> {
            if (helpManager.canUseHint()) {
                tvHint.setText("💡 " + questionList.get(currentQuestionIndex).hint); // Gán nội dung gợi ý
                findViewById(R.id.hintCard).setVisibility(View.VISIBLE); // Hiện hintCard
                helpManager.useHint();
                btnHint.setEnabled(false);
            } else {
                Toast.makeText(this, "Bạn đã dùng gợi ý rồi!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSkip.setOnClickListener(v -> {
            if (helpManager.canUseSkip()) {
                helpManager.useSkip();
                btnSkip.setEnabled(false);

            } else {
                Toast.makeText(this, "Bạn đã dùng lượt bỏ qua!", Toast.LENGTH_SHORT).show();
            }
        });

        btn5050.setOnClickListener(v -> {
            if (helpManager.canUse5050()) {
                helpManager.use5050();
                btn5050.setEnabled(false);
                apply5050(q.correctAnswer);
            } else {
                Toast.makeText(this, "Bạn đã dùng 50:50 rồi!", Toast.LENGTH_SHORT).show();
            }
        });




    }
    private void updateLivesUI() {
        tvLives.setText("❤️ x" + helpManager.getLives());
    }
    private void updateScoreUI() {
        tvScores.setText("⭐ Điểm: " + helpManager.getScore());
    }
    private void vibrateView(View view) {
        Animation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(100);
        shake.setRepeatCount(3);
        shake.setRepeatMode(Animation.REVERSE);
        view.startAnimation(shake);
    }

    private void apply5050(String correctAnswer) {
        int removed = 0;
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View v = optionsContainer.getChildAt(i);
            if (v instanceof Button) {
                Button btn = (Button) v;
                if (!btn.getText().toString().equals(correctAnswer) && removed < 2) {
                    AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                    fadeOut.setDuration(1500);
                    fadeOut.setFillAfter(true);

                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            btn.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    btn.startAnimation(fadeOut);
                    removed++;
                }
            }
        }
    }


}
