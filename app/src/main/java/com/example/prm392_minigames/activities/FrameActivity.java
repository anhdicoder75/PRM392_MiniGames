package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.models.AvatarFrame;
import com.example.prm392_minigames.hangmangame.db.AppDatabaseHelper;
import android.graphics.drawable.AnimationDrawable;

import java.util.*;

public class FrameActivity extends Activity {
    LinearLayout llFrameList;
    AppDatabaseHelper db;
    int userPoint, currentFrameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        llFrameList = findViewById(R.id.ll_frame_list);
        db = new AppDatabaseHelper(this);

        Cursor c = db.getProfile();
        if (c != null && c.moveToFirst()) {
            userPoint = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
            currentFrameId = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_FRAME));
        } else {
            userPoint = 0;
            currentFrameId = 0;
        }

        List<AvatarFrame> frames = Arrays.asList(
                new AvatarFrame(0, R.drawable.ic_frame_default, "Cơ bản", 0),
                new AvatarFrame(1, R.drawable.ic_frame_gold_anim, "Khung vàng", 300),
                new AvatarFrame(2, R.drawable.ic_frame_rainbow_anim, "Rainbow", 900)
        );

        for (AvatarFrame frame : frames) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setPadding(12, 14, 12, 14);

            ImageView img = new ImageView(this);
            img.setImageResource(frame.iconRes);
            img.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
            // Khung động
            if (frame.id == 1 || frame.id == 2) {
                AnimationDrawable anim = (AnimationDrawable) img.getDrawable();
                img.post(anim::start);
            }
            item.addView(img);

            TextView tv = new TextView(this);
            tv.setText(frame.name + (frame.cost > 0 ? " (" + frame.cost + " điểm)" : " (Miễn phí)"));
            tv.setTextSize(17);
            tv.setPadding(22, 0, 0, 0);
            item.addView(tv);

            Button btn = new Button(this);
            if (frame.id == currentFrameId) {
                btn.setText("Đang dùng");
                btn.setEnabled(false);
            } else if (userPoint >= frame.cost) {
                btn.setText("Đổi");
                btn.setEnabled(true);
                btn.setOnClickListener(v -> {
                    db.updateFrame(frame.id);
                    if (frame.cost > 0 && currentFrameId != frame.id) {
                        db.updatePoint(userPoint - frame.cost);
                    }
                    Toast.makeText(this, "Đổi thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                btn.setText("Thiếu điểm");
                btn.setEnabled(false);
            }
            item.addView(btn);
            llFrameList.addView(item);
        }
    }
}
