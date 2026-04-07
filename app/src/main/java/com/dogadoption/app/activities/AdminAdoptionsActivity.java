package com.dogadoption.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dogadoption.app.R;
import com.dogadoption.app.adapters.AdminAdoptionAdapter;
import com.dogadoption.app.database.DatabaseHelper;
import com.dogadoption.app.models.AdoptionRecord;

import java.util.List;

public class AdminAdoptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_adoptions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Adoptions");
        }

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        RecyclerView rvAdoptions = findViewById(R.id.rv_adoptions);
        View emptyView           = findViewById(R.id.empty_view);
        TextView tvCount         = findViewById(R.id.tv_adoption_count);

        List<AdoptionRecord> records = dbHelper.getAllAdoptionRecords();

        rvAdoptions.setLayoutManager(new LinearLayoutManager(this));
        rvAdoptions.setAdapter(new AdminAdoptionAdapter(this, records));

        tvCount.setText("Total adoptions: " + records.size());
        emptyView.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
        rvAdoptions.setVisibility(records.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
