package com.example.prm392_minigames.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "minigames.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_PROFILE = "user_profile";
    public static final String COL_NAME = "name";

    public AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROFILE + " (" + COL_NAME + " TEXT PRIMARY KEY )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    public void insertProfile(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, null, null); // Chỉ 1 dòng duy nhất
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        db.insert(TABLE_PROFILE, null, values);
        db.close();
    }

    public Cursor getProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " LIMIT 1", null);
    }

    public void clearProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, null, null);
        db.close();
    }
}
