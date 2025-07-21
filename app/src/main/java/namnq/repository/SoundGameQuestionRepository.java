package namnq.repository;

import java.util.List;

import namnq.dao.SoundGameQuestionDao;
import namnq.model.SoundQuestion;

public class SoundGameQuestionRepository {
    private final SoundGameQuestionDao soundGameQuestionDao;

    public SoundGameQuestionRepository(SoundGameQuestionDao soundGameQuestionDao) {
        this.soundGameQuestionDao = soundGameQuestionDao;
    }

    public List<SoundQuestion> getAllQuestions() {
        return soundGameQuestionDao.getAll();
    }

    public void insertAll(List<SoundQuestion> questions) {
        soundGameQuestionDao.insertAll(questions);
    }

    public void update(SoundQuestion question) {
        soundGameQuestionDao.update(question);
    }

    public void delete(SoundQuestion question) {
        soundGameQuestionDao.delete(question);
    }

    public SoundQuestion getRandom() {
        return soundGameQuestionDao.getRandom();
    }

    public SoundQuestion getRandomByCategory(int categoryId) {
        return soundGameQuestionDao.getRandomByCategory(categoryId);
    }



}
