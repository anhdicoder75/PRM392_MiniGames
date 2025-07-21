package com.example.prm392_minigames.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.example.prm392_minigames.R;

public class AdminLoginActivity extends Activity {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "1234";

    EditText edtUsername, edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        edtUsername = findViewById(R.id.edt_admin_username);
        edtPassword = findViewById(R.id.edt_admin_password);
        btnLogin = findViewById(R.id.btn_admin_login);

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                startActivity(new Intent(this, AdminGameCrudActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
