package namnq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.concurrent.Executors;

import namnq.adapter.SoundQuestionAdapter;
import namnq.dao.SoundGameQuestionDao;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundQuestion;

public class ManageQuestionsActivity extends AppCompatActivity {

    private RecyclerView rvQuestions;
    private SoundGameQuestionDao questionDao;
    private List<SoundQuestion> questionList;
    private SoundQuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_questions);

        rvQuestions = findViewById(R.id.rvQuestions);

        SoundAppDatabase db = SoundAppDatabase.getInstance(this);
        questionDao = db.soundGameQuestionDao();

        loadQuestions();

        findViewById(R.id.btnAddQuestion).setOnClickListener(v ->
                startActivity(new Intent(this, SoundGameAddQuestionsActivity.class))
        );
    }

    private void loadQuestions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            questionList = questionDao.getAll();

            runOnUiThread(() -> {
                adapter = new SoundQuestionAdapter(this, questionList, new SoundQuestionAdapter.OnQuestionActionListener() {
                    @Override
                    public void onUpdate(SoundQuestion question) {
                        Intent i = new Intent(ManageQuestionsActivity.this, SoundGameAddQuestionsActivity.class);
                        i.putExtra("questionId", question.id);
                        startActivity(i);
                    }

                    @Override
                    public void onDelete(SoundQuestion question) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            questionDao.delete(question);
                            runOnUiThread(() -> {
                                Toast.makeText(ManageQuestionsActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                loadQuestions(); // refresh list
                            });
                        });
                    }
                });

                rvQuestions.setLayoutManager(new LinearLayoutManager(this));
                rvQuestions.setAdapter(adapter);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions();
    }
}
