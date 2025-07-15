package ditd.activity;

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

    private RecyclerView rvQuestionImages;
    private LinearLayout optionsContainer;
    private Button btnNextQuestion;
    private TextView tvHint;
    private TextView tvCategoryName;
    private AppDatabase db;
    private ImageGameQuestionDao imageGameQuestionDao;
    private ImageGameCategoryDao imageGameCategoryDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_activity_play);

        rvQuestionImages = findViewById(R.id.rvQuestionImages);
        optionsContainer = findViewById(R.id.optionsContainer);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        tvHint = findViewById(R.id.tvHint);
        tvCategoryName = findViewById(R.id.tvCategoryName);

        Executors.newSingleThreadExecutor().execute(() -> {
            db = AppDatabase.getInstance(getApplicationContext());
            imageGameQuestionDao = db.imageGameQuestionDao();
            imageGameCategoryDao = db.imageGameCategoryDao();
            questionList = imageGameQuestionDao.getAll();

            runOnUiThread(() -> showQuestion());
        });

        btnNextQuestion.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                showQuestion();
            } else {
                Toast.makeText(this, "You've completed all questions!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showQuestion() {
        Question q = questionList.get(currentQuestionIndex);

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
            btnOption.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            btnOption.setOnClickListener(v -> {
                boolean isCorrect = option.equals(q.correctAnswer);
                Toast.makeText(this, isCorrect ? "Correct!" : "Wrong!", Toast.LENGTH_SHORT).show();
            });
            optionsContainer.addView(btnOption);
        }
    }
    public class HelpManager {
        private boolean hintUsed = false;
        private boolean skipUsed = false;
        private boolean used5050 = false;

        public boolean canUseHint() {
            return !hintUsed;
        }

        public boolean canUseSkip() {
            return !skipUsed;
        }

        public boolean canUse5050() {
            return !used5050;
        }

        public void useHint() {
            hintUsed = true;
        }

        public void useSkip() {
            skipUsed = true;
        }

        public void use5050() {
            used5050 = true;
        }
    }
}
