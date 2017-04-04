package com.prembros.symptomator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SymptomCheck extends AppCompatActivity {

    private String selectedAge;
    private String selectedSex;
    private String selectedBodyArea;
    private String selectedBodyPart;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_check);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }
        Intent intent = getIntent();
        selectedAge = intent.getExtras().getString("selectedAge");
        selectedSex = intent.getExtras().getString("selectedSex");
        selectedBodyArea = intent.getExtras().getString("selectedBodyArea");
        selectedBodyPart = intent.getExtras().getString("selectedBodyPart");
        String header = "Select symptom for " + selectedSex + ", age " + selectedAge + " with concerns in " + selectedBodyPart + " in " + selectedBodyArea;
        ((TextView) this.findViewById(R.id.symptom_check_activity_header)).setText(header);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_symptom_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }
    }
}
