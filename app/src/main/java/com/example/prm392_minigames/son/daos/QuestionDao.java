package com.example.prm392_minigames.son.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.prm392_minigames.son.entities.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    void insert(Question question);

    @Update
    void update(Question question);

    @Delete
    void delete(Question question);

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId")
    LiveData<List<Question>> getQuestionsByCategory(int categoryId);

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId AND isAnswered = 0 LIMIT 1")
    Question getNextUnansweredQuestion(int categoryId);

    @Query("SELECT COUNT(*) FROM questions WHERE categoryId = :categoryId")
    int getQuestionCountByCategory(int categoryId);

    @Query("SELECT SUM(points) FROM questions WHERE categoryId = :categoryId")
    int getTotalPointsByCategory(int categoryId);

    @Query("SELECT SUM(points) FROM questions WHERE categoryId = :categoryId AND isCorrect = 1")
    int getUserScoreByCategory(int categoryId);

    @Query("UPDATE questions SET isAnswered = 0, isCorrect = 0 WHERE categoryId = :categoryId")
    void resetCategoryProgress(int categoryId);

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    Question getQuestionById(int id);

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId AND isAnswered = 0 ORDER BY RANDOM() LIMIT 1")
    Question getNextUnansweredQuestionRandom(int categoryId);
}
