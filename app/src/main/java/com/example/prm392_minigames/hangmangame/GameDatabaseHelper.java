package com.example.prm392_minigames.hangmangame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class GameDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hangmangame.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng từ
    private static final String TABLE_GAME_WORDS = "game_words";
    private static final String COLUMN_WORD_ID = "id";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_HINT = "hint";

    // Bảng điểm
    private static final String TABLE_GAME_SCORES = "game_scores";
    private static final String COLUMN_SCORE_ID = "id";
    private static final String COLUMN_PLAYER_NAME = "player_name";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_DATE = "date";

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_WORDS = "CREATE TABLE " + TABLE_GAME_WORDS + "("
            + COLUMN_WORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_WORD + " TEXT NOT NULL,"
            + COLUMN_HINT + " TEXT NOT NULL"
            + ")";

    private static final String CREATE_TABLE_SCORES = "CREATE TABLE " + TABLE_GAME_SCORES + "("
            + COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PLAYER_NAME + " TEXT NOT NULL,"
            + COLUMN_SCORE + " INTEGER NOT NULL,"
            + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORDS);
        db.execSQL(CREATE_TABLE_SCORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_SCORES);
        onCreate(db);
    }

    // Phương thức thêm từ mới
    public long addWord(String word, String hint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word.toUpperCase());
        values.put(COLUMN_HINT, hint);

        long id = db.insert(TABLE_GAME_WORDS, null, values);
        db.close();
        return id;
    }

    // Lấy tất cả từ
    public List<GameWord> getAllWords() {
        List<GameWord> wordsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GAME_WORDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int wordIndex = cursor.getColumnIndex(COLUMN_WORD);
                int hintIndex = cursor.getColumnIndex(COLUMN_HINT);

                if (wordIndex != -1 && hintIndex != -1) {
                    String word = cursor.getString(wordIndex);
                    String hint = cursor.getString(hintIndex);
                    wordsList.add(new GameWord(word, hint));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return wordsList;
    }

    // Kiểm tra xem có từ nào trong database không
    public boolean hasWords() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_GAME_WORDS;
        Cursor cursor = db.rawQuery(countQuery, null);

        boolean hasWords = false;
        if (cursor.moveToFirst()) {
            hasWords = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();
        return hasWords;
    }

    // Thêm điểm số
    public long addScore(String playerName, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_NAME, playerName);
        values.put(COLUMN_SCORE, score);

        long id = db.insert(TABLE_GAME_SCORES, null, values);
        db.close();
        return id;
    }

    // Lấy top điểm cao nhất
    public List<GameScore> getTopScores(int limit) {
        List<GameScore> scoresList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GAME_SCORES +
                " ORDER BY " + COLUMN_SCORE + " DESC LIMIT " + limit;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_SCORE_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_PLAYER_NAME);
                int scoreIndex = cursor.getColumnIndex(COLUMN_SCORE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

                if (idIndex != -1 && nameIndex != -1 && scoreIndex != -1 && dateIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String playerName = cursor.getString(nameIndex);
                    int score = cursor.getInt(scoreIndex);
                    String date = cursor.getString(dateIndex);

                    scoresList.add(new GameScore(id, playerName, score, date));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return scoresList;
    }

    // Lấy tất cả điểm số
    public List<GameScore> getAllScores() {
        List<GameScore> scoresList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GAME_SCORES +
                " ORDER BY " + COLUMN_SCORE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_SCORE_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_PLAYER_NAME);
                int scoreIndex = cursor.getColumnIndex(COLUMN_SCORE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

                if (idIndex != -1 && nameIndex != -1 && scoreIndex != -1 && dateIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String playerName = cursor.getString(nameIndex);
                    int score = cursor.getInt(scoreIndex);
                    String date = cursor.getString(dateIndex);

                    scoresList.add(new GameScore(id, playerName, score, date));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return scoresList;
    }

    // Xóa điểm số
    public void deleteScore(int scoreId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAME_SCORES, COLUMN_SCORE_ID + " = ?",
                new String[]{String.valueOf(scoreId)});
        db.close();
    }

    // Xóa từ
    public void deleteWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAME_WORDS, COLUMN_WORD + " = ?", new String[]{word});
        db.close();
    }

    // Cập nhật từ
    public int updateWord(String oldWord, String newWord, String newHint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, newWord.toUpperCase());
        values.put(COLUMN_HINT, newHint);

        int result = db.update(TABLE_GAME_WORDS, values, COLUMN_WORD + " = ?",
                new String[]{oldWord});
        db.close();
        return result;
    }

    // Xóa tất cả điểm số
    public void clearAllScores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GAME_SCORES);
        db.close();
    }
}

// Class riêng để lưu trữ điểm số
class GameScore {
    private int id;
    private String playerName;
    private int score;
    private String date;

    public GameScore(int id, String playerName, int score, String date) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public String getDate() { return date; }
}


class GameWord {
    private String word;
    private String hint;

    public GameWord(String word, String hint) {
        this.word = word;
        this.hint = hint;
    }

    public String getWord() {
        return word;
    }

    public String getHint() {
        return hint;
    }
}