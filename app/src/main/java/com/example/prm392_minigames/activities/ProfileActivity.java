package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import android.graphics.drawable.AnimationDrawable;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.hangmangame.db.AppDatabaseHelper;

public class ProfileActivity extends Activity {
    private static final int REQ_PICK_IMAGE = 1200;

    EditText edtName;
    Button btnContinue, btnSync, btnChooseAvatar;
    ImageView imgAvatar, imgFrame;
    TextView tvHello, tvPoint;
    String avatarUri = null;
    int frameId = 0, point = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edt_name);
        btnContinue = findViewById(R.id.btn_continue);
        btnSync = findViewById(R.id.btn_sync);
        btnChooseAvatar = findViewById(R.id.btn_choose_avatar);
        imgAvatar = findViewById(R.id.img_avatar);
        imgFrame = findViewById(R.id.img_frame);
        tvHello = findViewById(R.id.tv_hello);
        tvPoint = findViewById(R.id.tv_point);

        AppDatabaseHelper db = new AppDatabaseHelper(this);
        Cursor c = db.getProfile();
        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_NAME));
            avatarUri = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_AVATAR));
            frameId = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_FRAME));
            point = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
            edtName.setText(name);
            tvHello.setText("Chỉnh sửa hồ sơ");
            tvPoint.setText("Điểm: " + point);
            if (avatarUri != null) {
                imgAvatar.setImageURI(Uri.parse(avatarUri));
            } else {
                imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            }
            setFrameIcon(frameId);
        } else {
            tvHello.setText("Chào bạn, bạn tên gì?");
            tvPoint.setText("Điểm: 0");
            imgAvatar.setImageResource(R.drawable.ic_avatar_default);
            imgFrame.setImageResource(R.drawable.ic_frame_default);
        }

        btnContinue.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Nhập tên trước nhé!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (c != null && c.moveToFirst()) {
                db.clearProfile();
            }
            db.insertProfile(name);
            if (avatarUri != null) db.updateAvatarUri(avatarUri);
            db.updateFrame(frameId);
            db.updatePoint(point);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnSync.setOnClickListener(v -> {
            startActivity(new Intent(this, SyncActivity.class));
            finish();
        });

        btnChooseAvatar.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, REQ_PICK_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                avatarUri = uri.toString();
                imgAvatar.setImageURI(uri);
            }
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
