package com.dogadoption.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dogadoption.app.R;
import com.dogadoption.app.adapters.AdminUserAdapter;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.models.User;

import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private AdminUserAdapter adapter;
    private RecyclerView rvUsers;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Users");
        }

        dbHelper  = DatabaseHelper.getInstance(this);
        rvUsers   = findViewById(R.id.rv_users);
        emptyView = findViewById(R.id.empty_view);
        TextView tvUserCount = findViewById(R.id.tv_user_count);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this, user -> confirmDeleteUser(user));
        rvUsers.setAdapter(adapter);

        loadUsers(tvUserCount);
    }

    private void loadUsers(TextView tvCount) {
        List<User> users = dbHelper.getAllUsers();
        adapter.setUsers(users);
        tvCount.setText("Total registered users: " + users.size());
        emptyView.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(users.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void confirmDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Delete " + user.getName() + "? Their adoption records will also be removed.")
                .setPositiveButton("Delete", (d, w) -> {
                    dbHelper.deleteUser(user.getId());
                    TextView tvCount = findViewById(R.id.tv_user_count);
                    loadUsers(tvCount);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
