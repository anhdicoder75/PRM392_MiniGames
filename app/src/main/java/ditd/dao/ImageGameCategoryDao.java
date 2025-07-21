package ditd.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ditd.model.Category;

@Dao
public interface ImageGameCategoryDao {


    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    Category getById(int categoryId);


    @Delete
    void delete(Category category);

    @Update
    void update(Category editingCategory);
}
