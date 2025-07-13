package com.example.prm392_minigames.son.activities;

import android.app.Dialog;
import android.os.Bundle;
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

    private void loadNextQuestion() {
        quizViewModel.getNextUnansweredQuestion(categoryId, new QuizRepository.OnQuestionLoadedListener() {
            @Override
            public void onQuestionLoaded(Question question) {
                if (question != null) {
                    currentQuestion = question;
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
    }

    private void onOptionSelected(int selectedOption) {
        boolean isCorrect = selectedOption == currentQuestion.getCorrectAnswer();

        currentQuestion.setAnswered(true);
        currentQuestion.setCorrect(isCorrect);

        if (isCorrect) {
            totalScore += currentQuestion.getPoints();
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
}
