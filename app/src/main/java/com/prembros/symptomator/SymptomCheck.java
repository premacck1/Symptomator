package com.prembros.symptomator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SymptomCheck extends AppCompatActivity {

    private String selectedAge;
    private String selectedSex;
    private String selectedBodyArea;
    private String selectedBodyPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_check);
        Intent intent = getIntent();
        selectedAge = intent.getExtras().getString("selectedAge");
        selectedSex = intent.getExtras().getString("selectedSex");
        selectedBodyArea = intent.getExtras().getString("selectedBodyArea");
        selectedBodyPart = intent.getExtras().getString("selectedBodyPart");
        String header = "Select symptom for " + selectedSex + ", age " + selectedAge + " with concerns in " + selectedBodyPart + " in " + selectedBodyArea;
        ((TextView) this.findViewById(R.id.symptom_check_activity_header)).setText(header);
    }
}
