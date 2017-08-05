package fi.mabrosim.shoppinglist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import fi.mabrosim.shoppinglist.R;
import fi.mabrosim.shoppinglist.tasks.DeleteAllTask;
import fi.mabrosim.shoppinglist.tasks.ShareCsvFileTask;
import fi.mabrosim.shoppinglist.tasks.ShareJsonFileTask;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.settings_show_export_bpb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PreviewExportedPbActivity.class));
            }
        });

        findViewById(R.id.settings_show_export_bpb_csv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PreviewExportedCsvActivity.class));
            }
        });

        findViewById(R.id.settings_show_export_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PreviewExportedJsonActivity.class));
            }
        });

        findViewById(R.id.settings_export_to_csv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareCsvFileTask(SettingsActivity.this).execute();
            }
        });

        findViewById(R.id.settings_export_to_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareJsonFileTask(SettingsActivity.this).execute();
            }
        });
    }

    public void deleteAllFromDb(View view) {
        new DeleteAllTask(SettingsActivity.this).execute();
        Toast.makeText(SettingsActivity.this, "Database cleared", Toast.LENGTH_SHORT).show();
        finish();
    }
}
