package com.example.prm392_minigames.son.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.entities.Question;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;

public class AddEditQuestionActivity extends AppCompatActivity {
    private EditText etQuestionText;
    private EditText etOption1;
    private EditText etOption2;
    private EditText etOption3;
    private EditText etOption4;
    private EditText etPoints;
    private RadioGroup radioGroupCorrect;
    private RadioButton rbOption1;
    private RadioButton rbOption2;
    private RadioButton rbOption3;
    private RadioButton rbOption4;
    private Button btnSave;
    private Button btnCancel;

    private QuizViewModel quizViewModel;
    private Question currentQuestion;
    private boolean isEditMode = false;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_question);

        initViews();
        setupViewModel();
        checkEditMode();
        setupClickListeners();
    }

    private void initViews() {
        etQuestionText = findViewById(R.id.et_question_text);
        etOption1 = findViewById(R.id.et_option1);
        etOption2 = findViewById(R.id.et_option2);
        etOption3 = findViewById(R.id.et_option3);
        etOption4 = findViewById(R.id.et_option4);
        etPoints = findViewById(R.id.et_points);
        radioGroupCorrect = findViewById(R.id.radio_group_correct);
        rbOption1 = findViewById(R.id.rb_option1);
        rbOption2 = findViewById(R.id.rb_option2);
        rbOption3 = findViewById(R.id.rb_option3);
        rbOption4 = findViewById(R.id.rb_option4);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
    }

    private void checkEditMode() {
        categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        int questionId = getIntent().getIntExtra("QUESTION_ID", -1);

        if (questionId != -1) {
            isEditMode = true;
            setTitle("Edit Question");
            loadQuestion(questionId);
        } else {
            setTitle("Add Question");
        }
    }

    private void loadQuestion(int questionId) {
        // You'll need to add this method to your repository and ViewModel
        new Thread(() -> {
            currentQuestion = quizViewModel.getQuestionById(questionId);
            runOnUiThread(() -> {
                if (currentQuestion != null) {
                    etQuestionText.setText(currentQuestion.getQuestionText());
                    etOption1.setText(currentQuestion.getOption1());
                    etOption2.setText(currentQuestion.getOption2());
                    etOption3.setText(currentQuestion.getOption3());
                    etOption4.setText(currentQuestion.getOption4());
                    etPoints.setText(String.valueOf(currentQuestion.getPoints()));

                    switch (currentQuestion.getCorrectAnswer()) {
                        case 1:
                            rbOption1.setChecked(true);
                            break;
                        case 2:
                            rbOption2.setChecked(true);
                            break;
                        case 3:
                            rbOption3.setChecked(true);
                            break;
                        case 4:
                            rbOption4.setChecked(true);
                            break;
                    }
                }
            });
        }).start();
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveQuestion());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveQuestion() {
        String questionText = etQuestionText.getText().toString().trim();
        String option1 = etOption1.getText().toString().trim();
        String option2 = etOption2.getText().toString().trim();
        String option3 = etOption3.getText().toString().trim();
        String option4 = etOption4.getText().toString().trim();
        String pointsStr = etPoints.getText().toString().trim();

        if (questionText.isEmpty()) {
            etQuestionText.setError("Question text is required");
            return;
        }

        if (option1.isEmpty() || option2.isEmpty() || option3.isEmpty() || option4.isEmpty()) {
            Toast.makeText(this, "All options are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pointsStr.isEmpty()) {
            etPoints.setError("Points are required");
            return;
        }

        int points;
        try {
            points = Integer.parseInt(pointsStr);
        } catch (NumberFormatException e) {
            etPoints.setError("Invalid points value");
            return;
        }

        int correctAnswer = 0;
        int selectedId = radioGroupCorrect.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_option1) correctAnswer = 1;
        else if (selectedId == R.id.rb_option2) correctAnswer = 2;
        else if (selectedId == R.id.rb_option3) correctAnswer = 3;
        else if (selectedId == R.id.rb_option4) correctAnswer = 4;

        if (correctAnswer == 0) {
            Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode && currentQuestion != null) {
            currentQuestion.setQuestionText(questionText);
            currentQuestion.setOption1(option1);
            currentQuestion.setOption2(option2);
            currentQuestion.setOption3(option3);
            currentQuestion.setOption4(option4);
            currentQuestion.setCorrectAnswer(correctAnswer);
            currentQuestion.setPoints(points);
            quizViewModel.updateQuestion(currentQuestion);
            Toast.makeText(this, "Question updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Question newQuestion = new Question(categoryId, questionText, option1, option2,
                    option3, option4, correctAnswer, points);
            quizViewModel.insertQuestion(newQuestion);
            Toast.makeText(this, "Question added successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}