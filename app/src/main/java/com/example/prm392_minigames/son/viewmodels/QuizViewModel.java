package com.example.prm392_minigames.son.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

//import com.example.quizapp.entities.Category;
//import com.example.quizapp.entities.Question;
//import com.example.quizapp.repository.QuizRepository;

import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.entities.Question;
import com.example.prm392_minigames.son.repositories.QuizRepository;

import java.util.List;

public class QuizViewModel extends AndroidViewModel {
    private QuizRepository repository;
    private LiveData<List<Category>> allCategories;

    public QuizViewModel(@NonNull Application application) {
        super(application);
        repository = new QuizRepository(application);
        allCategories = repository.getAllCategories();
    }

    public void insertCategory(Category category) {
        repository.insertCategory(category);
    }

    public void updateCategory(Category category) {
        repository.updateCategory(category);
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<Category>> searchCategories(String query) {
        return repository.searchCategories(query);
    }

    public LiveData<List<Category>> getCategoriesSortedByName() {
        return repository.getCategoriesSortedByName();
    }

    public LiveData<List<Category>> getCategoriesSortedByQuestions() {
        return repository.getCategoriesSortedByQuestions();
    }

    public LiveData<List<Category>> getCategoriesSortedByScore() {
        return repository.getCategoriesSortedByScore();
    }

    public void insertQuestion(Question question) {
        repository.insertQuestion(question);
    }

    public void updateQuestion(Question question) {
        repository.updateQuestion(question);
    }

    public LiveData<List<Question>> getQuestionsByCategory(int categoryId) {
        return repository.getQuestionsByCategory(categoryId);
    }

    public void getNextUnansweredQuestion(int categoryId, QuizRepository.OnQuestionLoadedListener listener) {
        repository.getNextUnansweredQuestion(categoryId, listener);
    }

    public void resetCategoryProgress(int categoryId) {
        repository.resetCategoryProgress(categoryId);
    }

    public Category getCategoryById(int id) {
        return repository.getCategoryById(id);
    }

    public void deleteQuestion(Question question) {
        repository.deleteQuestion(question);
    }

    public void deleteCategory(Category category) {
        repository.deleteCategory(category);
    }

    public Question getQuestionById(int id) {
        return repository.getQuestionById(id);
    }

    public void getNextUnansweredQuestionRandom(int categoryId, QuizRepository.OnQuestionLoadedListener listener) {
        repository.getNextUnansweredQuestionRandom(categoryId, listener);
    }

    public void updateCategoryScore(int categoryId, int points) {
        repository.updateCategoryScore(categoryId, points);
    }

}
