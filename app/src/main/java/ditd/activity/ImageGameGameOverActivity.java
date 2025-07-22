package ditd.activity;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.activities.SplashActivity;

public class ImageGameGameOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_game_game_over_activity);

//        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnBackHome = findViewById(R.id.btnBackHome);
        int finalScore = ImageGameHelpManager.getInstance().getScore();
//        GameScoreActivity. dbHelper = new GameDatabaseHelper(this);
//        btnRestart.setOnClickListener(v -> {
//            Intent intent = new Intent(this, ImageGamePlayActivity.class);
//            startActivity(intent);
//            finish();
//        });

        btnBackHome.setOnClickListener(v -> {
            ImageGameHelpManager.getInstance().resetHelps();
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
