package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.db.AppDatabaseHelper;
import com.example.prm392_minigames.models.MiniGame;
import com.example.prm392_minigames.adapters.MiniGameAdapter;
import android.graphics.drawable.AnimationDrawable;

import java.util.*;

public class MainActivity extends Activity {
    ImageView imgAvatar, imgFrame;
    TextView tvWelcome, tvPoint;
    Button btnShop, btnProfile;
    RecyclerView rvGames;

    String avatarUri = null;
    int frameId = 0, point = 0;
    String name = "";

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgAvatar = findViewById(R.id.img_avatar);
        imgFrame = findViewById(R.id.img_frame);
        tvWelcome = findViewById(R.id.tv_welcome);
        tvPoint = findViewById(R.id.tv_point);
        btnShop = findViewById(R.id.btn_shop);
        btnProfile = findViewById(R.id.btn_profile);
        rvGames = findViewById(R.id.rv_games);

        // Setup RecyclerView - danh sách mini game
        List<MiniGame> games = Arrays.asList(
                new MiniGame(R.drawable.ic_quiz, "Trắc nghiệm kiến thức", "Quiz game hỏi đáp, nhiều câu, tổng kết điểm."),
                new MiniGame(R.drawable.ic_brush, "Trí nhớ!", "Bạn có thể lật nhanh hơn ai!"),
                new MiniGame(R.drawable.ic_volume, "Sound Guess", "Nghe âm thanh, chọn đáp án đúng, có bảng xếp hạng."),
                new MiniGame(R.drawable.ic_image, "Image Guess", "Đoán hình ảnh: chọn đúng đáp án từ ảnh random."),
                new MiniGame(R.drawable.ic_hangman, "Hangman", "Đoán chữ cái, có hint, sai nhiều thua, trừ điểm.")
        );
        MiniGameAdapter adapter = new MiniGameAdapter(games, position -> {
            if (position == 1) { // Memory game
                startActivity(new Intent(this, MemoryGameActivity.class));
            } else {
                Toast.makeText(this, "Chức năng game này sẽ sớm mở!", Toast.LENGTH_SHORT).show();
            }
        });
        rvGames.setLayoutManager(new LinearLayoutManager(this));
        rvGames.setAdapter(adapter);

        btnShop.setOnClickListener(v -> startActivity(new Intent(this, ShopActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.fab_sync).setOnClickListener(v -> startActivity(new Intent(this, SyncActivity.class)));

        loadProfile();
    }

    private void loadProfile() {
        AppDatabaseHelper db = new AppDatabaseHelper(this);
        Cursor c = db.getProfile();
        if (c != null && c.moveToFirst()) {
            name = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_NAME));
            avatarUri = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_AVATAR));
            frameId = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_FRAME));
            point = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
            tvWelcome.setText("Xin chào, " + name + "!");
            tvPoint.setText("Điểm: " + point);
            if (avatarUri != null) {
                imgAvatar.setImageURI(Uri.parse(avatarUri));
            } else {
                imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            }
            setFrameIcon(frameId);
        } else {
            tvWelcome.setText("Xin chào!");
            tvPoint.setText("Điểm: 0");
            imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            imgFrame.setImageResource(R.drawable.ic_frame_default);
        }
    }

    private void setFrameIcon(int frameId) {
        if (frameId == 1) {
            imgFrame.setImageResource(R.drawable.ic_frame_gold_anim);
            AnimationDrawable anim = (AnimationDrawable) imgFrame.getDrawable();
            anim.start();
        } else if (frameId == 2) {
            imgFrame.setImageResource(R.drawable.ic_frame_rainbow_anim);
            AnimationDrawable anim = (AnimationDrawable) imgFrame.getDrawable();
            anim.start();
        } else {
            imgFrame.setImageResource(R.drawable.ic_frame_default);
        }
    }
}
