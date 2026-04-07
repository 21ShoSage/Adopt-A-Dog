package com.dogadoption.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dogadoption.app.R;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.database.SessionManager;
import com.dogadoption.app.models.User;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    private CircleImageView ivProfilePic;
    private String selectedImagePath = "";
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private ImagePickerHelper pickerHelper;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        ImagePickerHelper.persistUri(this, uri);
                        selectedImagePath = uri.toString();
                        Glide.with(this).load(uri).circleCrop()
                                .placeholder(R.drawable.ic_person_placeholder).into(ivProfilePic);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper      = DatabaseHelper.getInstance(this);
        session       = SessionManager.getInstance(this);
        pickerHelper  = new ImagePickerHelper(this, imagePickerLauncher);

        etName           = findViewById(R.id.et_name);
        etEmail          = findViewById(R.id.et_email);
        etPassword       = findViewById(R.id.et_password);
        etConfirmPassword= findViewById(R.id.et_confirm_password);
        etPhone          = findViewById(R.id.et_phone);
        etAddress        = findViewById(R.id.et_address);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLogin   = findViewById(R.id.tv_login);
        ivProfilePic       = findViewById(R.id.iv_profile_pic);

        ivProfilePic.setOnClickListener(v -> pickerHelper.checkAndPickImage());
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms, @NonNull int[] grants) {
        super.onRequestPermissionsResult(req, perms, grants);
        pickerHelper.onPermissionResult(req, grants);
    }

    private void attemptRegister() {
        String name    = etName.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String pw      = etPassword.getText().toString().trim();
        String pwConf  = etConfirmPassword.getText().toString().trim();
        String phone   = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name))  { etName.setError("Name required");  return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Email required"); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required"); return; }
        if (TextUtils.isEmpty(pw))    { etPassword.setError("Password required"); return; }
        if (pw.length() < 6)          { etPassword.setError("Min 6 characters");  return; }
        if (!pw.equals(pwConf))       { etConfirmPassword.setError("Passwords do not match"); return; }
        if (dbHelper.isEmailExists(email)) { etEmail.setError("Email already registered"); return; }

        User user = new User(name, email, pw, phone, address);
        user.setPhotoPath(selectedImagePath);
        long userId = dbHelper.registerUser(user);

        if (userId > 0) {
            session.createLoginSession((int) userId, email, name, selectedImagePath, "user");
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
