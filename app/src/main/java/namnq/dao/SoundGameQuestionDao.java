package namnq.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

import namnq.model.SoundQuestion;

@Dao
public interface SoundGameQuestionDao {
    @Query("SELECT * FROM SoundGameQuestions")
    List<SoundQuestion> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SoundQuestion> questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SoundQuestion question);

    @Update
    void update(SoundQuestion question);

    @Delete
    void delete(SoundQuestion question);

    @Query("SELECT * FROM SoundGameQuestions ORDER BY RANDOM() LIMIT 1")
    SoundQuestion getRandom();

    @Query("SELECT * FROM SoundGameQuestions WHERE category_id = :categoryId ORDER BY RANDOM() LIMIT 1")
    SoundQuestion getRandomByCategory(int categoryId);

    @Query("SELECT * FROM SoundGameQuestions WHERE id = :id LIMIT 1")
    SoundQuestion getQuestionById(int id);
}
