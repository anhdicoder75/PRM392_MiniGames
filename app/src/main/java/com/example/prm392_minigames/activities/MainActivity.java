package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.database.Cursor;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.db.AppDatabaseHelper;

public class MainActivity extends Activity {
    TextView tvWelcome;
    Button btnProfile, btnSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tv_welcome);
        btnProfile = findViewById(R.id.btn_profile);
        btnSync = findViewById(R.id.btn_sync);

        // Hiển thị tên người dùng nếu có
        AppDatabaseHelper db = new AppDatabaseHelper(this);
        Cursor c = db.getProfile();
        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_NAME));
            tvWelcome.setText("Xin chào, " + name + "!");
        } else {
            tvWelcome.setText("Xin chào!");
        }

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        btnSync.setOnClickListener(v -> {
            startActivity(new Intent(this, SyncActivity.class));
        });
    }
}
