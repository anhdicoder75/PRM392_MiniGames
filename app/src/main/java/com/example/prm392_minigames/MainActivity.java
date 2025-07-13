package com.example.prm392_minigames;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_minigames.son.AdminMain;
import com.example.prm392_minigames.son.SonMain;

public class MainActivity extends AppCompatActivity {

    private Button btnPart1, btnPart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnPart1 = findViewById(R.id.btnPart1);

        btnPart2 = findViewById(R.id.btnPart2);

        btnPart1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SonMain.class);
            startActivity(intent);
        });

        btnPart2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminMain.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}