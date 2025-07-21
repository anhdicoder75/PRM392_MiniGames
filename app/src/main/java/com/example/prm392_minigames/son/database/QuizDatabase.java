package com.example.prm392_minigames.son.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.prm392_minigames.son.daos.CategoryDao;
import com.example.prm392_minigames.son.daos.QuestionDao;
import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.entities.Question;


@Database(entities = {Category.class, Question.class}, version = 2, exportSchema = false)
public abstract class QuizDatabase extends RoomDatabase {

    private static QuizDatabase instance;

    public abstract CategoryDao categoryDao();
    public abstract QuestionDao questionDao();

    public static synchronized QuizDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            QuizDatabase.class, "quiz_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
