package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.prm392_minigames.hangmangame.db.AppDatabaseHelper;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabaseHelper db = new AppDatabaseHelper(this);
        Cursor cursor = db.getProfile();
        if (cursor != null && cursor.moveToFirst()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        finish();
    }
}
