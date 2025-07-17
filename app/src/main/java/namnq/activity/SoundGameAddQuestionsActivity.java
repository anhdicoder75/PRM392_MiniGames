package namnq.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.prm392_minigames.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import namnq.adapter.SoundGameSelectedSoundsAdapter;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundCategory;
import namnq.model.SoundQuestion;
import namnq.service.CloudService;

public class SoundGameAddQuestionsActivity extends AppCompatActivity {
    private Button btnPickSounds, btnUploadAndSave, btnAddOption, btnClearSounds;
    private RecyclerView rvSelectedSounds;
    private Spinner spinnerCorrectAnswer, spinnerCategory;
    private LinearLayout optionContainer;
    private List<Uri> selectedSoundUris = new ArrayList<>();
    private List<EditText> optionEditTexts = new ArrayList<>();
    private SoundGameSelectedSoundsAdapter soundAdapter;
    private ArrayAdapter<String> spinnerAdapter, categoryAdapter;
    private List<SoundCategory> categoryList = new ArrayList<>();
    private ActivityResultLauncher<Intent> pickSoundsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_add_questions);
        initData();
    }

    private void initData() {
        btnPickSounds = findViewById(R.id.btnPickSounds);
        btnUploadAndSave = findViewById(R.id.btnUploadAndSave);
        btnAddOption = findViewById(R.id.btnAddOption);
        btnClearSounds = findViewById(R.id.btnClearSounds);
        rvSelectedSounds = findViewById(R.id.rvSelectedSounds);
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

        // RecyclerView setup
        soundAdapter = new SoundGameSelectedSoundsAdapter(this, selectedSoundUris);
        rvSelectedSounds.setAdapter(soundAdapter);
        rvSelectedSounds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Pick sound button
        btnPickSounds.setOnClickListener(v -> pickSounds());

        // Clear sound list
        btnClearSounds.setOnClickListener(v -> {
            selectedSoundUris.clear();
            soundAdapter.notifyDataSetChanged();
        });

        // Upload & save
        btnUploadAndSave.setOnClickListener(v -> {
            if (selectedSoundUris.isEmpty()) {
                Toast.makeText(this, "Please select at least one sound", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadSoundsAndSaveQuestion();
        });

        // Add option field
        btnAddOption.setOnClickListener(v -> addOptionField(null));
        addOptionField(null);

        // Register file picker
        pickSoundsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedSoundUris.clear();
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                selectedSoundUris.add(data.getClipData().getItemAt(i).getUri());
                            }
                        } else if (data.getData() != null) {
                            selectedSoundUris.add(data.getData());
                        }
                        soundAdapter.notifyDataSetChanged();
                    }
                });

        loadCategories();
    }

    private void pickSounds() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickSoundsLauncher.launch(Intent.createChooser(intent, "Select Sounds"));
    }

    private void uploadSoundsAndSaveQuestion() {
        List<String> uploadedUrls = new ArrayList<>();
        int total = selectedSoundUris.size();

        for (Uri uri : selectedSoundUris) {
            CloudService.uploadSound(this, uri, new UploadCallback() {
                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String url = resultData.get("secure_url").toString();
                    uploadedUrls.add(url);
                    if (uploadedUrls.size() == total) {
                        saveQuestionToDatabase(uploadedUrls);
                    }
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    runOnUiThread(() -> Toast.makeText(SoundGameAddQuestionsActivity.this,
                            "Upload error: " + error.getDescription(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onStart(String requestId) {
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                }
            });
        }
    }

    private void saveQuestionToDatabase(List<String> soundUrls) {
        List<String> options = collectOptions();
        String correctAnswer = spinnerCorrectAnswer.getSelectedItem().toString();
        String hint = ((EditText) findViewById(R.id.etHint)).getText().toString().trim();
        String explanation = ((EditText) findViewById(R.id.etExplanation)).getText().toString().trim();
        int difficulty = Integer.parseInt(((EditText) findViewById(R.id.etDifficulty)).getText().toString().trim());
        int timeLimit = Integer.parseInt(((EditText) findViewById(R.id.etTimeLimit)).getText().toString().trim());

        SoundQuestion question = new SoundQuestion();
        question.soundUrls  = soundUrls;
        question.options = options;
        question.correctAnswer = correctAnswer;
        question.hint = hint;
        question.explanation = explanation;
        question.difficulty = difficulty;
        question.timeLimitSeconds = timeLimit;

        int pos = spinnerCategory.getSelectedItemPosition();
        if (pos >= 0 && pos < categoryList.size()) {
            question.categoryId = categoryList.get(pos).id;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            SoundAppDatabase.getInstance(this).soundGameQuestionDao().insertAll(Collections.singletonList(question));
            runOnUiThread(() -> {
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            categoryList = SoundAppDatabase.getInstance(this).soundGameCategoryDao().getAll();
            List<String> names = new ArrayList<>();
            for (SoundCategory c : categoryList)
                names.add(c.name);

            runOnUiThread(() -> {
                categoryAdapter.clear();
                categoryAdapter.addAll(names);
                categoryAdapter.notifyDataSetChanged();
            });
        });
    }

    private void addOptionField(@Nullable String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        EditText et = new EditText(this);
        et.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        et.setHint("Option");
        if (text != null)
            et.setText(text);

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
            if (!text.isEmpty())
                options.add(text);
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