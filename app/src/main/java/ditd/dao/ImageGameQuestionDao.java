package ditd.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ditd.model.Question;

@Dao
public interface ImageGameQuestionDao {
    @Query("Select * FROM ImageGameQuestions")
    List<Question> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Question> questions);
    @Query("SELECT * FROM ImageGameQuestions ORDER BY RANDOM() LIMIT 1")
    Question getRandom();
    @Query("SELECT * FROM ImageGameQuestions WHERE category_id = :categoryId ORDER BY RANDOM() LIMIT 1")
    Question getRandomByCategory(int categoryId);
    @Query("SELECT * FROM ImageGameQuestions WHERE id = :id LIMIT 1")
    Question getById(int id);

    @Delete
    void delete(Question question);



}
