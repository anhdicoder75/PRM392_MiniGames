package ditd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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

import ditd.adapter.ImageGameSelectedImagesAdapter;
import ditd.db.AppDatabase;
import ditd.model.Category;
import ditd.model.Question;
import ditd.service.CloudService;

public class ImageGameAddQuestionsActivity extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST_CODE = 1001;
    private Button btnPickImages;
    private Button btnUploadAndSave;
    private RecyclerView rvSelectedImages;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private ImageGameSelectedImagesAdapter imageAdapter;
    private LinearLayout optionContainer;
    private Button btnAddOption;
    private List<EditText> optionEditTexts = new ArrayList<>();
    private Spinner spinnerCorrectAnswer;
    private Button btnClearImages;

    private ArrayAdapter<String> spinnerAdapter;

    private ActivityResultLauncher<Intent> pickImagesLauncher;


    private Spinner spinnerCategory;
    private ArrayAdapter<String> categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_activity_add_questions);
        initData();
    }

    private void initData() {
        optionContainer = findViewById(R.id.optionContainer);
        btnAddOption = findViewById(R.id.btnAddOption);
        btnClearImages = findViewById(R.id.btnClearImages);
        spinnerCorrectAnswer = findViewById(R.id.spinnerCorrectAnswer);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        btnPickImages = findViewById(R.id.btnPickImages);
        btnUploadAndSave = findViewById(R.id.btnUploadAndSave);
        //adapter for spinner
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrectAnswer.setAdapter(spinnerAdapter);

        //image recycler view adapter
        imageAdapter = new ImageGameSelectedImagesAdapter(selectedImageUris); // <<< Sử dụng class adapter bạn đã tạo
        rvSelectedImages.setAdapter(imageAdapter);


        btnAddOption.setOnClickListener(v -> addOptionField(null));

        btnPickImages.setOnClickListener(v -> pickImages());

        btnClearImages.setOnClickListener(v -> {
            selectedImageUris.clear();
            imageAdapter.notifyDataSetChanged();
        });


        addOptionField(null);
        rvSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        pickImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        selectedImageUris.clear();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                selectedImageUris.add(imageUri);
                            }
                        } else if (data.getData() != null) {
                            Uri imageUri = data.getData();
                            selectedImageUris.add(imageUri);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                }
        );
        btnUploadAndSave.setOnClickListener(v -> {
            if (selectedImageUris.isEmpty()) {
                Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImagesAndSaveQuestion();
        });


        spinnerCategory = findViewById(R.id.spinnerCategory);
        loadCategories();

    }


    private void uploadImagesAndSaveQuestion() {
        List<String> uploadedUrls = new ArrayList<>();
        int totalImages = selectedImageUris.size();

        for (Uri uri : selectedImageUris) {
            CloudService.uploadImage(this, uri, new UploadCallback() {
                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String url = resultData.get("secure_url").toString();
                    uploadedUrls.add(url);

                    if (uploadedUrls.size() == totalImages) {
                        saveQuestionToDatabase(uploadedUrls);
                    }
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    runOnUiThread(() -> Toast.makeText(ImageGameAddQuestionsActivity.this, "Error uploading image: " + error.getDescription(), Toast.LENGTH_SHORT).show());
                }

                @Override public void onStart(String requestId) {}
                @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                @Override public void onReschedule(String requestId, ErrorInfo error) {}
            });
        }
    }
    private void saveQuestionToDatabase(List<String> imageUrls) {
        List<String> options = collectOptions();
        String correctAnswer = spinnerCorrectAnswer.getSelectedItem().toString();
        String hint = ((EditText) findViewById(R.id.etHint)).getText().toString().trim();
        String questionContent = ((EditText) findViewById(R.id.etQuestionText)).getText().toString().trim();
        String explanation = ((EditText) findViewById(R.id.etExplanation)).getText().toString().trim();
        int difficulty = Integer.parseInt(((EditText) findViewById(R.id.etDifficulty)).getText().toString().trim());
        int timeLimit = Integer.parseInt(((EditText) findViewById(R.id.etTimeLimit)).getText().toString().trim());

        Question question = new Question();
        question.imageUrls = imageUrls;
        question.questionText = questionContent;
        question.options = options;
        question.correctAnswer = correctAnswer;
        question.hint = hint;
        question.explanation = explanation;
        question.difficulty = difficulty;
        question.timeLimitSeconds = timeLimit;
        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        if (categoryPosition >= 0 && categoryPosition < categoryList.size()) {
            question.categoryId = categoryList.get(categoryPosition).id;
        }



        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).imageGameQuestionDao().insertAll(Collections.singletonList(question));
            runOnUiThread(() -> {
                Toast.makeText(this, "Question saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            categoryList = AppDatabase.getInstance(getApplicationContext()).imageGameCategoryDao().getAll();
            List<String> categoryNames = new ArrayList<>();
            for (Category c : categoryList) categoryNames.add(c.name);

            runOnUiThread(() -> {
                categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
            });
        });
    }
    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImagesLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }


    private void addOptionField(@Nullable String text) {
        LinearLayout optionRow = new LinearLayout(this);
        optionRow.setOrientation(LinearLayout.HORIZONTAL);
        optionRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        editText.setHint("Option");
        if (text != null) editText.setText(text);

        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setBackground(null);
        btnDelete.setOnClickListener(v -> {
            optionContainer.removeView(optionRow);
            optionEditTexts.remove(editText);
            updateSpinnerOptions();
        });

        optionRow.addView(editText);
        optionRow.addView(btnDelete);
        optionContainer.addView(optionRow);

        optionEditTexts.add(editText);
        updateSpinnerOptions();
    }

    private List<String> collectOptions() {
        List<String> options = new ArrayList<>();
        for (EditText editText : optionEditTexts) {
            String text = editText.getText().toString().trim();
            if (!text.isEmpty()) {
                options.add(text);
            }
        }
        return options;
    }

    private void updateSpinnerOptions() {
        List<String> currentOptions = collectOptions();
        spinnerAdapter.clear();
        spinnerAdapter.addAll(currentOptions);
        spinnerAdapter.notifyDataSetChanged();
    }

}
