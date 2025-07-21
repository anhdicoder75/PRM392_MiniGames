package com.example.prm392_minigames.son.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.entities.Question;
import com.example.prm392_minigames.son.repositories.QuizRepository;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;

public class QuizActivity extends AppCompatActivity {

    private QuizViewModel quizViewModel;
    private TextView tvQuestion, tvScore;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private LinearLayout questionContainer;

    private int categoryId;
    private String categoryName;
    private Question currentQuestion;
    private int totalScore = 0;

    private int hearts = 3;
    private int wrongAnswers = 0;
    private int correctAnswers = 0;
    private CountDownTimer countDownTimer;
    private TextView tvTimer, tvHearts;
    private long timeLeftInMillis = 60000; // 60 seconds
    private Button btnClue;
    private boolean clueUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();
        setupViewModel();
        getCategoryData();
        loadNextQuestion();
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tv_question);
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);
        tvScore = findViewById(R.id.tv_score);
        questionContainer = findViewById(R.id.question_container);

        tvTimer = findViewById(R.id.tv_timer);
        tvHearts = findViewById(R.id.tv_hearts);
        btnClue = findViewById(R.id.btn_clue);
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
    }

    private void getCategoryData() {
        categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (categoryId == -1) {
            finish();
            return;
        }

        setTitle(categoryName + " Quiz");
        updateScoreDisplay();
    }

//    private void loadNextQuestion() {
//        quizViewModel.getNextUnansweredQuestion(categoryId, new QuizRepository.OnQuestionLoadedListener() {
//            @Override
//            public void onQuestionLoaded(Question question) {
//                if (question != null) {
//                    currentQuestion = question;
//                    displayQuestion();
//                } else {
//                    showQuizCompleted();
//                }
//            }
//        });
//    }

    private void loadNextQuestion() {
        quizViewModel.getNextUnansweredQuestionRandom(categoryId, new QuizRepository.OnQuestionLoadedListener() {
            @Override
            public void onQuestionLoaded(Question question) {
                if (question != null) {
                    currentQuestion = question;
                    clueUsed = false;
                    startTimer();
                    displayQuestion();
                } else {
                    showQuizCompleted();
                }
            }
        });
    }

    private void displayQuestion() {
        tvQuestion.setText(currentQuestion.getQuestionText());
        btnOption1.setText(currentQuestion.getOption1());
        btnOption2.setText(currentQuestion.getOption2());
        btnOption3.setText(currentQuestion.getOption3());
        btnOption4.setText(currentQuestion.getOption4());

        questionContainer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));

        btnOption1.setOnClickListener(v -> onOptionSelected(1));
        btnOption2.setOnClickListener(v -> onOptionSelected(2));
        btnOption3.setOnClickListener(v -> onOptionSelected(3));
        btnOption4.setOnClickListener(v -> onOptionSelected(4));

        updateHeartsDisplay();

        // Setup clue button
        btnClue.setVisibility(View.VISIBLE);
        btnClue.setEnabled(!clueUsed);
        btnClue.setOnClickListener(v -> showClue());
    }

