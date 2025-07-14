package ditd.repository;

import java.util.List;

import ditd.dao.ImageGameCategoryDao;
import ditd.model.Category;

public class ImageGameCategoryRepository {
    private final ImageGameCategoryDao imageGameCategoryDao;

    public ImageGameCategoryRepository(ImageGameCategoryDao imageGameCategoryDao) {
        this.imageGameCategoryDao = imageGameCategoryDao;
    }

    public List<Category> getAllCategories() {
        return imageGameCategoryDao.getAll();
    }

    public void insert(Category category) {
        imageGameCategoryDao.insert(category);
    }

    public void insertAll(List<Category> categories) {
        imageGameCategoryDao.insertAll(categories);
    }


}
