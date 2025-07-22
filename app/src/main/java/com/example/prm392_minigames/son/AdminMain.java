package com.example.prm392_minigames.son;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.activities.AdminCategoryActivity;
import com.example.prm392_minigames.son.activities.AdminQuestionActivity;

public class AdminMain extends AppCompatActivity {
    private Button btnManageCategories;
    private Button btnManageQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnManageCategories = findViewById(R.id.btn_manage_categories);
        btnManageQuestions = findViewById(R.id.btn_manage_questions);
    }

    private void setupClickListeners() {
        btnManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMain.this, AdminCategoryActivity.class);
            startActivity(intent);
        });

        btnManageQuestions.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMain.this, AdminQuestionActivity.class);
            startActivity(intent);
        });
    }
}