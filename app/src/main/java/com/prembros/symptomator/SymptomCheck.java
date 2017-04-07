package com.prembros.symptomator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class SymptomCheck extends AppCompatActivity implements CompleteSymptomList.OnFragmentInteractionListener {

    private boolean hidden = true;
    SupportAnimator animator;
    int itemCount;
    private MyRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_check);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        }

        final AppCompatImageButton searchButton = (AppCompatImageButton) this.findViewById(R.id.search_button);
        final AutoCompleteTextView searchBar = (AutoCompleteTextView) this.findViewById(R.id.first_aid_search_bar);
        final FrameLayout mRevealView = (FrameLayout) this.findViewById(R.id.reveal_view);
        final AppCompatImageButton closeButton = (AppCompatImageButton) this.findViewById(R.id.reveal_view_close_button);
        final TextView header = (TextView) this.findViewById(R.id.symptom_check_activity_header);

        Intent intent = getIntent();
        String selectedAge = intent.getExtras().getString("selectedAge");
        String selectedSex = intent.getExtras().getString("selectedSex");
//        String selectedBodyArea = intent.getExtras().getString("selectedBodyArea");
        String selectedBodyPart = intent.getExtras().getString("selectedBodyPart");
        String headerText = "Symptoms for " + selectedSex + ", " + selectedAge + ",\nwith concerns in " + selectedBodyPart;
        header.setText(headerText);

        final RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.first_aid_list);
        final List<String> symptomList = new SymptomDirectory().showSymptom(selectedBodyPart, selectedSex);
        symptomList.add("Didn't find what you were looking for?\nLook in the whole Symptom directory");
        recyclerViewAdapter = new MyRecyclerViewAdapter(this, symptomList, null, null);
        itemCount = recyclerViewAdapter.getItemCount();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationForward(v, mRevealView, searchButton, header, searchBar);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationReversed(mRevealView, searchButton, header, searchBar);
            }
        });

        //Set the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charSequence = charSequence.toString().toLowerCase();
                final List<String> filteredList = new ArrayList<>();

                for (int x = 0; x < symptomList.size(); x++){
                    final String text = symptomList.get(x).toLowerCase();
                    if (text.contains(charSequence))
                        filteredList.add(symptomList.get(x));
                }
                if (!charSequence.equals(""))
                    filteredList.add("Didn't find what you were looking for?\nLook in the whole Symptom directory");

                recyclerView.setLayoutManager(new LinearLayoutManager(SymptomCheck.this));
                recyclerViewAdapter = new MyRecyclerViewAdapter(SymptomCheck.this, filteredList, null, null);
                itemCount = recyclerViewAdapter.getItemCount();
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this, new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(SymptomCheck.this, "Clicked: " + position + " : " + itemCount, Toast.LENGTH_SHORT).show();
                if (position == itemCount - 1){
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                            .add(R.id.symptom_check_fragment_container, new CompleteSymptomList(), "completeSymptomList")
                            .commit();
                }
            }
        }));
    }

    public void animationForward(View v, FrameLayout mRevealView, AppCompatImageButton searchButton,
                                 TextView header, AutoCompleteTextView searchBar){
//        float pixelDensity = getResources().getDisplayMetrics().density;
        int centerX = (v.getLeft() + v.getRight()) / 2;
        int centerY = (v.getTop() + v.getBottom()) / 2;
        int startRadius = 0;
        int endRadius = (int) Math.hypot(mRevealView.getWidth(), mRevealView.getHeight());
        animator = ViewAnimationUtils.createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        if (hidden){
            animator.start();
            mRevealView.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.INVISIBLE);
            header.setVisibility(View.INVISIBLE);
            if(searchBar.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
            }
            hidden = false;
        }
    }

    public void animationReversed(final FrameLayout mRevealView, final AppCompatImageButton searchButton,
                                  final TextView header, final AutoCompleteTextView searchBar){
        if (animator != null && !animator.isRunning()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            animator = animator.reverse();
            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    searchBar.setText("");
                    searchBar.clearFocus();
                    mRevealView.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    hidden = true;
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            animator.start();
        }
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

    @Override
    public void onFragmentInteraction(String item) {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("completeSymptomList") != null){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                    .remove(getSupportFragmentManager().findFragmentByTag("completeSymptomList"))
                    .commit();
        } else super.onBackPressed();
    }
}
