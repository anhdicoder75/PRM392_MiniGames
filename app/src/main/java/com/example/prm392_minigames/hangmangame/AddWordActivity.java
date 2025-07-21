package com.example.prm392_minigames.hangmangame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.R;

public class AddWordActivity extends AppCompatActivity {
    private GameDatabaseHelper dbHelper;
    private EditText etNewWord, etNewHint;
    private Button btnSaveWord, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        dbHelper = new GameDatabaseHelper(this);
        initializeViews();
    }

    private void initializeViews() {
        etNewWord = findViewById(R.id.etNewWord);
        etNewHint = findViewById(R.id.etNewHint);
        btnSaveWord = findViewById(R.id.btnSaveWord);
        btnBack = findViewById(R.id.btnBack);

        btnSaveWord.setOnClickListener(v -> saveWord());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveWord() {
        String word = etNewWord.getText().toString().trim().toUpperCase();
        String hint = etNewHint.getText().toString().trim();

        if (word.isEmpty() || hint.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập cả từ và gợi ý!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!word.matches("[A-Z]+")) {
            Toast.makeText(this, "Từ chỉ được chứa chữ cái A-Z!", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addWord(word, hint);
        if (result != -1) {
            Toast.makeText(this, "Đã thêm từ thành công!", Toast.LENGTH_SHORT).show();
            etNewWord.setText("");
            etNewHint.setText("");
        } else {
            Toast.makeText(this, "Lỗi khi thêm từ!", Toast.LENGTH_SHORT).show();
        }
    }
}