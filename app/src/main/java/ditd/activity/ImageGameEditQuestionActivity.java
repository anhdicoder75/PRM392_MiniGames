package ditd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import ditd.adapter.ImageGameSelectedImagesAdapter;
import ditd.adapter.ImageGameUrlAdapter;
import ditd.dao.ImageGameCategoryDao;
import ditd.dao.ImageGameQuestionDao;
import ditd.db.AppDatabase;
import ditd.model.Category;
import ditd.model.Question;
import ditd.service.CloudService;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

public class ImageGameEditQuestionActivity extends AppCompatActivity {

    private EditText etQuestionText, etHint, etExplanation, etDifficulty, etTimeLimit;
    private Spinner spinnerCorrectAnswer, spinnerCategory;
    private Button btnSaveChanges, btnPickImages, btnClearImages, btnAddOption;
    private RecyclerView rvSelectedImages, rvOldImages;
    private LinearLayout optionContainer;

    private List<String> oldImageUrls = new ArrayList<>();
    private List<Uri> selectedImageUris = new ArrayList<>();
    private List<EditText> optionEditTexts = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    private ImageGameSelectedImagesAdapter imageAdapter;
    private Question currentQuestion;
    private int questionId;

    private ImageGameQuestionDao questionDao;
    private ImageGameCategoryDao categoryDao;

