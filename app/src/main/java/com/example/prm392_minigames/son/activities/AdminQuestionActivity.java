package com.example.prm392_minigames.son.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.adapters.AdminQuestionAdapter;
import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.entities.Question;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminQuestionActivity extends AppCompatActivity {
    private QuizViewModel quizViewModel;
    private AdminQuestionAdapter questionAdapter;
    private Spinner spinnerCategory;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    private List<Category> categories = new ArrayList<>();
    private int selectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_question);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupSpinner();
        setupFab();
    }

    private void initViews() {
        spinnerCategory = findViewById(R.id.spinner_category);
        recyclerView = findViewById(R.id.recycler_view);
        fabAdd = findViewById(R.id.fab_add);
    }

    private void setupRecyclerView() {
        questionAdapter = new AdminQuestionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(questionAdapter);

        questionAdapter.setOnItemClickListener(new AdminQuestionAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Question question) {
                Intent intent = new Intent(AdminQuestionActivity.this, AddEditQuestionActivity.class);
                intent.putExtra("QUESTION_ID", question.getId());
                intent.putExtra("CATEGORY_ID", question.getCategoryId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Question question) {
                quizViewModel.deleteQuestion(question);
            }
        });
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        quizViewModel.getAllCategories().observe(this, categories -> {
            this.categories = categories;
            setupCategorySpinner();
        });
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select Category");

        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupSpinner() {
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= categories.size()) {
                    selectedCategoryId = categories.get(position - 1).getId();
                    loadQuestions(selectedCategoryId);
                } else {
                    selectedCategoryId = -1;
                    questionAdapter.setQuestions(new ArrayList<>());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadQuestions(int categoryId) {
        quizViewModel.getQuestionsByCategory(categoryId).observe(this, questions -> {
            questionAdapter.setQuestions(questions);
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            if (selectedCategoryId != -1) {
                Intent intent = new Intent(AdminQuestionActivity.this, AddEditQuestionActivity.class);
                intent.putExtra("CATEGORY_ID", selectedCategoryId);
                startActivity(intent);
            } else {
                // Show toast to select category first
                android.widget.Toast.makeText(this, "Please select a category first",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}