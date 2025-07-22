package com.example.prm392_minigames.son.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.adapters.AdminCategoryAdapter;
import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminCategoryActivity extends AppCompatActivity {
    private QuizViewModel quizViewModel;
    private AdminCategoryAdapter categoryAdapter;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupSearch();
        setupFab();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        recyclerView = findViewById(R.id.recycler_view);
        fabAdd = findViewById(R.id.fab_add);
    }

    private void setupRecyclerView() {
        categoryAdapter = new AdminCategoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);

        categoryAdapter.setOnItemClickListener(new AdminCategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Category category) {
                Intent intent = new Intent(AdminCategoryActivity.this, AddEditCategoryActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Category category) {
                quizViewModel.deleteCategory(category);
            }
        });
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.getAllCategories().observe(this, categories -> {
            categoryAdapter.setCategories(categories);
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    quizViewModel.getAllCategories().observe(AdminCategoryActivity.this, categories -> {
                        categoryAdapter.setCategories(categories);
                    });
                } else {
                    quizViewModel.searchCategories(s.toString()).observe(AdminCategoryActivity.this, categories -> {
                        categoryAdapter.setCategories(categories);
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AdminCategoryActivity.this, AddEditCategoryActivity.class);
            startActivity(intent);
        });
    }
}