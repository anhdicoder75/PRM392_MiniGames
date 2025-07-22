package ditd.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ditd.dao.ImageGameCategoryDao;
import ditd.dao.ImageGameQuestionDao;
import ditd.model.Category;
import ditd.model.Question;

@Database(
        entities = {Question.class, Category.class},
        version =   3,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile  AppDatabase INSTANCE;

    public abstract ImageGameQuestionDao imageGameQuestionDao();
    public abstract ImageGameCategoryDao imageGameCategoryDao();
    public static AppDatabase getInstance(Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(
                                context.getApplicationContext(),
                                AppDatabase.class,
                                "image_guess_game.db"
                        ).fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }
}
