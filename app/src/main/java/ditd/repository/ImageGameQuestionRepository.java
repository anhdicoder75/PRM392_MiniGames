package ditd.repository;

import java.util.List;

import ditd.dao.ImageGameQuestionDao;
import ditd.model.Question;

public class ImageGameQuestionRepository {
    private final ImageGameQuestionDao iamgeGameQuestionDao;

    public ImageGameQuestionRepository(ImageGameQuestionDao iamgeGameQuestionDao) {
        this.iamgeGameQuestionDao = iamgeGameQuestionDao;
    }
    public List<Question> getAllQuestions(){
        return iamgeGameQuestionDao.getAll();
    }
//    public List<Question> getRandomQuestions(int limit) {
//        return questionDao.getRandomQuestions(limit);
//    }

    public void insertAll(List<Question> questions) {
        iamgeGameQuestionDao.insertAll(questions);
    }
}
