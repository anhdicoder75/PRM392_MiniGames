package ditd.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import ditd.adapter.ImageGameQuestionAdapter;
import ditd.db.AppDatabase;
import ditd.model.Question;

public class ImageGameQuestionListActivity extends AppCompatActivity {
    private RecyclerView rvQuestions;
    private ImageGameQuestionAdapter adapter;
    private List<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_activity_question_list);
        rvQuestions = findViewById(R.id.rvQuestions);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvQuestions.addItemDecoration(dividerItemDecoration);
        adapter = new ImageGameQuestionAdapter(questionList, new ImageGameQuestionAdapter.OnQuestionActionListener() {
            @Override
            public void onEdit(Question question) {
                Intent intent = new Intent(ImageGameQuestionListActivity.this, ImageGameEditQuestionActivity.class);
                intent.putExtra("question_id", question.id);
                startActivity(intent);
            }

            @Override
            public void onDelete(Question question) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getInstance(getApplicationContext()).imageGameQuestionDao().delete(question);
                    loadData();
                });
            }

            @Override
            public void onDetail(Question question) {
                Intent intent = new Intent(ImageGameQuestionListActivity.this, ImageGameDetailQuestionActivity.class);
                intent.putExtra("question_id", question.id);
                startActivity(intent);
            }
        });
        rvQuestions.setAdapter(adapter);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Question> list = AppDatabase.getInstance(getApplicationContext()).imageGameQuestionDao().getAll();
            runOnUiThread(() -> adapter.updateList(list));
        });
    }
}
