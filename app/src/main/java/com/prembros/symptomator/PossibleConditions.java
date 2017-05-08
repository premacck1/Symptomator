package com.prembros.symptomator;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class PossibleConditions extends AppCompatActivity {

    private ActionBar actionBar;
    private ArrayList<String> possibleConditions;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_possible_conditions);
        possibleConditions = new ArrayList<>();
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerview);

        actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(R.string.possible_conditions);
//        }
        possibleConditions = getIntent().getExtras().getStringArrayList("possibleConditions");
        recyclerView.setAdapter(new MyRecyclerViewAdapter(false, this, possibleConditions, null));
    }
}
