package ditd.activity;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class ImageGameHelpManager {
    private static ImageGameHelpManager instance;
    public static ImageGameHelpManager getInstance() {
        if (instance == null) {
            instance = new ImageGameHelpManager();
        }
        return instance;
    }
    private boolean hintUsed = false;
    private int score = 0;
    private boolean skipUsed = false;
    private boolean used5050 = false;
    private int lives = 5;
    private static final int MAX_LIVES = 5;
    public int getLives() {
        return lives;
    }

    public void resetLives() {
        lives = MAX_LIVES;
    }
    public int getScore() {
        return score;
    }
    public void increaseScore() {
        score = score +50;
    }
    public boolean loseLife() {
        if (lives > 0) {
            lives--;
            return true; // còn mạng
        }
        return false; // hết mạng
    }

    public boolean gainLife() {
        if (lives < MAX_LIVES) {
            lives++;
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }
    public boolean canUseHint() {
        return !hintUsed;
    }

    public boolean canUseSkip() {
        return !skipUsed;
    }

    public boolean canUse5050() {
        return !used5050;
    }

    public void useHint() {
        hintUsed = true;
    }

    public void useSkip() {
        skipUsed = true;
    }

    public void use5050() {
        used5050 = true;
    }


    public void resetHelps() {
        hintUsed = false;
        skipUsed = false;
        used5050 = false;
        lives=5;
        score = 0;
    }
}
