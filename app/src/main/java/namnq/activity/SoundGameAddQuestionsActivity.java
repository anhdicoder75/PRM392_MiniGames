package namnq.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import namnq.adapter.SoundSelectionAdapter;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundCategory;
import namnq.model.SoundItem;
import namnq.model.SoundQuestion;

public class SoundGameAddQuestionsActivity extends AppCompatActivity {

    private Button btnAddOption, btnSaveQuestion;
    private RecyclerView rvAvailableSounds;
    private Spinner spinnerCorrectAnswer, spinnerCategory;
    private LinearLayout optionContainer;

    private List<EditText> optionEditTexts = new ArrayList<>();
    private List<SoundItem> soundItemList = new ArrayList<>();

    private SoundSelectionAdapter soundSelectionAdapter;
    private ArrayAdapter<String> spinnerAdapter, categoryAdapter;
    private List<SoundCategory> categoryList = new ArrayList<>();

    private int editingQuestionId = -1;
    private SoundQuestion editingQuestion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_add_questions);

        editingQuestionId = getIntent().getIntExtra("questionId", -1);
        if (editingQuestionId != -1) {
            loadQuestionForEditing(editingQuestionId);
        }

        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                SoundAppDatabase database = SoundAppDatabase.getInstance(this);
                List<SoundCategory> existingCategories = database.soundGameCategoryDao().getAll();

                if (existingCategories.isEmpty()) {
                    List<SoundCategory> defaultCategories = new ArrayList<>();

                    SoundCategory animal = new SoundCategory();
                    animal.name = "Animal";
                    animal.description = "Animal sounds and noises";
                    defaultCategories.add(animal);

                    SoundCategory natural = new SoundCategory();
                    natural.name = "Natural";
                    natural.description = "Nature sounds like wind, water, etc";
                    defaultCategories.add(natural);

                    SoundCategory things = new SoundCategory();
                    things.name = "Things";
                    things.description = "Object and machine sounds";
                    defaultCategories.add(things);

                    SoundCategory human = new SoundCategory();
                    human.name = "Human";
                    human.description = "Human voices and activities";
                    defaultCategories.add(human);

                    database.soundGameCategoryDao().insertAll(defaultCategories);
                }

                runOnUiThread(this::initData);
            } catch (Exception e) {
                runOnUiThread(this::initData);
            }
        });
    }

    private void initData() {
        btnAddOption = findViewById(R.id.btnAddOption);
        btnSaveQuestion = findViewById(R.id.btnSaveQuestion);
        rvAvailableSounds = findViewById(R.id.rvAvailableSounds);
        spinnerCorrectAnswer = findViewById(R.id.spinnerCorrectAnswer);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        optionContainer = findViewById(R.id.optionContainer);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrectAnswer.setAdapter(spinnerAdapter);

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        rvAvailableSounds.setLayoutManager(new LinearLayoutManager(this));
        soundSelectionAdapter = new SoundSelectionAdapter(this, soundItemList, (soundItem, isSelected) -> {
            Toast.makeText(this, (isSelected ? "Selected: " : "Deselected: ") + soundItem.getName(), Toast.LENGTH_SHORT).show();
        });
        rvAvailableSounds.setAdapter(soundSelectionAdapter);

        loadSoundsFromAssets();

        btnAddOption.setOnClickListener(v -> addOptionField(null));
        addOptionField(null);

        btnSaveQuestion.setOnClickListener(v -> saveQuestionToDatabase());

        loadCategories();
    }

    private void loadSoundsFromAssets() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String[] files = getAssets().list("sounds");
                if (files != null) {
                    for (String file : files) {
                        String path = "sounds/" + file;
                        SoundItem item = new SoundItem(file, path, "default");
                        soundItemList.add(item);
                    }
                }
                runOnUiThread(() -> soundSelectionAdapter.notifyDataSetChanged());
            } catch (IOException ignored) {}
        });
    }

    private void saveQuestionToDatabase() {
        if (!validateInputs()) {
            showToast("Please fill in all required fields correctly");
            return;
        }

        List<String> soundUrls = collectSelectedAssetSounds();
        List<String> options = collectOptions();

        if (spinnerCorrectAnswer.getSelectedItem() == null) {
            showToast("Please select a correct answer");
            return;
        }

        String correctAnswer = spinnerCorrectAnswer.getSelectedItem().toString();
        String hint = ((EditText) findViewById(R.id.etHint)).getText().toString().trim();
        String explanation = ((EditText) findViewById(R.id.etExplanation)).getText().toString().trim();

        int difficulty;
        int timeLimit;
        try {
            String difficultyStr = ((EditText) findViewById(R.id.etDifficulty)).getText().toString().trim();
            String timeLimitStr = ((EditText) findViewById(R.id.etTimeLimit)).getText().toString().trim();
            difficulty = Integer.parseInt(difficultyStr);
            timeLimit = Integer.parseInt(timeLimitStr);
        } catch (NumberFormatException e) {
            showToast("Please enter valid numbers for difficulty and time limit");
            return;
        }

        SoundQuestion question = new SoundQuestion();
        question.soundUrls = soundUrls;
        question.options = options;
        question.correctAnswer = correctAnswer;
        question.hint = hint;
        question.explanation = explanation;
        question.difficulty = difficulty;
        question.timeLimitSeconds = timeLimit;

        int selectedCategoryPos = spinnerCategory.getSelectedItemPosition();
        if (selectedCategoryPos >= 0 && selectedCategoryPos < categoryList.size()) {
            question.categoryId = categoryList.get(selectedCategoryPos).id;
        } else {
            if (!categoryList.isEmpty()) {
                question.categoryId = categoryList.get(0).id;
            } else {
                showToast("Error: No categories available. Please restart the app.");
                return;
            }
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                SoundAppDatabase database = SoundAppDatabase.getInstance(this);
                SoundCategory category = database.soundGameCategoryDao().getCategoryById(question.categoryId);
                if (category == null) {
                    runOnUiThread(() -> showToast("Error: Selected category does not exist"));
                    return;
                }

                if (editingQuestionId == -1) {
                    database.soundGameQuestionDao().insertAll(Collections.singletonList(question));
                } else {
                    question.id = editingQuestionId;
                    database.soundGameQuestionDao().update(question);
                }

                runOnUiThread(() -> {
                    showToast("Question saved successfully!");
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> showToast("Failed to save question: " + e.getMessage()));
            }
        });
    }

    private boolean validateInputs() {
        List<String> soundUrls = collectSelectedAssetSounds();
        if (soundUrls.isEmpty()) {
            showToast("Please select at least one sound");
            return false;
        }

        List<String> options = collectOptions();
        if (options.size() < 2) {
            showToast("Please add at least 2 options");
            return false;
        }

        EditText etHint = findViewById(R.id.etHint);
        EditText etExplanation = findViewById(R.id.etExplanation);
        EditText etDifficulty = findViewById(R.id.etDifficulty);
        EditText etTimeLimit = findViewById(R.id.etTimeLimit);

        return etHint != null && etExplanation != null && etDifficulty != null && etTimeLimit != null;
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private List<String> collectSelectedAssetSounds() {
        List<String> selectedAssets = new ArrayList<>();
        for (SoundItem item : soundItemList) {
            if (item.isSelected()) {
                selectedAssets.add(item.getAssetPath());
            }
        }
        return selectedAssets;
    }

    private void loadQuestionForEditing(int questionId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            SoundAppDatabase db = SoundAppDatabase.getInstance(this);
            editingQuestion = db.soundGameQuestionDao().getQuestionById(questionId);

            if (editingQuestion != null) {
                runOnUiThread(() -> {
                    List<String> urls = editingQuestion.soundUrls;
                    for (SoundItem item : soundItemList) {
                        if (urls.contains(item.getAssetPath())) {
                            item.setSelected(true);
                        }
                    }
                    soundSelectionAdapter.notifyDataSetChanged();

                    optionContainer.removeAllViews();
                    optionEditTexts.clear();
                    for (String opt : editingQuestion.options) {
                        addOptionField(opt);
                    }

                    spinnerCorrectAnswer.setSelection(editingQuestion.options.indexOf(editingQuestion.correctAnswer));
                    ((EditText) findViewById(R.id.etHint)).setText(editingQuestion.hint);
                    ((EditText) findViewById(R.id.etExplanation)).setText(editingQuestion.explanation);
                    ((EditText) findViewById(R.id.etDifficulty)).setText(String.valueOf(editingQuestion.difficulty));
                    ((EditText) findViewById(R.id.etTimeLimit)).setText(String.valueOf(editingQuestion.timeLimitSeconds));

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).id == editingQuestion.categoryId) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                });
            }
        });
    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                categoryList = SoundAppDatabase.getInstance(this).soundGameCategoryDao().getAll();
                List<String> names = new ArrayList<>();
                for (SoundCategory c : categoryList) {
                    names.add(c.name);
                }

                runOnUiThread(() -> {
                    categoryAdapter.clear();
                    categoryAdapter.addAll(names);
                    categoryAdapter.notifyDataSetChanged();
                });
            } catch (Exception ignored) {}
        });
    }

    private void addOptionField(@Nullable String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        EditText et = new EditText(this);
        et.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        et.setHint("Option " + (optionEditTexts.size() + 1));
        if (text != null)
            et.setText(text);

        et.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                updateSpinnerOptions();
            }
        });

        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setBackground(null);
        btnDelete.setOnClickListener(v -> {
            optionContainer.removeView(row);
            optionEditTexts.remove(et);
            updateSpinnerOptions();
        });

        row.addView(et);
        row.addView(btnDelete);
        optionContainer.addView(row);
        optionEditTexts.add(et);
        updateSpinnerOptions();
    }

    private List<String> collectOptions() {
        List<String> options = new ArrayList<>();
        for (EditText et : optionEditTexts) {
            String text = et.getText().toString().trim();
            if (!text.isEmpty()) {
                options.add(text);
            }
        }
        return options;
    }

    private void updateSpinnerOptions() {
        List<String> options = collectOptions();
        spinnerAdapter.clear();
        spinnerAdapter.addAll(options);
        spinnerAdapter.notifyDataSetChanged();
    }
}
