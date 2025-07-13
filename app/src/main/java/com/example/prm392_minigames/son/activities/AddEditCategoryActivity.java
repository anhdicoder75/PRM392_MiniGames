package com.example.prm392_minigames.son.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;

public class AddEditCategoryActivity extends AppCompatActivity {
    private EditText etCategoryName;
    private EditText etCategoryDescription;
    private Button btnSave;
    private Button btnCancel;

    private QuizViewModel quizViewModel;
    private Category currentCategory;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        initViews();
        setupViewModel();
        checkEditMode();
        setupClickListeners();
    }

    private void initViews() {
        etCategoryName = findViewById(R.id.et_category_name);
        etCategoryDescription = findViewById(R.id.et_category_description);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
    }

    private void checkEditMode() {
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        if (categoryId != -1) {
            isEditMode = true;
            setTitle("Edit Category");
            loadCategory(categoryId);
        } else {
            setTitle("Add Category");
        }
    }

    private void loadCategory(int categoryId) {
        // You'll need to add this method to your repository and ViewModel
        new Thread(() -> {
            currentCategory = quizViewModel.getCategoryById(categoryId);
            runOnUiThread(() -> {
                if (currentCategory != null) {
                    etCategoryName.setText(currentCategory.getName());
                    etCategoryDescription.setText(currentCategory.getDescription());
                }
            });
        }).start();
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveCategory());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveCategory() {
        String name = etCategoryName.getText().toString().trim();
        String description = etCategoryDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etCategoryName.setError("Category name is required");
            return;
        }

        if (isEditMode && currentCategory != null) {
            currentCategory.setName(name);
            currentCategory.setDescription(description);
            quizViewModel.updateCategory(currentCategory);
            Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Category newCategory = new Category(name, description);
            quizViewModel.insertCategory(newCategory);
            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}