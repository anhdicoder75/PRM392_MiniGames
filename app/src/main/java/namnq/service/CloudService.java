package namnq.service;

import android.content.Context;
import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.UploadPolicy;
import com.example.prm392_minigames.R;

import java.util.HashMap;
import java.util.Map;

public class CloudService {
    private static boolean isInitialized = false;
    public static void init(Context context){
        if (!isInitialized){
            Map<String,String> config = new HashMap<>();
            config.put("cloud_name", context.getString(R.string.cloudinary_cloud_name));
            config.put("api_key", context.getString(R.string.cloudinary_api_key));
            config.put("api_secret", context.getString(R.string.cloudinary_api_secret));

            MediaManager.init(context, config);
            isInitialized = true;
        }
    }
    public static void uploadSound(Context context, Uri fileUri, UploadCallback callback) {
        init(context);

        MediaManager.get().upload(fileUri)
                .option("resource_type", "video")
                .policy(new UploadPolicy.Builder()
                        .maxRetries(2)
                        .build())
                .callback(callback)
                .dispatch();
    }

}
