package ditd.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm392_minigames.R;

import java.util.concurrent.Executors;

import ditd.db.AppDatabase;
import ditd.model.Question;

public class ImageGameQuestionResultActivity extends AppCompatActivity {

    private TextView tvExplanation;
    private Button btnNextQuestion;
    private Question currentQuestion;
    private int questionSize;
    private int questionId;
    private int currentIndex;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_question_result);

        tvExplanation = findViewById(R.id.tvExplanation);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);

        db = AppDatabase.getInstance(getApplicationContext());
         questionId = getIntent().getIntExtra("questionId", -1);
         currentIndex = getIntent().getIntExtra("currentIndex", 0);
        questionSize = getIntent().getIntExtra("questionSize", 0);



        ScrollView layout = findViewById(R.id.image_game_question_result);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

        Executors.newSingleThreadExecutor().execute(() -> {
            Question q = db.imageGameQuestionDao().getById(questionId);
            runOnUiThread(() -> {
                currentQuestion = q;
                tvExplanation.setText(q.explanation);
            });
        });

        btnNextQuestion.setOnClickListener(v -> {
            if (currentIndex + 1 < questionSize) {
                Intent intent = new Intent(ImageGameQuestionResultActivity.this, ImageGamePlayActivity.class);
                intent.putExtra("questionIndex", currentIndex + 1);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ImageGameQuestionResultActivity.this, "Hết rồi!", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