//    private void onOptionSelected(int selectedOption) {
//        boolean isCorrect = selectedOption == currentQuestion.getCorrectAnswer();
//
//        currentQuestion.setAnswered(true);
//        currentQuestion.setCorrect(isCorrect);
//
//        if (isCorrect) {
//            totalScore += currentQuestion.getPoints();
//        }
//
//        quizViewModel.updateQuestion(currentQuestion);
//        showAnswerDialog(isCorrect);
//    }

    private void onOptionSelected(int selectedOption) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        boolean isCorrect = selectedOption == currentQuestion.getCorrectAnswer();

        currentQuestion.setAnswered(true);
        currentQuestion.setCorrect(isCorrect);

        if (isCorrect) {
            totalScore += currentQuestion.getPoints();
            correctAnswers++;

            // Check for rewards
            if (currentQuestion.isHard()) {
                showHardQuestionReward();
            } else if (correctAnswers % 5 == 0) {
                showStreakReward();
            }
        } else {
            wrongAnswers++;
            hearts--;
            updateHeartsDisplay();

            if (hearts <= 0) {
                showGameOver();
                return;
            }

            if (wrongAnswers % 3 == 0) {
                showWrongAnswerDialog();
                return;
            }
        }

        quizViewModel.updateQuestion(currentQuestion);
        showAnswerDialog(isCorrect);
    }

    private void showAnswerDialog(boolean isCorrect) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_answer_result);
        dialog.setCancelable(false);

        TextView tvResult = dialog.findViewById(R.id.tv_result);
        TextView tvCorrectAnswer = dialog.findViewById(R.id.tv_correct_answer);
        TextView tvExplanation = dialog.findViewById(R.id.tv_explanation);
        TextView tvPoints = dialog.findViewById(R.id.tv_points);
        Button btnNext = dialog.findViewById(R.id.btn_next);

        if (isCorrect) {
            tvResult.setText("Correct! 🎉");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvPoints.setText("+" + currentQuestion.getPoints() + " points");
            tvCorrectAnswer.setVisibility(View.GONE);
        } else {
            tvResult.setText("Wrong Answer 😞");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvPoints.setText("0 points");
            tvCorrectAnswer.setText("Correct answer: " + getCorrectAnswerText());
            tvCorrectAnswer.setVisibility(View.VISIBLE);
        }

        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            updateScoreDisplay();
            loadNextQuestion();
        });

        if (currentQuestion.getExplanation() != null && !currentQuestion.getExplanation().isEmpty()) {
            tvExplanation.setText("Explanation: " + currentQuestion.getExplanation());
            tvExplanation.setVisibility(View.VISIBLE);
        } else {
            tvExplanation.setVisibility(View.GONE);
        }

        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            updateScoreDisplay();
            timeLeftInMillis = 60000; // Reset timer
            loadNextQuestion();
        });

        dialog.show();
    }

    private String getCorrectAnswerText() {
        switch (currentQuestion.getCorrectAnswer()) {
            case 1:
                return currentQuestion.getOption1();
            case 2:
                return currentQuestion.getOption2();
            case 3:
                return currentQuestion.getOption3();
            case 4:
                return currentQuestion.getOption4();
            default:
                return "";
        }
    }

    private void updateScoreDisplay() {
        tvScore.setText("Score: " + totalScore);
    }

    private void showQuizCompleted() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_quiz_completed);
        dialog.setCancelable(false);

        TextView tvFinalScore = dialog.findViewById(R.id.tv_final_score);
        Button btnBackToMain = dialog.findViewById(R.id.btn_back_to_main);
        Button btnRetry = dialog.findViewById(R.id.btn_retry);

        tvFinalScore.setText("Quiz Completed!\nFinal Score: " + totalScore);

        btnBackToMain.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        btnRetry.setOnClickListener(v -> {
            dialog.dismiss();
            quizViewModel.resetCategoryProgress(categoryId);
            totalScore = 0;
            updateScoreDisplay();
            loadNextQuestion();
        });

        dialog.show();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimer();
                onTimeUp();
            }
        }.start();
    }

    private void updateTimer() {
        int seconds = (int) (timeLeftInMillis / 1000);
        tvTimer.setText("Time: " + seconds + "s");
    }

    private void onTimeUp() {
        wrongAnswers++;
        hearts--;
        updateHeartsDisplay();

        if (hearts <= 0) {
            showGameOver();
        } else {
            showTimeUpDialog();
        }
    }

    private void showHardQuestionReward() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_hard_question_reward);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        // Random reward
        int reward = (int) (Math.random() * 3);
        switch (reward) {
            case 0:
                hearts++;
                tvMessage.setText("Great job! You earned an extra heart! ❤️");
                break;
            case 1:
                timeLeftInMillis += 30000; // 30 extra seconds
                tvMessage.setText("Amazing! You got 30 extra seconds! ⏰");
                break;
            case 2:
                tvMessage.setText("Excellent! You get help for the next question! 💡");
                break;
        }

        btnOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showStreakReward() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_streak_reward);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        tvMessage.setText("5 correct answers in a row! Here's a gift! 🎁");
        btnOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showWrongAnswerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_wrong_answers);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnContinue = dialog.findViewById(R.id.btn_continue);

        tvMessage.setText("You've made several mistakes. Stay focused! 💪");
        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            loadNextQuestion();
        });

        dialog.show();
    }

    private void updateHeartsDisplay() {
        StringBuilder heartsText = new StringBuilder();
        for (int i = 0; i < hearts; i++) {
            heartsText.append("❤️");
        }
        tvHearts.setText(heartsText.toString());
    }

    // Add game over dialog
    private void showGameOver() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_game_over);
        dialog.setCancelable(false);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnRetry = dialog.findViewById(R.id.btn_retry);
        Button btnBack = dialog.findViewById(R.id.btn_back);

        tvMessage.setText("Game Over!\nFinal Score: " + totalScore);

        btnRetry.setOnClickListener(v -> {
            dialog.dismiss();
            resetGame();
        });

        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    // Add reset game method
    private void resetGame() {
        hearts = 3;
        wrongAnswers = 0;
        correctAnswers = 0;
        totalScore = 0;
        timeLeftInMillis = 60000;
        quizViewModel.resetCategoryProgress(categoryId);
        updateScoreDisplay();
        loadNextQuestion();
    }

    private void showClue() {
        if (!clueUsed && currentQuestion.getClue() != null && !currentQuestion.getClue().isEmpty()) {
            clueUsed = true;
            btnClue.setEnabled(false);

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_clue);

            TextView tvClue = dialog.findViewById(R.id.tv_clue);
            Button btnOk = dialog.findViewById(R.id.btn_ok);

            tvClue.setText(currentQuestion.getClue());
            btnOk.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
    }

    private void showTimeUpDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_time_up);
        dialog.setCancelable(false);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCorrectAnswer = dialog.findViewById(R.id.tv_correct_answer);
        TextView tvExplanation = dialog.findViewById(R.id.tv_explanation);
        Button btnNext = dialog.findViewById(R.id.btn_next);

        tvMessage.setText("Time's Up! ⏰");
        tvCorrectAnswer.setText("Correct answer: " + getCorrectAnswerText());

        // Show explanation if available
        if (currentQuestion.getExplanation() != null && !currentQuestion.getExplanation().isEmpty()) {
            tvExplanation.setText("Explanation: " + currentQuestion.getExplanation());
            tvExplanation.setVisibility(View.VISIBLE);
        } else {
            tvExplanation.setVisibility(View.GONE);
        }

        btnNext.setOnClickListener(v -> {
            dialog.dismiss();

            // Mark question as answered but incorrect
            currentQuestion.setAnswered(true);
            currentQuestion.setCorrect(false);
            quizViewModel.updateQuestion(currentQuestion);

            updateScoreDisplay();
            timeLeftInMillis = 60000; // Reset timer for next question
            loadNextQuestion();
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
