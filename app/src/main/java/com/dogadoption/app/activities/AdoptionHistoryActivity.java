package com.dogadoption.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dogadoption.app.R;
import com.dogadoption.app.adapters.AdoptionAdapter;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.database.SessionManager;
import com.dogadoption.app.models.AdoptionRecord;

import java.util.List;

public class AdoptionHistoryActivity extends AppCompatActivity {

    private RecyclerView rvAdoptions;
    private View emptyView;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Adoption History");
        }

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = SessionManager.getInstance(this);

        rvAdoptions = findViewById(R.id.rv_adoptions);
        emptyView = findViewById(R.id.empty_view);

        rvAdoptions.setLayoutManager(new LinearLayoutManager(this));

        loadAdoptions();
    }

    private void loadAdoptions() {
        List<AdoptionRecord> records = dbHelper.getAdoptionsByUser(sessionManager.getUserId());
        AdoptionAdapter adapter = new AdoptionAdapter(this, records);
        rvAdoptions.setAdapter(adapter);
        emptyView.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
        rvAdoptions.setVisibility(records.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
