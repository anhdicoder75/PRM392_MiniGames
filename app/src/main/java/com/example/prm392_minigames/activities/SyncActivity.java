package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.prm392_minigames.R;
import com.example.prm392_minigames.db.AppDatabaseHelper;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import java.util.List;
import java.util.ArrayList;

public class SyncActivity extends Activity {
    private static final int RC_SIGN_IN = 999;
    private static final String TAG = "SyncActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_google_signin).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase authentication successful");
                        syncProfileFromCloudOrPushLocal();
                    } else {
                        Toast.makeText(this, "Lỗi xác thực Firebase!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Firebase auth failed", task.getException());
                    }
                });
    }

    private void syncProfileFromCloudOrPushLocal() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "Chưa đăng nhập Firebase!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Firebase UID is null");
            return;
        }

        // Sử dụng đúng URL realtime DB theo region của bạn
        DatabaseReference userRef = FirebaseDatabase.getInstance(
                        "https://prm392minigames-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("profiles").child(uid);

        Log.d(TAG, "Syncing profile for UID: " + uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AppDatabaseHelper db = new AppDatabaseHelper(SyncActivity.this);
                if (snapshot.exists()) {
                    // Đã từng sync, lấy về local
                    UserProfile cloud = snapshot.getValue(UserProfile.class);
                    if (cloud != null) {
                        db.clearProfile();
                        db.insertProfile(cloud.name != null ? cloud.name : "Người chơi");
                        db.updateAvatarUri(cloud.avatarUri);
                        db.updateFrame(cloud.frame);
                        db.updatePoint(cloud.point);
                        if (cloud.ownedFrames != null) db.setAllOwnedFrames(cloud.ownedFrames);
                        Toast.makeText(SyncActivity.this, "Đã đồng bộ tài khoản từ Cloud!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Profile synced from cloud");
                    } else {
                        Toast.makeText(SyncActivity.this, "Dữ liệu cloud không hợp lệ!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Cloud data snapshot null");
                    }
                } else {
                    // Chưa có, upload local lên cloud
                    Cursor c = db.getProfile();
                    String name = "Người chơi";
                    String avatarUri = null;
                    int frame = 0;
                    int point = 0;
                    if (c != null) {
                        try {
                            if (c.moveToFirst()) {
                                name = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_NAME));
                                avatarUri = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_AVATAR));
                                frame = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_FRAME));
                                point = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
                            }
                        } finally {
                            c.close(); // đóng cursor tránh leak
                        }
                    }
                    List<Integer> ownedFrames = db.getAllOwnedFrames();
                    UserProfile local = new UserProfile(name, avatarUri, frame, point, ownedFrames);
                    userRef.setValue(local);
                    Toast.makeText(SyncActivity.this, "Chưa có dữ liệu Cloud, đã upload hồ sơ hiện tại!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Profile uploaded to cloud");
                }
                startActivity(new Intent(SyncActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SyncActivity.this, "Lỗi kết nối Cloud: " + error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Firebase database error", error.toException());
            }
        });
    }

    public static class UserProfile {
        public String name;
        public String avatarUri;
        public int frame;
        public int point;
        public List<Integer> ownedFrames;

        public UserProfile() {}

        public UserProfile(String name, String avatarUri, int frame, int point, List<Integer> ownedFrames) {
            this.name = name;
            this.avatarUri = avatarUri;
            this.frame = frame;
            this.point = point;
            this.ownedFrames = ownedFrames;
        }
    }
}
