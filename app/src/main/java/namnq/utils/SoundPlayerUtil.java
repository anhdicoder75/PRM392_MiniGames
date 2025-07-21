package namnq.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class SoundPlayerUtil {

    private static SoundPlayerUtil instance;
    private MediaPlayer mediaPlayer;

    private SoundPlayerUtil() {
        // private constructor to enforce singleton
    }

    public static SoundPlayerUtil getInstance() {
        if (instance == null) {
            instance = new SoundPlayerUtil();
        }
        return instance;
    }

    /**
     * Play sound file from assets folder
     *
     * @param context   context from Activity or Adapter
     * @param assetPath path to file inside assets folder, e.g. "sounds/traffic_sound.mp3"
     */
    public void playSoundFromAssets(Context context, String assetPath) {
        stopSound(); // Stop any currently playing sound

        try {
            AssetFileDescriptor afd = context.getAssets().openFd(assetPath);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });

        } catch (IOException e) {
            Log.e("SoundPlayerUtil", "Error playing sound: " + assetPath, e);
        }
    }

    /**
     * Stop current playing sound if any
     */
    public void stopSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
