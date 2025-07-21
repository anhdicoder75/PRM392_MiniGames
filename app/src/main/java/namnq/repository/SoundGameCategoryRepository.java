package namnq.repository;

import java.util.List;

import namnq.dao.SoundGameCategoryDao;
import namnq.model.SoundCategory;

public class SoundGameCategoryRepository {
    private final SoundGameCategoryDao soundGameCategoryDao;

    public SoundGameCategoryRepository(SoundGameCategoryDao soundGameCategoryDao) {
        this.soundGameCategoryDao = soundGameCategoryDao;
    }

    public List<SoundCategory> getAllCategories() {
        return soundGameCategoryDao.getAll();
    }

    public void insert(SoundCategory category) {
        soundGameCategoryDao.insert(category);
    }

    public void insertAll(List<SoundCategory> categories) {
        soundGameCategoryDao.insertAll(categories);
    }
}