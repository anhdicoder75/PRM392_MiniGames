package com.example.prm392_minigames.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.adapters.MiniGameAdapter;
import com.example.prm392_minigames.son.SonMain;

import android.graphics.drawable.AnimationDrawable;
import com.example.prm392_minigames.hangmangame.HangmanMainActivity;
import namnq.activity.SoundGameLobbyActivity;
import com.example.prm392_minigames.hangmangame.db.AppDatabaseHelper;
import com.example.prm392_minigames.models.MiniGame;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import ditd.activity.ImageGamePlayActivity;

public class MainActivity extends AppCompatActivity implements MiniGameAdapter.OnGameClickListener {
    ImageView imgAvatar, imgFrame;
    TextView tvWelcome, tvPoint;
    Button btnShop, btnProfile;
    RecyclerView rvGames;

    String avatarUri = null;
    int frameId = 0, point = 0;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgAvatar = findViewById(R.id.img_avatar);
        imgFrame = findViewById(R.id.img_frame);
        tvWelcome = findViewById(R.id.tv_welcome);
        tvPoint = findViewById(R.id.tv_point);
        btnShop = findViewById(R.id.btn_shop);
        btnProfile = findViewById(R.id.btn_profile);
        rvGames = findViewById(R.id.rv_games);

        // Setup RecyclerView - danh sách mini game
        List<MiniGame> games = Arrays.asList(
                new MiniGame(R.drawable.ic_quiz, "Trắc nghiệm kiến thức",
                        "Quiz game hỏi đáp, nhiều câu, tổng kết điểm."),
                new MiniGame(R.drawable.ic_brush, "Trí nhớ!", "Bạn có thể lật nhanh hơn ai!"),
                new MiniGame(R.drawable.ic_volume, "Sound Guess", "Nghe âm thanh, chọn đáp án đúng, có bảng xếp hạng."),
                new MiniGame(R.drawable.ic_image, "Image Guess", "Đoán hình ảnh: chọn đúng đáp án từ ảnh random."),
                new MiniGame(R.drawable.ic_hangman, "Hangman", "Đoán chữ cái, có hint, sai nhiều thua, trừ điểm."));
        // MiniGameAdapter adapter = new MiniGameAdapter(games, position -> {
        // Toast.makeText(this, "Chức năng game này sẽ sớm mở!",
        // Toast.LENGTH_SHORT).show();
        // });

        MiniGameAdapter adapter = new MiniGameAdapter(games, position -> {
            switch (position) {
                case 0:
                    // Quiz Game - chưa có
                    startActivity(new Intent(this, SonMain.class));
                    break;
                case 1:
                    startActivity(new Intent(this, MemoryGameActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(this, SoundGameLobbyActivity.class));
                    break;
                case 3:
                    // Image Guess Game - chưa có
                    Toast.makeText(this, "Chức năng game này sẽ sớm mở!", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    startActivity(new Intent(this, HangmanMainActivity.class));
                    break;
                default:
                    Toast.makeText(this, "Chức năng game này sẽ sớm mở!", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        rvGames.setLayoutManager(new LinearLayoutManager(this));
        rvGames.setAdapter(adapter);

        btnShop.setOnClickListener(v -> startActivity(new Intent(this, ShopActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.fab_sync).setOnClickListener(v -> startActivity(new Intent(this, SyncActivity.class)));

        loadProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    @Override
    public void onGameClick(int position) {
        Toast.makeText(this, "Chức năng game này sẽ sớm mở!", Toast.LENGTH_SHORT).show();
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
            c.close();
        } else {
            tvWelcome.setText("Xin chào!");
            tvPoint.setText("Điểm: 0");
            imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            imgFrame.setImageResource(R.drawable.ic_frame_default);
            if (c != null) {
                c.close();
            }
        }
    }

    private void setFrameIcon(int frameId) {
        switch (frameId) {
            case 1:
                imgFrame.setImageResource(R.drawable.ic_frame_gold_anim);
                break;
            case 2:
                imgFrame.setImageResource(R.drawable.ic_frame_rainbow_anim);
                break;
            default:
                imgFrame.setImageResource(R.drawable.ic_frame_default);
                break;
        }
    }
}