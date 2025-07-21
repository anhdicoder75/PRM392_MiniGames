package namnq.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import namnq.dao.SoundGameCategoryDao;
import namnq.dao.SoundGameQuestionDao;
import namnq.model.SoundCategory;
import namnq.model.SoundQuestion;

@Database(entities = { SoundQuestion.class, SoundCategory.class }, version = 1, exportSchema = false)
@TypeConverters(SoundConverters.class)
public abstract class SoundAppDatabase extends RoomDatabase {
    private static volatile SoundAppDatabase INSTANCE;

    public abstract SoundGameQuestionDao soundGameQuestionDao();

    public abstract SoundGameCategoryDao soundGameCategoryDao();

    public static SoundAppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SoundAppDatabase.class) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        SoundAppDatabase.class,
                        "sound_guess_game.db").build();
            }
        }
        return INSTANCE;
    }
}
