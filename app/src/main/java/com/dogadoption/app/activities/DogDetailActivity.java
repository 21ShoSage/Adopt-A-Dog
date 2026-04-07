package com.dogadoption.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dogadoption.app.R;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.database.SessionManager;
import com.dogadoption.app.models.AdoptionRecord;
import com.dogadoption.app.models.Dog;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DogDetailActivity extends AppCompatActivity {

    private ImageView ivDogPhoto;
    private TextView tvName, tvBreed, tvBloodType, tvHeight, tvWeight,
            tvColor, tvAge, tvGender, tvDescription, tvDateAdded;
    private Chip chipStatus;
    private Button btnEdit, btnDelete, btnAdopt;
    private LinearLayout layoutAdminButtons;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private Dog currentDog;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DatabaseHelper.getInstance(this);
        session  = SessionManager.getInstance(this);
        isAdmin  = getIntent().getBooleanExtra("is_admin", session.isAdmin());

        ivDogPhoto        = findViewById(R.id.iv_dog_photo);
        tvName            = findViewById(R.id.tv_dog_name);
        tvBreed           = findViewById(R.id.tv_breed);
        tvBloodType       = findViewById(R.id.tv_blood_type);
        tvHeight          = findViewById(R.id.tv_height);
        tvWeight          = findViewById(R.id.tv_weight);
        tvColor           = findViewById(R.id.tv_color);
        tvAge             = findViewById(R.id.tv_age);
        tvGender          = findViewById(R.id.tv_gender);
        tvDescription     = findViewById(R.id.tv_description);
        tvDateAdded       = findViewById(R.id.tv_date_added);
        chipStatus        = findViewById(R.id.chip_status);
        btnEdit           = findViewById(R.id.btn_edit);
        btnDelete         = findViewById(R.id.btn_delete);
        btnAdopt          = findViewById(R.id.btn_adopt);
        layoutAdminButtons= findViewById(R.id.layout_admin_buttons);

        // Show Edit/Delete only for admins
        layoutAdminButtons.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        int dogId = getIntent().getIntExtra("dog_id", -1);
        if (dogId == -1) { finish(); return; }
        loadDog(dogId);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditDogActivity.class);
            intent.putExtra("dog_id", currentDog.getId());
            startActivity(intent);
        });
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnAdopt.setOnClickListener(v -> confirmAdopt());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentDog != null) loadDog(currentDog.getId());
    }

    private void loadDog(int dogId) {
        currentDog = dbHelper.getDogById(dogId);
        if (currentDog == null) { finish(); return; }

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(currentDog.getName());

        if (currentDog.getPhotoPath() != null && !currentDog.getPhotoPath().isEmpty()) {
            try {
                Glide.with(this).load(Uri.parse(currentDog.getPhotoPath())).centerCrop()
                        .placeholder(R.drawable.ic_dog_placeholder)
                        .error(R.drawable.ic_dog_placeholder).into(ivDogPhoto);
            } catch (Exception e) {
                ivDogPhoto.setImageResource(R.drawable.ic_dog_placeholder);
            }
        } else {
            ivDogPhoto.setImageResource(R.drawable.ic_dog_placeholder);
        }

        tvName.setText(currentDog.getName());
        tvBreed.setText(val(currentDog.getBreed()));
        tvBloodType.setText(val(currentDog.getBloodType()));
        tvHeight.setText(currentDog.getHeight() > 0 ? currentDog.getHeight() + " cm" : "N/A");
        tvWeight.setText(currentDog.getWeight() > 0 ? currentDog.getWeight() + " kg" : "N/A");
        tvColor.setText(val(currentDog.getColor()));
        tvAge.setText(currentDog.getAge() > 0 ? currentDog.getAge() + " years" : "Unknown");
        tvGender.setText(val(currentDog.getGender()));
        tvDescription.setText((currentDog.getDescription() != null && !currentDog.getDescription().isEmpty())
                ? currentDog.getDescription() : "No description available.");
        tvDateAdded.setText(currentDog.getDateAdded() != null ? currentDog.getDateAdded() : "N/A");

        if (currentDog.isAdopted()) {
            chipStatus.setText("Adopted");
            chipStatus.setChipBackgroundColorResource(R.color.status_adopted);
            btnAdopt.setEnabled(false);
            btnAdopt.setText("Already Adopted");
        } else {
            chipStatus.setText("Available");
            chipStatus.setChipBackgroundColorResource(R.color.status_available);
            // Users can adopt; admins can also adopt for testing
            btnAdopt.setEnabled(true);
            btnAdopt.setText("Adopt This Dog");
            // If admin is viewing, hide the adopt button (admins don't adopt)
            btnAdopt.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
        }
        // If already adopted and admin, hide adopt button too
        if (isAdmin) btnAdopt.setVisibility(View.GONE);
    }

    private String val(String s) { return (s != null && !s.isEmpty()) ? s : "Unknown"; }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Dog")
                .setMessage("Delete " + currentDog.getName() + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    dbHelper.deleteDog(currentDog.getId());
                    Toast.makeText(this, currentDog.getName() + " deleted.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null).show();
    }

    private void confirmAdopt() {
        new AlertDialog.Builder(this)
                .setTitle("Adopt " + currentDog.getName())
                .setMessage("Confirm adoption of " + currentDog.getName() + "?")
                .setPositiveButton("Confirm", (d, w) -> {
                    AdoptionRecord record = new AdoptionRecord();
                    record.setUserId(session.getUserId());
                    record.setDogId(currentDog.getId());
                    record.setAdoptionDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()).format(new Date()));
                    record.setNotes("Adopted via app");
                    if (dbHelper.addAdoptionRecord(record) > 0) {
                        Toast.makeText(this, "Congratulations! You adopted "
                                + currentDog.getName(), Toast.LENGTH_LONG).show();
                        loadDog(currentDog.getId());
                    }
                })
                .setNegativeButton("Cancel", null).show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
