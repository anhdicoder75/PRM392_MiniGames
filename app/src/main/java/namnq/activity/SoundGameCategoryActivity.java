package namnq.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;

import java.util.List;
import java.util.concurrent.Executors;

import namnq.adapter.SoundGameCategoryAdapter;
import namnq.dao.SoundGameCategoryDao;
import namnq.db.SoundAppDatabase;
import namnq.model.SoundCategory;

public class SoundGameCategoryActivity extends AppCompatActivity {

    private EditText etCategoryName;
    private EditText etCategoryDescription;
    private Button btnAddCategory;
    private RecyclerView rvCategories;

    private SoundGameCategoryAdapter adapter;
    private List<SoundCategory> categoryList;
    private SoundGameCategoryDao soundGameCategoryDao;
    private SoundAppDatabase db;

    private SoundCategory editingCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_game_activity_category);

        etCategoryName = findViewById(R.id.etCategoryName);
        etCategoryDescription = findViewById(R.id.etCategoryDescription);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        rvCategories = findViewById(R.id.rvCategories);

        db = SoundAppDatabase.getInstance(getApplicationContext());
        soundGameCategoryDao = db.soundGameCategoryDao();

        loadCategories();

        btnAddCategory.setOnClickListener(v -> {
            String name = etCategoryName.getText().toString().trim();
            String description = etCategoryDescription.getText().toString().trim();
            if (name.isEmpty())
                return;

            Executors.newSingleThreadExecutor().execute(() -> {
                if (editingCategory == null) {
                    soundGameCategoryDao.insert(new SoundCategory(name, description));
                } else {
                    editingCategory.name = name;
                    editingCategory.description = description;
                    soundGameCategoryDao.update(editingCategory);
                    editingCategory = null;
                }

                runOnUiThread(() -> {
                    etCategoryName.setText("");
                    etCategoryDescription.setText("");
                    loadCategories();
                });
            });
        });
    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<SoundCategory> result = soundGameCategoryDao.getAll();

            runOnUiThread(() -> {
                categoryList = result;

                if (adapter == null) {
                    adapter = new SoundGameCategoryAdapter(categoryList,
                            new SoundGameCategoryAdapter.OnCategoryClickListener() {
                                @Override
                                public void onEdit(SoundCategory category) {
                                    etCategoryName.setText(category.name);
                                    etCategoryDescription.setText(category.description);
                                    editingCategory = category;
                                }

                                @Override
                                public void onDelete(SoundCategory category) {
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        soundGameCategoryDao.delete(category);
                                        loadCategories();
                                    });
                                }
                            });
                    rvCategories.setLayoutManager(new LinearLayoutManager(SoundGameCategoryActivity.this));
                    rvCategories.setAdapter(adapter);
                } else {
                    adapter.updateList(categoryList);
                }
            });
        });
    }
}
