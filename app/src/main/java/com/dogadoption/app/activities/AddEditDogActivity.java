package com.dogadoption.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dogadoption.app.R;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.models.Dog;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditDogActivity extends AppCompatActivity {

    private TextInputEditText etName, etHeight, etWeight, etAge, etColor, etDescription;
    private AutoCompleteTextView actvBreed, actvBloodType, actvGender;
    private Switch switchAdopted;
    private LinearLayout layoutAdoptedToggle;
    private ImageView ivDogPhoto;
    private String selectedImagePath = "";
    private DatabaseHelper dbHelper;
    private Dog editingDog = null;
    private ImagePickerHelper pickerHelper;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        ImagePickerHelper.persistUri(this, uri);
                        selectedImagePath = uri.toString();
                        Glide.with(this).load(uri).centerCrop()
                                .placeholder(R.drawable.ic_dog_placeholder).into(ivDogPhoto);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_dog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper     = DatabaseHelper.getInstance(this);
        pickerHelper = new ImagePickerHelper(this, imagePickerLauncher);

        TextView tvTitle        = findViewById(R.id.tv_form_title);
        etName                  = findViewById(R.id.et_dog_name);
        actvBreed               = findViewById(R.id.actv_breed);
        actvBloodType           = findViewById(R.id.actv_blood_type);
        etHeight                = findViewById(R.id.et_height);
        etWeight                = findViewById(R.id.et_weight);
        etAge                   = findViewById(R.id.et_age);
        etColor                 = findViewById(R.id.et_color);
        actvGender              = findViewById(R.id.actv_gender);
        etDescription           = findViewById(R.id.et_description);
        switchAdopted           = findViewById(R.id.switch_adopted);
        layoutAdoptedToggle     = findViewById(R.id.layout_adopted_toggle);
        ivDogPhoto              = findViewById(R.id.iv_dog_photo);
        Button btnSave          = findViewById(R.id.btn_save);

        setupDropdowns();
        ivDogPhoto.setOnClickListener(v -> pickerHelper.checkAndPickImage());

        int dogId = getIntent().getIntExtra("dog_id", -1);
        if (dogId != -1) {
            editingDog = dbHelper.getDogById(dogId);
            if (editingDog != null) populateFields(editingDog);
            tvTitle.setText("Edit Dog");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Dog");
            layoutAdoptedToggle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("Add New Dog");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Add New Dog");
            layoutAdoptedToggle.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveDog());
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms, @NonNull int[] grants) {
        super.onRequestPermissionsResult(req, perms, grants);
        pickerHelper.onPermissionResult(req, grants);
    }

    private void setupDropdowns() {
        String[] breeds = {"Labrador Retriever","German Shepherd","Golden Retriever","Bulldog",
                "Poodle","Beagle","Rottweiler","Yorkshire Terrier","Boxer","Dachshund",
                "Siberian Husky","Shih Tzu","Doberman","Pomeranian","Chihuahua",
                "Border Collie","Mixed Breed","Unknown"};
        actvBreed.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, breeds));

        String[] bloodTypes = {"DEA 1.1+","DEA 1.1-","DEA 1.2+","DEA 1.2-",
                "DEA 3+","DEA 3-","DEA 4+","DEA 5+","Unknown"};
        actvBloodType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodTypes));

        String[] genders = {"Male","Female"};
        actvGender.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders));
    }

    private void populateFields(Dog dog) {
        etName.setText(dog.getName());
        actvBreed.setText(dog.getBreed(), false);
        actvBloodType.setText(dog.getBloodType(), false);
        etHeight.setText(dog.getHeight() > 0 ? String.valueOf(dog.getHeight()) : "");
        etWeight.setText(dog.getWeight() > 0 ? String.valueOf(dog.getWeight()) : "");
        etAge.setText(dog.getAge() > 0 ? String.valueOf(dog.getAge()) : "");
        etColor.setText(dog.getColor());
        actvGender.setText(dog.getGender(), false);
        etDescription.setText(dog.getDescription());
        switchAdopted.setChecked(dog.isAdopted());
        selectedImagePath = dog.getPhotoPath() != null ? dog.getPhotoPath() : "";
        if (!selectedImagePath.isEmpty()) {
            Glide.with(this).load(Uri.parse(selectedImagePath)).centerCrop()
                    .placeholder(R.drawable.ic_dog_placeholder)
                    .error(R.drawable.ic_dog_placeholder).into(ivDogPhoto);
        }
    }

    private void saveDog() {
        String name   = etName.getText().toString().trim();
        String breed  = actvBreed.getText().toString().trim();
        String bType  = actvBloodType.getText().toString().trim();
        String hStr   = etHeight.getText().toString().trim();
        String wStr   = etWeight.getText().toString().trim();
        String aStr   = etAge.getText().toString().trim();
        String color  = etColor.getText().toString().trim();
        String gender = actvGender.getText().toString().trim();
        String desc   = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Dog name is required"); return; }

        Dog dog = editingDog != null ? editingDog : new Dog();
        dog.setName(name);
        dog.setBreed(breed.isEmpty()  ? "Unknown" : breed);
        dog.setBloodType(bType.isEmpty() ? "Unknown" : bType);
        dog.setHeight(hStr.isEmpty() ? 0 : Float.parseFloat(hStr));
        dog.setWeight(wStr.isEmpty() ? 0 : Float.parseFloat(wStr));
        dog.setAge(aStr.isEmpty()    ? 0 : Integer.parseInt(aStr));
        dog.setColor(color.isEmpty() ? "Unknown" : color);
        dog.setGender(gender.isEmpty()? "Unknown" : gender);
        dog.setDescription(desc);
        dog.setPhotoPath(selectedImagePath);
        dog.setAdopted(editingDog != null && switchAdopted.isChecked());

        if (editingDog == null) {
            dog.setDateAdded(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            if (dbHelper.addDog(dog) > 0) {
                Toast.makeText(this, "Dog added!", Toast.LENGTH_SHORT).show(); finish();
            } else Toast.makeText(this, "Failed to add dog.", Toast.LENGTH_SHORT).show();
        } else {
            if (dbHelper.updateDog(dog) > 0) {
                Toast.makeText(this, "Dog updated!", Toast.LENGTH_SHORT).show(); finish();
            } else Toast.makeText(this, "Failed to update.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
