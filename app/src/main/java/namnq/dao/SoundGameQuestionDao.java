package namnq.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import namnq.model.SoundQuestion;

@Dao
public interface SoundGameQuestionDao {
    @Query("SELECT * FROM SoundGameQuestions")
    List<SoundQuestion> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SoundQuestion> questions);

    @Query("SELECT * FROM SoundGameQuestions ORDER BY RANDOM() LIMIT 1")
    SoundQuestion getRandom();

    @Query("SELECT * FROM SoundGameQuestions WHERE category_id = :categoryId ORDER BY RANDOM() LIMIT 1")
    SoundQuestion getRandomByCategory(int categoryId);
}
