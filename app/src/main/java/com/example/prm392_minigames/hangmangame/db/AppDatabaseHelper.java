package com.example.prm392_minigames.hangmangame.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.List;
import java.util.ArrayList;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "minigames.db";
    private static final int DB_VERSION = 3;

    // Profile
    public static final String TABLE_PROFILE = "user_profile";
    public static final String COL_NAME = "name";
    public static final String COL_AVATAR = "avatarUri";
    public static final String COL_FRAME = "currentFrame";
    public static final String COL_POINT = "point";

    // Khung đã sở hữu
    public static final String TABLE_OWNED_FRAMES = "owned_frames";
    public static final String COL_FRAME_ID = "frame_id";

    public AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROFILE + " (" +
                COL_NAME + " TEXT PRIMARY KEY, " +
                COL_AVATAR + " TEXT, " +
                COL_FRAME + " INTEGER DEFAULT 0, " +
                COL_POINT + " INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_OWNED_FRAMES + " (" +
                COL_FRAME_ID + " INTEGER PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNED_FRAMES);
        onCreate(db);
    }

    // ---- PROFILE ----
    public void insertProfile(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, null, null);
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_AVATAR, (String)null);
        values.put(COL_FRAME, 0);
        values.put(COL_POINT, 0);
        db.insert(TABLE_PROFILE, null, values);
        // Luôn sở hữu sẵn khung mặc định
        addOwnedFrame(0);
        db.close();
    }

    public Cursor getProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " LIMIT 1", null);
    }

    public void updateAvatarUri(String avatarUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AVATAR, avatarUri);
        db.update(TABLE_PROFILE, values, null, null);
        db.close();
    }

    public void updateFrame(int frameId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FRAME, frameId);
        db.update(TABLE_PROFILE, values, null, null);
        db.close();
    }

    public void updatePoint(int point) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_POINT, point);
        db.update(TABLE_PROFILE, values, null, null);
        db.close();
    }

    public void addPoint(int delta) {
        Cursor cursor = getProfile();
        if (cursor != null && cursor.moveToFirst()) {
            int current = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POINT));
            updatePoint(current + delta);
            cursor.close();
        }
    }


    public void clearProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, null, null);
        db.delete(TABLE_OWNED_FRAMES, null, null);
        db.close();
    }

    // ---- FRAME SHOP ----
    public boolean isFrameOwned(int frameId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + TABLE_OWNED_FRAMES + " WHERE " + COL_FRAME_ID + "=?",
                new String[]{String.valueOf(frameId)});
        boolean owned = c.moveToFirst();
        c.close();
        return owned;
    }

    public void addOwnedFrame(int frameId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FRAME_ID, frameId);
        db.insertWithOnConflict(TABLE_OWNED_FRAMES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    // Lấy toàn bộ id khung đã sở hữu
    public List<Integer> getAllOwnedFrames() {
        List<Integer> frames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_FRAME_ID + " FROM " + TABLE_OWNED_FRAMES, null);
        if (c != null) {
            while (c.moveToNext()) {
                frames.add(c.getInt(0));
            }
            c.close();
        }
        return frames;
    }

    // Ghi đè toàn bộ khung sở hữu (dùng cho sync ngược)
    public void setAllOwnedFrames(List<Integer> frames) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OWNED_FRAMES, null, null);
        for (int frameId : frames) addOwnedFrame(frameId);
        db.close();
    }
}
