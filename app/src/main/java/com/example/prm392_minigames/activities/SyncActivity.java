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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;

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
                        Toast.makeText(this, "Đăng nhập thành công! Bắt đầu đồng bộ...", Toast.LENGTH_SHORT).show();
                        syncProfileToFirebase();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi xác thực Firebase!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void syncProfileToFirebase() {
        AppDatabaseHelper db = new AppDatabaseHelper(this);
        String name = null;
        try (Cursor c = db.getProfile()) {
            if (c != null && c.moveToFirst()) {
                name = c.getString(c.getColumnIndexOrThrow(AppDatabaseHelper.COL_NAME));
            }
        }
        if (name != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("profiles")
                    .child(uid)
                    .setValue(new UserProfile(name));
        }
    }

    public static class UserProfile {
        public String name;
        public UserProfile() {}
        public UserProfile(String name) {
            this.name = name;
        }
    }
}
