package com.example.prm392_minigames.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.R;

import ditd.activity.ImageGameAddQuestionsActivity;
import ditd.activity.ImageGameCategoryActivity;
import ditd.activity.ImageGameQuestionListActivity;

public class AdminImageCrudActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_crud); // Gán layout ở đây

        Button btnManageCategory = findViewById(R.id.btnManageCategory);
        Button btnManageImage = findViewById(R.id.btnManageImageQuestions);

        btnManageCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGameCategoryActivity.class); // Class quản lý category
            startActivity(intent);
        });

        btnManageImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGameQuestionListActivity.class); // Class quản lý câu hỏi
            startActivity(intent);
        });
    }
}
