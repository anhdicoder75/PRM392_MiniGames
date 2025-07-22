package ditd.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.concurrent.Executors;

import ditd.adapter.ImageGameCategoryAdapter;
import ditd.dao.ImageGameCategoryDao;
import ditd.db.AppDatabase;
import ditd.model.Category;

public class ImageGameCategoryActivity extends AppCompatActivity {

    private EditText etCategoryName;
    private EditText etCategoryDescription;
    private Button btnAddCategory;
    private RecyclerView rvCategories;
    private Button btnUpdateCategory;
    private ImageGameCategoryAdapter adapter;
    private List<Category> categoryList;
    private ImageGameCategoryDao imageGameCategoryDao;
    private AppDatabase db;

    private Category editingCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_activity_category);

        etCategoryName = findViewById(R.id.etCategoryName);
        etCategoryDescription = findViewById(R.id.etCategoryDescription);

         btnUpdateCategory = findViewById(R.id.btnUpdateCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        rvCategories = findViewById(R.id.rvCategories);

        db = AppDatabase.getInstance(getApplicationContext());
        imageGameCategoryDao = db.imageGameCategoryDao();


        loadCategories();

        btnAddCategory.setOnClickListener(v -> {
            String name = etCategoryName.getText().toString().trim();
            String description = etCategoryDescription.getText().toString().trim();
            if (name.isEmpty()) return;

            Executors.newSingleThreadExecutor().execute(() -> {
                imageGameCategoryDao.insert(new Category(name, description));
                runOnUiThread(() -> {
                    etCategoryName.setText("");
                    etCategoryDescription.setText("");
                    loadCategories();
                });
            });
        });

        btnUpdateCategory.setOnClickListener(v -> {
            if (editingCategory == null) return;

            String name = etCategoryName.getText().toString().trim();
            String description = etCategoryDescription.getText().toString().trim();
            if (name.isEmpty()) return;

            Executors.newSingleThreadExecutor().execute(() -> {
                editingCategory.name = name;
                editingCategory.description = description;
                imageGameCategoryDao.update(editingCategory);

                runOnUiThread(() -> {
                    editingCategory = null;
                    etCategoryName.setText("");
                    etCategoryDescription.setText("");

                    btnUpdateCategory.setEnabled(false);
                    btnUpdateCategory.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
                    btnAddCategory.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));

                    loadCategories();
                });
            });
        });

    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> result = imageGameCategoryDao.getAll();

            runOnUiThread(() -> {
                categoryList = result;

                if (adapter == null) {
                    adapter = new ImageGameCategoryAdapter(categoryList, new ImageGameCategoryAdapter.OnCategoryClickListener() {
                        @Override
                        public void onEdit(Category category) {
                            etCategoryName.setText(category.name);
                            etCategoryDescription.setText(category.description);
                            editingCategory = category;

                            btnUpdateCategory.setEnabled(true);
                            btnUpdateCategory.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));
                            btnAddCategory.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
                        }
                        @Override
                        public void onDelete(Category category) {
                        }

                    });
                    rvCategories.setLayoutManager(new LinearLayoutManager(ImageGameCategoryActivity.this));
                    rvCategories.setAdapter(adapter);
                } else {
                    adapter.updateList(categoryList);
                }
            });
        });
    }


}
