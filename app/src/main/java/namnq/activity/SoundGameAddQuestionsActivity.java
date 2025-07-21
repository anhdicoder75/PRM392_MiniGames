package namnq.activity;

import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "SoundGameAddQuestions";

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
        Log.d(TAG, "🚀 Activity onCreate started");

        editingQuestionId = getIntent().getIntExtra("questionId", -1);
        if (editingQuestionId != -1) {
            loadQuestionForEditing(editingQuestionId);
        }

        // 🔥 CRITICAL FIX: Initialize default categories FIRST before loading UI
        initializeDefaultCategories();
    }

    /**
     * 🆕 NEW METHOD: Initialize 4 default categories in database
     */
    private void initializeDefaultCategories() {
        Log.d(TAG, "🏗️ Initializing default categories...");

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                SoundAppDatabase database = SoundAppDatabase.getInstance(this);

                // Check if categories already exist
                List<SoundCategory> existingCategories = database.soundGameCategoryDao().getAll();

                if (existingCategories.isEmpty()) {
                    Log.d(TAG, "📦 No categories found, creating default ones...");

                    // Create 4 default categories
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

                    // Insert all categories
                    database.soundGameCategoryDao().insertAll(defaultCategories);
                    Log.d(TAG, "✅ Successfully created 4 default categories");

                } else {
                    Log.d(TAG, "✅ Categories already exist (" + existingCategories.size() + " found)");
                }

                // After categories are ready, initialize UI on main thread
                runOnUiThread(this::initData);

            } catch (Exception e) {
                Log.e(TAG, "❌ Error initializing categories", e);
                // Still try to init UI even if category creation fails
                runOnUiThread(this::initData);
            }
        });
    }

    private void initData() {
        Log.d(TAG, "🔧 initData() called");

        // Bind views
        btnAddOption = findViewById(R.id.btnAddOption);
        btnSaveQuestion = findViewById(R.id.btnSaveQuestion);
        rvAvailableSounds = findViewById(R.id.rvAvailableSounds);
        spinnerCorrectAnswer = findViewById(R.id.spinnerCorrectAnswer);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        optionContainer = findViewById(R.id.optionContainer);

        // Spinner setup
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrectAnswer.setAdapter(spinnerAdapter);

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // RecyclerView setup for available sounds (from assets)
        rvAvailableSounds.setLayoutManager(new LinearLayoutManager(this));
        soundSelectionAdapter = new SoundSelectionAdapter(this, soundItemList, (soundItem, isSelected) -> {
            Toast.makeText(this, (isSelected ? "Selected: " : "Deselected: ") + soundItem.getName(), Toast.LENGTH_SHORT).show();
        });
        rvAvailableSounds.setAdapter(soundSelectionAdapter);

        // Load sounds from assets
        loadSoundsFromAssets();

        // Add first option field
        btnAddOption.setOnClickListener(v -> {
            Log.d(TAG, "➕ Add option button clicked");
            addOptionField(null);
        });
        addOptionField(null);

        // Save question button
        btnSaveQuestion.setOnClickListener(v -> {
            Log.d(TAG, "💾 Save button clicked - starting validation");
            saveQuestionToDatabase();
        });

        // Load categories (now they should exist)
        loadCategories();

        Log.d(TAG, "✅ initData() completed successfully");
    }

    private void loadSoundsFromAssets() {
        Log.d(TAG, "🔊 Loading sounds from assets...");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String[] files = getAssets().list("sounds");
                if (files != null) {
                    Log.d(TAG, "📁 Found " + files.length + " sound files");
                    for (String file : files) {
                        String path = "sounds/" + file;
                        SoundItem item = new SoundItem(file, path, "default");
                        soundItemList.add(item);
                    }
                } else {
                    Log.w(TAG, "⚠️ No sound files found in assets/sounds");
                }
                runOnUiThread(() -> {
                    soundSelectionAdapter.notifyDataSetChanged();
                    Log.d(TAG, "🔄 Sound adapter updated on UI thread");
                });
            } catch (IOException e) {
                Log.e(TAG, "❌ Error loading sounds from assets", e);
            }
        });
    }

    private void saveQuestionToDatabase() {
        Log.d(TAG, "💾 saveQuestionToDatabase() method called");

        try {
            // Validate inputs first
            if (!validateInputs()) {
                Log.e(TAG, "❌ Input validation failed");
                showToast("Please fill in all required fields correctly");
                return;
            }

            List<String> soundUrls = collectSelectedAssetSounds();
            List<String> options = collectOptions();

            if (spinnerCorrectAnswer.getSelectedItem() == null) {
                Log.e(TAG, "❌ No correct answer selected");
                showToast("Please select a correct answer");
                return;
            }

            String correctAnswer = spinnerCorrectAnswer.getSelectedItem().toString();
            String hint = ((EditText) findViewById(R.id.etHint)).getText().toString().trim();
            String explanation = ((EditText) findViewById(R.id.etExplanation)).getText().toString().trim();

            // Parse difficulty and time limit
            int difficulty;
            int timeLimit;
            try {
                String difficultyStr = ((EditText) findViewById(R.id.etDifficulty)).getText().toString().trim();
                String timeLimitStr = ((EditText) findViewById(R.id.etTimeLimit)).getText().toString().trim();

                if (difficultyStr.isEmpty()) {
                    Log.e(TAG, "❌ Difficulty field is empty");
                    showToast("Please enter difficulty level");
                    return;
                }
                if (timeLimitStr.isEmpty()) {
                    Log.e(TAG, "❌ Time limit field is empty");
                    showToast("Please enter time limit");
                    return;
                }

                difficulty = Integer.parseInt(difficultyStr);
                timeLimit = Integer.parseInt(timeLimitStr);

                Log.d(TAG, "✅ Parsed difficulty: " + difficulty + ", timeLimit: " + timeLimit);
            } catch (NumberFormatException e) {
                Log.e(TAG, "❌ Error parsing numbers", e);
                showToast("Please enter valid numbers for difficulty and time limit");
                return;
            }

            // Create question object
            SoundQuestion question = new SoundQuestion();
            question.soundUrls = soundUrls;
            question.options = options;
            question.correctAnswer = correctAnswer;
            question.hint = hint;
            question.explanation = explanation;
            question.difficulty = difficulty;
            question.timeLimitSeconds = timeLimit;

            // 🔥 CRITICAL FIX: Better category handling
            int selectedCategoryPos = spinnerCategory.getSelectedItemPosition();
            if (selectedCategoryPos >= 0 && selectedCategoryPos < categoryList.size()) {
                question.categoryId = categoryList.get(selectedCategoryPos).id;
                Log.d(TAG, "✅ Category selected: " + categoryList.get(selectedCategoryPos).name + " (ID: " + question.categoryId + ")");
            } else {
                // 🔥 FALLBACK: If no category selected, use first available category (should be "Animal")
                if (!categoryList.isEmpty()) {
                    question.categoryId = categoryList.get(0).id;
                    Log.w(TAG, "⚠️ No category selected, using first category: " + categoryList.get(0).name + " (ID: " + question.categoryId + ")");
                } else {
                    Log.e(TAG, "❌ No categories available at all!");
                    showToast("Error: No categories available. Please restart the app.");
                    return;
                }
            }

            Log.d(TAG, "📦 Question object created:");
            Log.d(TAG, "   - Sound URLs: " + soundUrls.size() + " items");
            Log.d(TAG, "   - Options: " + options.size() + " items");
            Log.d(TAG, "   - Correct Answer: " + correctAnswer);
            Log.d(TAG, "   - Category ID: " + question.categoryId);

            // Insert to database
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    Log.d(TAG, "🗄️ Starting database insert...");
                    SoundAppDatabase database = SoundAppDatabase.getInstance(this);

                    // 🔥 ADDITIONAL CHECK: Verify category exists before inserting question
                    SoundCategory category = database.soundGameCategoryDao().getCategoryById(question.categoryId);
                    if (category == null) {
                        Log.e(TAG, "❌ Category with ID " + question.categoryId + " does not exist!");
                        runOnUiThread(() -> showToast("Error: Selected category does not exist"));
                        return;
                    }

                    if (editingQuestionId == -1) {
                        // Insert mới
                        database.soundGameQuestionDao().insertAll(Collections.singletonList(question));
                    } else {
                        // Update
                        question.id = editingQuestionId; // giữ ID cũ
                        database.soundGameQuestionDao().update(question);
                    }
                    Log.d(TAG, "✅ Database insert completed successfully");

                    runOnUiThread(() -> {
                        Log.d(TAG, "🎉 Showing success message on UI thread");
                        showToast("Question saved successfully!");
                        finish();
                    });

                } catch (Exception e) {
                    Log.e(TAG, "❌ Database insert failed", e);
                    runOnUiThread(() -> {
                        showToast("Failed to save question: " + e.getMessage());
                    });
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Unexpected error in saveQuestionToDatabase", e);
            showToast("Unexpected error occurred");
        }
    }

    private boolean validateInputs() {
        Log.d(TAG, "🔍 Validating inputs...");

        // Check if at least one sound is selected
        List<String> soundUrls = collectSelectedAssetSounds();
        if (soundUrls.isEmpty()) {
            Log.e(TAG, "❌ No sounds selected");
            showToast("Please select at least one sound");
            return false;
        }

        // Check if options are filled
        List<String> options = collectOptions();
        if (options.size() < 2) {
            Log.e(TAG, "❌ Need at least 2 options, found: " + options.size());
            showToast("Please add at least 2 options");
            return false;
        }

        // Check required EditTexts
        EditText etHint = findViewById(R.id.etHint);
        EditText etExplanation = findViewById(R.id.etExplanation);
        EditText etDifficulty = findViewById(R.id.etDifficulty);
        EditText etTimeLimit = findViewById(R.id.etTimeLimit);

        if (etHint == null || etExplanation == null || etDifficulty == null || etTimeLimit == null) {
            Log.e(TAG, "❌ Some EditText views are null");
            showToast("UI elements not found");
            return false;
        }

        Log.d(TAG, "✅ Input validation passed");
        return true;
    }

    private void showToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.d(TAG, "📢 Toast shown: " + message);
        });
    }

    private List<String> collectSelectedAssetSounds() {
        List<String> selectedAssets = new ArrayList<>();
        for (SoundItem item : soundItemList) {
            if (item.isSelected()) {
                selectedAssets.add(item.getAssetPath());
            }
        }
        Log.d(TAG, "🔊 Collected " + selectedAssets.size() + " selected sounds");
        return selectedAssets;
    }

    private void loadQuestionForEditing(int questionId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            SoundAppDatabase db = SoundAppDatabase.getInstance(this);
            editingQuestion = db.soundGameQuestionDao().getQuestionById(questionId);

            if (editingQuestion != null) {
                runOnUiThread(() -> {
                    // Fill sound selection
                    List<String> urls = editingQuestion.soundUrls;
                    for (SoundItem item : soundItemList) {
                        if (urls.contains(item.getAssetPath())) {
                            item.setSelected(true);
                        }
                    }
                    soundSelectionAdapter.notifyDataSetChanged();

                    // Fill options
                    optionContainer.removeAllViews();
                    optionEditTexts.clear();
                    for (String opt : editingQuestion.options) {
                        addOptionField(opt);
                    }

                    // Set correct answer
                    spinnerCorrectAnswer.setSelection(editingQuestion.options.indexOf(editingQuestion.correctAnswer));

                    // Set hint, explanation, difficulty, time limit
                    ((EditText) findViewById(R.id.etHint)).setText(editingQuestion.hint);
                    ((EditText) findViewById(R.id.etExplanation)).setText(editingQuestion.explanation);
                    ((EditText) findViewById(R.id.etDifficulty)).setText(String.valueOf(editingQuestion.difficulty));
                    ((EditText) findViewById(R.id.etTimeLimit)).setText(String.valueOf(editingQuestion.timeLimitSeconds));

                    // Set category
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).id == editingQuestion.categoryId) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }

                    Toast.makeText(this, "Editing question ID: " + questionId, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }



    private void loadCategories() {
        Log.d(TAG, "📂 Loading categories from database...");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                categoryList = SoundAppDatabase.getInstance(this).soundGameCategoryDao().getAll();
                List<String> names = new ArrayList<>();
                for (SoundCategory c : categoryList) {
                    names.add(c.name);
                }
                Log.d(TAG, "✅ Loaded " + categoryList.size() + " categories: " + names);

                runOnUiThread(() -> {
                    categoryAdapter.clear();
                    categoryAdapter.addAll(names);
                    categoryAdapter.notifyDataSetChanged();
                    Log.d(TAG, "🔄 Category adapter updated with: " + names);
                });
            } catch (Exception e) {
                Log.e(TAG, "❌ Error loading categories", e);
            }
        });
    }

    private void addOptionField(@Nullable String text) {
        Log.d(TAG, "➕ Adding new option field");

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        EditText et = new EditText(this);
        et.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        et.setHint("Option " + (optionEditTexts.size() + 1));
        if (text != null)
            et.setText(text);

        // Add text watcher to update spinner when option text changes
        et.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateSpinnerOptions();
            }
        });

        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setBackground(null);
        btnDelete.setOnClickListener(v -> {
            Log.d(TAG, "🗑️ Removing option field");
            optionContainer.removeView(row);
            optionEditTexts.remove(et);
            updateSpinnerOptions();
        });

        row.addView(et);
        row.addView(btnDelete);
        optionContainer.addView(row);
        optionEditTexts.add(et);
        updateSpinnerOptions();

        Log.d(TAG, "✅ Option field added, total options: " + optionEditTexts.size());
    }

    private List<String> collectOptions() {
        List<String> options = new ArrayList<>();
        for (EditText et : optionEditTexts) {
            String text = et.getText().toString().trim();
            if (!text.isEmpty()) {
                options.add(text);
            }
        }
        Log.d(TAG, "📝 Collected " + options.size() + " non-empty options");
        return options;
    }

    private void updateSpinnerOptions() {
        List<String> options = collectOptions();
        spinnerAdapter.clear();
        spinnerAdapter.addAll(options);
        spinnerAdapter.notifyDataSetChanged();
        Log.d(TAG, "🔄 Spinner options updated: " + options.size() + " items");
    }
}