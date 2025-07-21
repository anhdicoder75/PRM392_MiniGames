package com.example.prm392_minigames.son.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.adapters.QuestionScoreAdapter;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;

//import com.example.quizapp.adapters.QuestionScoreAdapter;
//import com.example.quizapp.viewmodel.QuizViewModel;

public class ScoreActivity extends AppCompatActivity {
    private QuizViewModel quizViewModel;
    private TextView tvCategoryName;
    private TextView tvTotalScore;
    private RecyclerView recyclerViewQuestions;
    private QuestionScoreAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        initViews();
        setupViewModel();
        loadScoreData();
    }

    private void initViews() {
        tvCategoryName = findViewById(R.id.tv_category_name);
        tvTotalScore = findViewById(R.id.tv_total_score);
        recyclerViewQuestions = findViewById(R.id.recycler_view_questions);
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
    }

    private void loadScoreData() {
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        tvCategoryName.setText(categoryName);

        questionAdapter = new QuestionScoreAdapter();
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewQuestions.setAdapter(questionAdapter);

        quizViewModel.getQuestionsByCategory(categoryId).observe(this, questions -> {
            questionAdapter.setQuestions(questions);

            // Calculate total score
            int totalScore = 0;
            int maxScore = 0;
            for (com.example.prm392_minigames.son.entities.Question question : questions) {
                maxScore += question.getPoints();
                if (question.isCorrect()) {
                    totalScore += question.getPoints();
                }
            }

            tvTotalScore.setText("Total Score: " + totalScore + "/" + maxScore);
        });
    }
}
