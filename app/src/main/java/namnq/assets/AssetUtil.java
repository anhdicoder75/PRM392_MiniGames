package namnq.assets;

import android.content.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import namnq.model.SoundItem;

public class AssetUtil {
    public static List<SoundItem> loadSoundsFromAssets(Context context) {
        List<SoundItem> soundItems = new ArrayList<>();
        try {
            String[] files = context.getAssets().list("sounds");
            if (files != null) {
                for (String fileName : files) {
                    String assetPath = "sounds/" + fileName;
                    soundItems.add(new SoundItem(fileName, assetPath, "General"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return soundItems;
    }
}