package com.dogadoption.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dogadoption.app.R;
import com.dogadoption.app.adapters.DogAdapter;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.database.SessionManager;
import com.dogadoption.app.models.Dog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDogs;
    private DogAdapter dogAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private EditText etSearch;
    private TextView tvTotalDogs, tvAvailable, tvAdopted;
    private RadioGroup rgFilter;
    private FloatingActionButton fabAdd;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DatabaseHelper.getInstance(this);
        session  = SessionManager.getInstance(this);

        etSearch    = findViewById(R.id.et_search);
        rvDogs      = findViewById(R.id.rv_dogs);
        tvTotalDogs = findViewById(R.id.tv_total_dogs);
        tvAvailable = findViewById(R.id.tv_available);
        tvAdopted   = findViewById(R.id.tv_adopted);
        rgFilter    = findViewById(R.id.rg_filter);
        fabAdd      = findViewById(R.id.fab_add);
        emptyView   = findViewById(R.id.empty_view);

        rvDogs.setLayoutManager(new GridLayoutManager(this, 2));

        // Pass isAdmin so the adapter can show/hide edit controls per card
        boolean isAdmin = session.isAdmin();

        dogAdapter = new DogAdapter(this, dog -> {
            Intent intent = new Intent(this, DogDetailActivity.class);
            intent.putExtra("dog_id", dog.getId());
            intent.putExtra("is_admin", isAdmin);
            startActivity(intent);
        });
        rvDogs.setAdapter(dogAdapter);

        // Only admins can add dogs
        if (isAdmin) {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v ->
                    startActivity(new Intent(this, AddEditDogActivity.class)));
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { filterDogs(); }
        });

        rgFilter.setOnCheckedChangeListener((g, id) -> filterDogs());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
        filterDogs();
    }

    private void updateStats() {
        tvTotalDogs.setText(String.valueOf(dbHelper.getTotalDogs()));
        tvAvailable.setText(String.valueOf(dbHelper.getAvailableDogsCount()));
        tvAdopted.setText(String.valueOf(dbHelper.getAdoptedDogsCount()));
    }

    private void filterDogs() {
        String query = etSearch.getText().toString().trim();
        List<Dog> dogs;
        int checkedId = rgFilter.getCheckedRadioButtonId();
        if (!query.isEmpty()) {
            dogs = dbHelper.searchDogs(query);
        } else if (checkedId == R.id.rb_available) {
            dogs = dbHelper.getAvailableDogs();
        } else {
            dogs = dbHelper.getAllDogs();
        }
        dogAdapter.setDogs(dogs);
        emptyView.setVisibility(dogs.isEmpty() ? View.VISIBLE : View.GONE);
        rvDogs.setVisibility(dogs.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_history) {
            startActivity(new Intent(this, AdoptionHistoryActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (d, w) -> {
                    session.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
