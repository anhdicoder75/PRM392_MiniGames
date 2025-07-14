package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        syncProfileFromCloudOrPushLocal();
                    } else {
                        Toast.makeText(this, "Lỗi xác thực Firebase!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void syncProfileFromCloudOrPushLocal() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                .getReference("profiles").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AppDatabaseHelper db = new AppDatabaseHelper(SyncActivity.this);
                if (snapshot.exists()) {
                    // Đã từng sync, lấy về local
                    UserProfile cloud = snapshot.getValue(UserProfile.class);
                    db.clearProfile();
                    db.insertProfile(cloud.name != null ? cloud.name : "Người chơi");
                    db.updateAvatarUri(cloud.avatarUri);
                    db.updateFrame(cloud.frame);
                    db.updatePoint(cloud.point);
                    if (cloud.ownedFrames != null) db.setAllOwnedFrames(cloud.ownedFrames);
                    Toast.makeText(SyncActivity.this, "Đã đồng bộ tài khoản từ Cloud!", Toast.LENGTH_SHORT).show();
                } else {
                    // Chưa có, upload local lên cloud
                    Cursor c = db.getProfile();
                    String name = "Người chơi", avatarUri = null;
                    int frame = 0, point = 0;
                    if (c != null && c.moveToFirst()) {
                        name = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_NAME));
                        avatarUri = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_AVATAR));
                        frame = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_FRAME));
                        point = c.getInt(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_POINT));
                    }
                    List<Integer> ownedFrames = db.getAllOwnedFrames();
                    UserProfile local = new UserProfile(name, avatarUri, frame, point, ownedFrames);
                    userRef.setValue(local);
                    Toast.makeText(SyncActivity.this, "Chưa có dữ liệu Cloud, đã upload hồ sơ hiện tại!", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(SyncActivity.this, MainActivity.class));
                finish();
            }
            @Override public void onCancelled(DatabaseError error) {
                Toast.makeText(SyncActivity.this, "Lỗi kết nối Cloud!", Toast.LENGTH_SHORT).show();
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
