package ditd.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_minigames.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ditd.adapter.ImagePreviewAdapter;
import ditd.db.AppDatabase;
import ditd.model.Question;

public class ImageGameDetailQuestionActivity extends AppCompatActivity {

    private TextView txtQuestionId, txtAnswer, txtCategory, txtQuestionText,
            txtOptions, txtDifficulty, txtHint, txtExplanation, txtTimeLimit;
    private ImageView imageView;
    private Button btnEdit, btnDelete;
    private RecyclerView recyclerViewImages;
    private Question question;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_question_detail);

        txtQuestionId = findViewById(R.id.txtQuestionId);
        txtAnswer = findViewById(R.id.txtAnswer);
        txtCategory = findViewById(R.id.txtCategory);
        txtQuestionText = findViewById(R.id.txtQuestionText);
        txtOptions = findViewById(R.id.txtOptions);
        txtDifficulty = findViewById(R.id.txtDifficulty);
        txtHint = findViewById(R.id.txtHint);
        txtExplanation = findViewById(R.id.txtExplanation);
        txtTimeLimit = findViewById(R.id.txtTimeLimit);

        imageView = findViewById(R.id.imageView);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);

        if (recyclerViewImages == null) {
            Log.e("DEBUG", "recyclerViewImages is NULL!");
            return;
        }

        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        db = AppDatabase.getInstance(this);

        int questionId = getIntent().getIntExtra("question_id", -1);
        if (questionId != -1) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                question = db.imageGameQuestionDao().getById(questionId);
                runOnUiThread(() -> {
                    if (question != null) {
                        loadData();
                    } else {
                        Toast.makeText(this, "Question not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            });
        }

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGameEditQuestionActivity.class);
            intent.putExtra("question_id", question.id);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this question?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.imageGameQuestionDao().delete(question);
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadData() {
        txtQuestionId.setText("ID: " + question.id);
        txtAnswer.setText("Answer: " + question.correctAnswer);
        txtCategory.setText("Category ID: " + question.categoryId);
        txtQuestionText.setText("Question: " + question.questionText);
        txtDifficulty.setText("Difficulty: " + question.difficulty);
        txtHint.setText("Hint: " + question.hint);
        txtExplanation.setText("Explanation: " + question.explanation);
        txtTimeLimit.setText("Time Limit: " + question.timeLimitSeconds + "s");

        if (question.options != null && !question.options.isEmpty()) {
            txtOptions.setText("Options:\n- " + String.join("\n- ", question.options));
        }

        if (question.imageUrls != null && !question.imageUrls.isEmpty()) {
            Glide.with(this).load(question.imageUrls.get(0)).into(imageView);
            ImagePreviewAdapter adapter = new ImagePreviewAdapter(this, question.imageUrls);
            recyclerViewImages.setAdapter(adapter);
        }
    }
}
