package namnq.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import namnq.model.SoundCategory;

@Dao
public interface SoundGameCategoryDao {

    @Query("SELECT * FROM SoundCategories")
    List<SoundCategory> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SoundCategory> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SoundCategory category);

    @Query("SELECT * FROM SoundCategories WHERE id = :categoryId LIMIT 1")
    SoundCategory getById(int categoryId);

    @Delete
    void delete(SoundCategory category);

    @Update
    void update(SoundCategory category);
}