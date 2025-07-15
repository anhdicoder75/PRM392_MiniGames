package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.*;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.models.AvatarFrame;
import com.example.prm392_minigames.db.AppDatabaseHelper;
import com.example.prm392_minigames.adapters.FrameShopAdapter;

import java.util.*;

public class ShopActivity extends Activity {
    RecyclerView rvShop;
    AppDatabaseHelper db;
    int userPoint, currentFrameId;
    java.util.Set<Integer> owned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        rvShop = findViewById(R.id.rv_shop);
        db = new AppDatabaseHelper(this);

        Cursor c = db.getProfile();
        if (c != null && c.moveToFirst()) {
            userPoint = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
            currentFrameId = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_FRAME));
        } else {
            userPoint = 0;
            currentFrameId = 0;
        }
        owned = new HashSet<>(db.getAllOwnedFrames());

        // Danh sách frame
        List<AvatarFrame> frames = Arrays.asList(
                new AvatarFrame(0, R.drawable.ic_frame_default, "Cơ bản", 0),
                new AvatarFrame(1, R.drawable.ic_frame_gold_anim, "Khung vàng", 50),
                new AvatarFrame(2, R.drawable.ic_frame_rainbow_anim, "Rainbow", 100)
        );

        FrameShopAdapter adapter = new FrameShopAdapter(this, frames, userPoint, currentFrameId, owned, frame -> {
            if (owned.contains(frame.id)) {
                db.updateFrame(frame.id);
                Toast.makeText(this, "Đã chuyển sang " + frame.name, Toast.LENGTH_SHORT).show();
            } else if (userPoint >= frame.cost) {
                db.addOwnedFrame(frame.id);
                db.updateFrame(frame.id);
                db.updatePoint(userPoint - frame.cost);
                Toast.makeText(this, "Đã mua và sử dụng " + frame.name, Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        rvShop.setLayoutManager(new GridLayoutManager(this, 2));
        rvShop.setAdapter(adapter);
    }
}