    private ActivityResultLauncher<Intent> pickImagesLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_edit_question);

        initViews();
        setupRecyclerView();
        setupImagePicker();

        questionDao = AppDatabase.getInstance(this).imageGameQuestionDao();
        categoryDao = AppDatabase.getInstance(this).imageGameCategoryDao();

        questionId = getIntent().getIntExtra("question_id", -1);
        if (questionId == -1) {
            Toast.makeText(this, "Không có câu hỏi để chỉnh sửa!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCategoriesAndQuestion();

        btnPickImages.setOnClickListener(v -> pickImages());
        btnClearImages.setOnClickListener(v -> {
            selectedImageUris.clear();
            imageAdapter.notifyDataSetChanged();
        });

        btnAddOption.setOnClickListener(v -> addOptionField(""));

        btnSaveChanges.setOnClickListener(v -> {
            if (!selectedImageUris.isEmpty()) {
                uploadImagesAndSave();
            } else {
                saveChanges(null);
            }
        });
    }

    private void initViews() {
        etQuestionText = findViewById(R.id.etQuestionText);
        etHint = findViewById(R.id.etHint);
        etExplanation = findViewById(R.id.etExplanation);
        etDifficulty = findViewById(R.id.etDifficulty);
        etTimeLimit = findViewById(R.id.etTimeLimit);
        spinnerCorrectAnswer = findViewById(R.id.spinnerCorrectAnswer);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnPickImages = findViewById(R.id.btnPickImages);
        btnClearImages = findViewById(R.id.btnClearImages);
        btnAddOption = findViewById(R.id.btnAddOption);

        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        rvOldImages = findViewById(R.id.rvOldImages);
        optionContainer = findViewById(R.id.optionContainer);
    }

    private void setupRecyclerView() {
        rvSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageGameSelectedImagesAdapter(selectedImageUris);
        rvSelectedImages.setAdapter(imageAdapter);
    }

    private void setupImagePicker() {
        pickImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUris.clear();
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                selectedImageUris.add(uri);
                            }
                        } else if (data.getData() != null) {
                            selectedImageUris.add(data.getData());
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImagesLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
    }

    private void loadCategoriesAndQuestion() {
        Executors.newSingleThreadExecutor().execute(() -> {
            categoryList = categoryDao.getAll();
            currentQuestion = questionDao.getById(questionId);

            runOnUiThread(() -> {
                setupCategorySpinner();
                if (currentQuestion != null) populateQuestion();
            });
        });
    }

    private void setupCategorySpinner() {
        List<String> names = new ArrayList<>();
        for (Category cat : categoryList) names.add(cat.name);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        if (currentQuestion != null) {
            int index = findCategoryIndexById(currentQuestion.categoryId);
            if (index >= 0) spinnerCategory.setSelection(index);
        }
    }

    private int findCategoryIndexById(int id) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).id == id) return i;
        }
        return -1;
    }

    private void populateQuestion() {
        etQuestionText.setText(currentQuestion.questionText);
        etHint.setText(currentQuestion.hint);
        etExplanation.setText(currentQuestion.explanation);
        etDifficulty.setText(String.valueOf(currentQuestion.difficulty));
        etTimeLimit.setText(String.valueOf(currentQuestion.timeLimitSeconds));

        if (currentQuestion.options != null) {
            for (String option : currentQuestion.options) addOptionField(option);
        }

        if (currentQuestion.imageUrls != null) {
            oldImageUrls = currentQuestion.imageUrls;
            ImageGameUrlAdapter oldImagesAdapter = new ImageGameUrlAdapter(oldImageUrls);
            rvOldImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvOldImages.setAdapter(oldImagesAdapter);
        }

        updateCorrectAnswerSpinner(currentQuestion.options, currentQuestion.correctAnswer);
    }

    private void addOptionField(String text) {
        EditText et = new EditText(this);
        et.setHint("Lựa chọn");
        et.setText(text);
        et.setPadding(24, 16, 24, 16);
        optionContainer.addView(et);
        optionEditTexts.add(et);
        updateCorrectAnswerSpinner(getOptionsFromUI(), currentQuestion != null ? currentQuestion.correctAnswer : "");
    }

    private List<String> getOptionsFromUI() {
        List<String> list = new ArrayList<>();
        for (EditText et : optionEditTexts) {
            String val = et.getText().toString().trim();
            if (!val.isEmpty()) list.add(val);
        }
        return list;
    }

    private void updateCorrectAnswerSpinner(List<String> options, String selected) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrectAnswer.setAdapter(adapter);
        int idx = options.indexOf(selected);
        if (idx >= 0) spinnerCorrectAnswer.setSelection(idx);
    }

    private void uploadImagesAndSave() {
        List<String> uploadedUrls = new ArrayList<>();
        int total = selectedImageUris.size();

        for (Uri uri : selectedImageUris) {
            CloudService.uploadImage(this, uri, new UploadCallback() {
                @Override
                public void onSuccess(String requestId, Map resultData) {
                    uploadedUrls.add(resultData.get("secure_url").toString());
                    if (uploadedUrls.size() == total) {
                        saveChanges(uploadedUrls);
                    }
                }

                @Override public void onError(String requestId, ErrorInfo error) {
                    runOnUiThread(() -> Toast.makeText(ImageGameEditQuestionActivity.this, "Lỗi upload ảnh: " + error.getDescription(), Toast.LENGTH_SHORT).show());
                }

                @Override public void onStart(String requestId) {}
                @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                @Override public void onReschedule(String requestId, ErrorInfo error) {}
            });
        }
    }

    private void saveChanges(@Nullable List<String> imageUrls) {
        String questionText = etQuestionText.getText().toString().trim();
        String hint = etHint.getText().toString().trim();
        String explanation = etExplanation.getText().toString().trim();
        String difficultyStr = etDifficulty.getText().toString().trim();
        String timeStr = etTimeLimit.getText().toString().trim();
        List<String> options = getOptionsFromUI();

        if (questionText.isEmpty() || options.size() < 2) {
            Toast.makeText(this, "Vui lòng nhập câu hỏi và ít nhất 2 lựa chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        String correctAnswer = spinnerCorrectAnswer.getSelectedItem() != null
                ? spinnerCorrectAnswer.getSelectedItem().toString()
                : "";

        if (!options.contains(correctAnswer)) {
            Toast.makeText(this, "Đáp án đúng không nằm trong lựa chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            currentQuestion.questionText = questionText;
            currentQuestion.hint = hint;
            currentQuestion.explanation = explanation;
            currentQuestion.difficulty = Integer.parseInt(difficultyStr);
            currentQuestion.timeLimitSeconds = Integer.parseInt(timeStr);
            currentQuestion.options = options;
            currentQuestion.correctAnswer = correctAnswer;
            currentQuestion.categoryId = categoryList.get(spinnerCategory.getSelectedItemPosition()).id;

            if (imageUrls != null) {
                currentQuestion.imageUrls = imageUrls;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                questionDao.insertAll(List.of(currentQuestion));
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Độ khó và thời gian phải là số", Toast.LENGTH_SHORT).show();
        }
    }
}