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
                if (editingCategory == null) {
                    imageGameCategoryDao.insert(new Category(name, description));
                } else {
                    editingCategory.name = name;
                    editingCategory.description = description;
                    imageGameCategoryDao.update(editingCategory);
                    editingCategory = null;
                }

                runOnUiThread(() -> {
                    etCategoryName.setText("");
                    etCategoryDescription.setText("");
                    loadCategories(); // phải gọi trong UI thread
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
                        }

                        @Override
                        public void onDelete(Category category) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                imageGameCategoryDao.delete(category); // xóa ở background
                                loadCategories(); // gọi lại sau khi xóa
                            });
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
