package com.prembros.symptomator;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class SymptomCheck extends AppCompatActivity implements CompleteSymptomList.OnFragmentInteractionListener {

//    private boolean hidden = true;
//    SupportAnimator animator;
    int itemCount;
    private ActionBar actionBar;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    private List<String> symptomList;
    private RecyclerView recyclerView;
    private FrameLayout layoutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_check);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left);
            actionBar.setTitle(R.string.symptom);
        }

        DatabaseHolder dbHandler = new DatabaseHolder(this);

        Intent intent = getIntent();
        String selectedAge = intent.getExtras().getString("selectedAge");
        String selectedSex = intent.getExtras().getString("selectedSex");
//        String selectedBodyArea = intent.getExtras().getString("selectedBodyArea");
        String selectedBodyPart = intent.getExtras().getString("selectedBodyPart");
        String headerText = selectedSex + ", " + selectedAge + ", in " + selectedBodyPart;
        actionBar.setSubtitle(headerText);

        symptomList = new ArrayList<>();
        recyclerView = (RecyclerView) this.findViewById(R.id.first_aid_list);
        layoutContainer = (FrameLayout) this.findViewById(R.id.symptom_check_layout);

        dbHandler.open();
        Cursor cursor = dbHandler.returnSymptoms(selectedSex, selectedBodyPart);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            try {
                symptomList.add(cursor.getString(cursor.getColumnIndex("Symptom")));
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                cursor.moveToNext();
            }
        }
        dbHandler.close();

        symptomList.add("Didn't find what you were looking for?\nLook in the whole Symptom directory");
        recyclerViewAdapter = new MyRecyclerViewAdapter(this, symptomList, null, null);
        itemCount = recyclerViewAdapter.getItemCount();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this, new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(SymptomCheck.this, "Clicked: " + position + " : " + itemCount, Toast.LENGTH_SHORT).show();
                if (position == itemCount - 1){
                    actionBar.hide();
                    layoutContainer.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                            .add(R.id.symptom_check_fragment_container, new CompleteSymptomList(), "completeSymptomList")
                            .commit();
                }
            }
        }));
    }

//    public void animationForward(View v, FrameLayout mRevealView, AppCompatImageButton searchButton,
//                                 TextView header, AutoCompleteTextView searchBar){
////        float pixelDensity = getResources().getDisplayMetrics().density;
//        int centerX = (v.getLeft() + v.getRight()) / 2;
//        int centerY = (v.getTop() + v.getBottom()) / 2;
//        int startRadius = 0;
//        int endRadius = (int) Math.hypot(mRevealView.getWidth(), mRevealView.getHeight());
//        animator = ViewAnimationUtils.createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.setDuration(400);
//        if (hidden){
//            animator.start();
//            mRevealView.setVisibility(View.VISIBLE);
//            searchButton.setVisibility(View.INVISIBLE);
//            header.setVisibility(View.INVISIBLE);
//            if(searchBar.requestFocus()) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
//            }
//            hidden = false;
//        }
//    }
//
//    public void animationReversed(final FrameLayout mRevealView, final AppCompatImageButton searchButton,
//                                  final TextView header, final AutoCompleteTextView searchBar){
//        if (animator != null && !animator.isRunning()){
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
//            animator = animator.reverse();
//            animator.addListener(new SupportAnimator.AnimatorListener() {
//                @Override
//                public void onAnimationStart() {
//
//                }
//
//                @Override
//                public void onAnimationEnd() {
//                    searchBar.setText("");
//                    searchBar.clearFocus();
//                    mRevealView.setVisibility(View.GONE);
//                    searchButton.setVisibility(View.VISIBLE);
//                    header.setVisibility(View.VISIBLE);
//                    hidden = true;
//                }
//
//                @Override
//                public void onAnimationCancel() {
//
//                }
//
//                @Override
//                public void onAnimationRepeat() {
//
//                }
//            });
//            animator.start();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_symptom_check, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setActionView(new SearchView(this));
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<String> filteredList = new ArrayList<>();
                filteredList.clear();

                for (int x = 0; x < symptomList.size(); x++){
                    final String text = symptomList.get(x).toLowerCase();
                    if (text.contains(newText))
                        filteredList.add(symptomList.get(x));
                }
                if (!filteredList.contains("Didn't find what you were looking for?\nLook in the whole Symptom directory")) {
                    filteredList.add("Didn't find what you were looking for?\nLook in the whole Symptom directory");
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(SymptomCheck.this));
                recyclerViewAdapter = new MyRecyclerViewAdapter(SymptomCheck.this, filteredList, null, null);
                itemCount = recyclerViewAdapter.getItemCount();
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
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
            actionBar.show();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                    .remove(getSupportFragmentManager().findFragmentByTag("completeSymptomList"))
                    .commit();
            layoutContainer.setVisibility(View.VISIBLE);
        } else super.onBackPressed();
    }
}
