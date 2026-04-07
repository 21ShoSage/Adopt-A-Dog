package com.dogadoption.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dogadoption.app.R;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.database.SessionManager;
import com.dogadoption.app.models.User;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView ivProfilePic;
    private TextInputEditText etName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private User currentUser;
    private String selectedImagePath = "";
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
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        dbHelper     = DatabaseHelper.getInstance(this);
        session      = SessionManager.getInstance(this);
        pickerHelper = new ImagePickerHelper(this, imagePickerLauncher);

        ivProfilePic     = findViewById(R.id.iv_profile_pic);
        etName           = findViewById(R.id.et_name);
        etEmail          = findViewById(R.id.et_email);
        etPhone          = findViewById(R.id.et_phone);
        etAddress        = findViewById(R.id.et_address);
        etPassword       = findViewById(R.id.et_password);
        etConfirmPassword= findViewById(R.id.et_confirm_password);
        Button btnSave   = findViewById(R.id.btn_save);

        loadUserData();
        ivProfilePic.setOnClickListener(v -> pickerHelper.checkAndPickImage());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms, @NonNull int[] grants) {
        super.onRequestPermissionsResult(req, perms, grants);
        pickerHelper.onPermissionResult(req, grants);
    }

    private void loadUserData() {
        currentUser = dbHelper.getUserById(session.getUserId());
        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhone());
            etAddress.setText(currentUser.getAddress());
            selectedImagePath = currentUser.getPhotoPath() != null ? currentUser.getPhotoPath() : "";
            if (!selectedImagePath.isEmpty()) {
                Glide.with(this).load(Uri.parse(selectedImagePath)).circleCrop()
                        .placeholder(R.drawable.ic_person_placeholder)
                        .error(R.drawable.ic_person_placeholder).into(ivProfilePic);
            }
        }
    }

    private void saveProfile() {
        String name    = etName.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String phone   = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pw      = etPassword.getText().toString().trim();
        String pwConf  = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name))  { etName.setError("Name required");  return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Email required"); return; }
        if (!pw.isEmpty() && !pw.equals(pwConf)) {
            etConfirmPassword.setError("Passwords do not match"); return; }

        currentUser.setName(name); currentUser.setEmail(email);
        currentUser.setPhone(phone); currentUser.setAddress(address);
        currentUser.setPhotoPath(selectedImagePath);
        if (!pw.isEmpty()) currentUser.setPassword(pw);

        int result = dbHelper.updateUser(currentUser);
        if (result > 0) {
            session.createLoginSession(currentUser.getId(), email, name,
                    selectedImagePath, currentUser.getRole());
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
