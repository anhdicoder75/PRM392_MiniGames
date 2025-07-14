package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.db.AppDatabaseHelper;

public class ProfileActivity extends Activity {
    EditText edtName;
    Button btnContinue, btnSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edt_name);
        btnContinue = findViewById(R.id.btn_continue);
        btnSync = findViewById(R.id.btn_sync);

        btnContinue.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Nhập tên trước nhé!", Toast.LENGTH_SHORT).show();
                return;
            }
            AppDatabaseHelper db = new AppDatabaseHelper(this);
            db.insertProfile(name);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnSync.setOnClickListener(v -> {
            startActivity(new Intent(this, SyncActivity.class));
            finish();
        });
    }
}
