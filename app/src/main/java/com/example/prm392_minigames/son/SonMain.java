package com.example.prm392_minigames.son;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.activities.QuizActivity;
import com.example.prm392_minigames.son.activities.ScoreActivity;
import com.example.prm392_minigames.son.adapters.CategoryAdapter;
import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.entities.Question;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;

public class SonMain extends AppCompatActivity {
    private QuizViewModel quizViewModel;
    private CategoryAdapter categoryAdapter;
    private EditText etSearch;
    private Spinner spinnerSort;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.son_main);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupSearchAndSort();
        maybePopulateDatabase();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        spinnerSort = findViewById(R.id.spinner_sort);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);

        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category category) {
                Intent intent = new Intent(SonMain.this, QuizActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                intent.putExtra("CATEGORY_NAME", category.getName());
                startActivity(intent);
            }

            @Override
            public void onViewScoreClick(Category category) {
                Intent intent = new Intent(SonMain.this, ScoreActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                intent.putExtra("CATEGORY_NAME", category.getName());
                startActivity(intent);
            }
        });
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        quizViewModel.getAllCategories().observe(this, categories -> {
            categoryAdapter.setCategories(categories);
        });
    }

    private void setupSearchAndSort() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    quizViewModel.getAllCategories().observe(SonMain.this, categories -> {
                        categoryAdapter.setCategories(categories);
                    });
                } else {
                    quizViewModel.searchCategories(s.toString()).observe(SonMain.this, categories -> {
                        categoryAdapter.setCategories(categories);
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        String[] sortOptions = {"Name", "Questions", "Score"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        quizViewModel.getCategoriesSortedByName().observe(SonMain.this, categories -> {
                            categoryAdapter.setCategories(categories);
                        });
                        break;
                    case 1:
                        quizViewModel.getCategoriesSortedByQuestions().observe(SonMain.this, categories -> {
                            categoryAdapter.setCategories(categories);
                        });
                        break;
                    case 2:
                        quizViewModel.getCategoriesSortedByScore().observe(SonMain.this, categories -> {
                            categoryAdapter.setCategories(categories);
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void maybePopulateDatabase() {
        quizViewModel.getAllCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                populateDatabase();
            }
        });
    }

    private void populateDatabase() {
        Category math = new Category("Mathematics", "Test your math skills");
        math.setTotalQuestions(3);
        math.setMaxScore(30);
        quizViewModel.insertCategory(math);

        Category science = new Category("Science", "General science questions");
        science.setTotalQuestions(2);
        science.setMaxScore(20);
        quizViewModel.insertCategory(science);

        Category history = new Category("History", "World history quiz");
        history.setTotalQuestions(2);
        history.setMaxScore(25);
        quizViewModel.insertCategory(history);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                quizViewModel.insertQuestion(new Question(1, "What is 2 + 2?", "3", "4", "5", "6", 2, 10));
                quizViewModel.insertQuestion(new Question(1, "What is 10 * 5?", "50", "45", "55", "60", 1, 10));
                quizViewModel.insertQuestion(new Question(1, "What is the square root of 16?", "3", "4", "5", "6", 2, 10));
                quizViewModel.insertQuestion(new Question(2, "What is the chemical symbol for water?", "H2O", "CO2", "O2", "H2", 1, 10));
                quizViewModel.insertQuestion(new Question(2, "How many planets are in our solar system?", "7", "8", "9", "10", 2, 10));
                quizViewModel.insertQuestion(new Question(3, "In which year did World War II end?", "1944", "1945", "1946", "1947", 2, 15));
                quizViewModel.insertQuestion(new Question(3, "Who was the first President of the United States?", "Thomas Jefferson", "George Washington", "John Adams", "Benjamin Franklin", 2, 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
