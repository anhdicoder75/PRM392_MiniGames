package com.example.prm392_minigames.son.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.prm392_minigames.son.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE name LIKE :searchQuery ORDER BY name ASC")
    LiveData<List<Category>> searchCategories(String searchQuery);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getCategoriesSortedByName();

    @Query("SELECT * FROM categories ORDER BY totalQuestions DESC")
    LiveData<List<Category>> getCategoriesSortedByQuestions();

    @Query("SELECT * FROM categories ORDER BY userScore DESC")
    LiveData<List<Category>> getCategoriesSortedByScore();

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(int categoryId);

    @Query("UPDATE categories SET userScore = userScore + :points WHERE id = :categoryId")
    void updateCategoryScore(int categoryId, int points);
}
