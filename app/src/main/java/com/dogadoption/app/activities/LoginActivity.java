package com.dogadoption.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dogadoption.app.R;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.database.SessionManager;
import com.dogadoption.app.models.User;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = DatabaseHelper.getInstance(this);
        session  = SessionManager.getInstance(this);

        etEmail    = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin   = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email))    { etEmail.setError("Email is required");    return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Password is required"); return; }

        User user = dbHelper.loginUser(email, password);
        if (user != null) {
            session.createLoginSession(
                    user.getId(), user.getEmail(), user.getName(),
                    user.getPhotoPath(), user.getRole());

            // Route to the correct home screen based on role
            if (user.isAdmin()) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
