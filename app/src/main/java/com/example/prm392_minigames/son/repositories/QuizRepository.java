package com.example.prm392_minigames.son.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

//import com.example.quizapp.dao.CategoryDao;
//import com.example.quizapp.dao.QuestionDao;
//import com.example.quizapp.database.QuizDatabase;
//import com.example.quizapp.entities.Category;
//import com.example.quizapp.entities.Question;

import com.example.prm392_minigames.son.daos.CategoryDao;
import com.example.prm392_minigames.son.daos.QuestionDao;
import com.example.prm392_minigames.son.database.QuizDatabase;
import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.entities.Question;

import java.util.List;

public class QuizRepository {
    private CategoryDao categoryDao;
    private QuestionDao questionDao;
    private LiveData<List<Category>> allCategories;

    public QuizRepository(Application application) {
        QuizDatabase database = QuizDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        questionDao = database.questionDao();
        allCategories = categoryDao.getAllCategories();
    }

    // Category operations
    public void insertCategory(Category category) {
        new InsertCategoryAsyncTask(categoryDao).execute(category);
    }

    public void updateCategory(Category category) {
        new UpdateCategoryAsyncTask(categoryDao).execute(category);
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<Category>> searchCategories(String query) {
        return categoryDao.searchCategories("%" + query + "%");
    }

    public LiveData<List<Category>> getCategoriesSortedByName() {
        return categoryDao.getCategoriesSortedByName();
    }

    public LiveData<List<Category>> getCategoriesSortedByQuestions() {
        return categoryDao.getCategoriesSortedByQuestions();
    }

    public LiveData<List<Category>> getCategoriesSortedByScore() {
        return categoryDao.getCategoriesSortedByScore();
    }

    // Question operations
    public void insertQuestion(Question question) {
        new InsertQuestionAsyncTask(questionDao).execute(question);
    }

    public void updateQuestion(Question question) {
        new UpdateQuestionAsyncTask(questionDao).execute(question);
    }

    public LiveData<List<Question>> getQuestionsByCategory(int categoryId) {
        return questionDao.getQuestionsByCategory(categoryId);
    }

    public void getNextUnansweredQuestion(int categoryId, OnQuestionLoadedListener listener) {
        new GetNextQuestionAsyncTask(questionDao, listener).execute(categoryId);
    }

    public void resetCategoryProgress(int categoryId) {
        new ResetProgressAsyncTask(questionDao).execute(categoryId);
    }

    // AsyncTask classes
    private static class InsertCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private InsertCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.insert(categories[0]);
            return null;
        }
    }

    private static class UpdateCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private UpdateCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.update(categories[0]);
            return null;
        }
    }

    private static class InsertQuestionAsyncTask extends AsyncTask<Question, Void, Void> {
        private QuestionDao questionDao;

        private InsertQuestionAsyncTask(QuestionDao questionDao) {
            this.questionDao = questionDao;
        }

        @Override
        protected Void doInBackground(Question... questions) {
            questionDao.insert(questions[0]);
            return null;
        }
    }

    private static class UpdateQuestionAsyncTask extends AsyncTask<Question, Void, Void> {
        private QuestionDao questionDao;

        private UpdateQuestionAsyncTask(QuestionDao questionDao) {
            this.questionDao = questionDao;
        }

        @Override
        protected Void doInBackground(Question... questions) {
            questionDao.update(questions[0]);
            return null;
        }
    }

    private static class GetNextQuestionAsyncTask extends AsyncTask<Integer, Void, Question> {
        private QuestionDao questionDao;
        private OnQuestionLoadedListener listener;

        private GetNextQuestionAsyncTask(QuestionDao questionDao, OnQuestionLoadedListener listener) {
            this.questionDao = questionDao;
            this.listener = listener;
        }

        @Override
        protected Question doInBackground(Integer... categoryIds) {
            return questionDao.getNextUnansweredQuestion(categoryIds[0]);
        }

        @Override
        protected void onPostExecute(Question question) {
            if (listener != null) {
                listener.onQuestionLoaded(question);
            }
        }
    }

    private static class ResetProgressAsyncTask extends AsyncTask<Integer, Void, Void> {
        private QuestionDao questionDao;

        private ResetProgressAsyncTask(QuestionDao questionDao) {
            this.questionDao = questionDao;
        }

        @Override
        protected Void doInBackground(Integer... categoryIds) {
            questionDao.resetCategoryProgress(categoryIds[0]);
            return null;
        }
    }

    public interface OnQuestionLoadedListener {
        void onQuestionLoaded(Question question);
    }

    public Category getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    public void deleteQuestion(Question question) {
        new DeleteQuestionAsyncTask(questionDao).execute(question);
    }

    private static class DeleteQuestionAsyncTask extends AsyncTask<Question, Void, Void> {
        private QuestionDao questionDao;

        private DeleteQuestionAsyncTask(QuestionDao questionDao) {
            this.questionDao = questionDao;
        }

        @Override
        protected Void doInBackground(Question... questions) {
            questionDao.delete(questions[0]);
            return null;
        }
    }

    public void deleteCategory(Category category) {
        new DeleteCategoryAsyncTask(categoryDao).execute(category);
    }

    private static class DeleteCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private DeleteCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.delete(categories[0]);
            return null;
        }
    }

    public Question getQuestionById(int id) {
        return questionDao.getQuestionById(id);
    }
}

