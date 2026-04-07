package com.dogadoption.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.dogadoption.app.R;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.database.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Admin Dashboard");

        dbHelper = DatabaseHelper.getInstance(this);
        session  = SessionManager.getInstance(this);

        // Stat cards
        TextView tvTotalDogs   = findViewById(R.id.tv_total_dogs);
        TextView tvAvailable   = findViewById(R.id.tv_available);
        TextView tvAdopted     = findViewById(R.id.tv_adopted);
        TextView tvTotalUsers  = findViewById(R.id.tv_total_users);

        // Nav cards
        CardView cardManageDogs    = findViewById(R.id.card_manage_dogs);
        CardView cardManageUsers   = findViewById(R.id.card_manage_users);
        CardView cardAllAdoptions  = findViewById(R.id.card_all_adoptions);
        CardView cardAdminProfile  = findViewById(R.id.card_admin_profile);

        cardManageDogs.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
        cardManageUsers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUsersActivity.class)));
        cardAllAdoptions.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAdoptionsActivity.class)));
        cardAdminProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        updateStats(tvTotalDogs, tvAvailable, tvAdopted, tvTotalUsers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvTotalDogs  = findViewById(R.id.tv_total_dogs);
        TextView tvAvailable  = findViewById(R.id.tv_available);
        TextView tvAdopted    = findViewById(R.id.tv_adopted);
        TextView tvTotalUsers = findViewById(R.id.tv_total_users);
        updateStats(tvTotalDogs, tvAvailable, tvAdopted, tvTotalUsers);
    }

    private void updateStats(TextView dogs, TextView avail, TextView adopted, TextView users) {
        dogs.setText(String.valueOf(dbHelper.getTotalDogs()));
        avail.setText(String.valueOf(dbHelper.getAvailableDogsCount()));
        adopted.setText(String.valueOf(dbHelper.getAdoptedDogsCount()));
        users.setText(String.valueOf(dbHelper.getTotalUsers()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (d, w) -> {
                        session.logout();
                        startActivity(new Intent(this, LoginActivity.class));
                        finishAffinity();
                    })
                    .setNegativeButton("Cancel", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
