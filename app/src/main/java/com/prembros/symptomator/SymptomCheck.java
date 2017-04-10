package com.prembros.symptomator;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;

import static io.codetail.animation.ViewAnimationUtils.createCircularReveal;

public class SymptomCheck extends AppCompatActivity implements CompleteSymptomList.OnFragmentInteractionListener {

//    private boolean hidden = true;
//    SupportAnimator animator;
    int itemCount;
    private ActionBar actionBar;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    private List<String> symptomList;
    private RecyclerView recyclerView;
    private FrameLayout layoutContainer;
    private FragmentManager fragmentManager;
    private FrameLayout revealView;
//    private boolean hidden = true;
    boolean somethingIsActive = false;
    private SupportAnimator animator;
    private int[] touchCoordinate = new int[2];

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchCoordinate[0] = (int) ev.getX();
        touchCoordinate[1] = (int) ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_check);

        revealView = (FrameLayout) this.findViewById(R.id.menu_fragment_container);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left);
            actionBar.setTitle(R.string.symptom);
        }

        final DatabaseHolder dbHandler = new DatabaseHolder(this);
        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        final String selectedSex = intent.getExtras().getString("selectedSex");
//        String selectedBodyArea = intent.getExtras().getString("selectedBodyArea");
        final String selectedBodyPart = intent.getExtras().getString("selectedBodyPart");
        String headerText = selectedSex + ", " + selectedBodyPart;
        actionBar.setSubtitle(headerText);

        symptomList = new ArrayList<>();
        recyclerView = (RecyclerView) this.findViewById(R.id.first_aid_list);
        layoutContainer = (FrameLayout) this.findViewById(R.id.symptom_check_layout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHandler.open();
                Cursor cursor = dbHandler.returnSymptoms(selectedSex, selectedBodyPart);
                if (cursor != null) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        try {
                            symptomList.add(cursor.getString(cursor.getColumnIndex("Symptom")));
                            cursor.moveToNext();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                symptomList.add("Didn't find what you were looking for?\nLook in the whole Symptom directory");
                if (cursor != null) {
                    cursor.close();
                }
                dbHandler.close();
            }
        }
        ).start();

        recyclerViewAdapter = new MyRecyclerViewAdapter(this, symptomList, null, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this, new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemCount = recyclerViewAdapter.getItemCount();
//                Toast.makeText(SymptomCheck.this, "Clicked: " + position + " : " + itemCount, Toast.LENGTH_SHORT).show();
                if (position == itemCount - 1){
                    actionBar.hide();
                    layoutContainer.setVisibility(View.INVISIBLE);
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                            .add(R.id.symptom_check_fragment_container, new CompleteSymptomList(), "completeSymptomList")
                            .commit();
                    somethingIsActive = true;
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
                onBackPressed();
                return true;
            case R.id.action_about:
                animationForward(revealView, touchCoordinate[0], touchCoordinate[1]);
                fragmentManager.beginTransaction().add(R.id.menu_fragment_container, new About(), "about").commit();
                somethingIsActive = true;
            default:
                return false;
        }
    }

    @Override
    public void onFragmentInteraction(String item) {

    }

    public void animationForward(View mRevealView, int centerX, int centerY){
        int startRadius = 0;
        int endRadius = (int) (Math.hypot(mRevealView.getWidth() * 2, mRevealView.getHeight() * 2));
        animator = createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);

        animator.start();
        mRevealView.setVisibility(View.VISIBLE);
    }

    public void animationReversed(final View mRevealView){
//        if (actionBar != null) {
//            actionBar.setTitle(previousTitles[0]);
//            actionBar.setSubtitle(previousTitles[1]);
//        }
        if (animator != null && !animator.isRunning()){

//            fab.setBackgroundColor(Color.rgb(0, 188, 212));
//            fab.setImageResource(R.drawable.ic_add_shopping_cart);

            animator = animator.reverse();
            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    mRevealView.setVisibility(View.INVISIBLE);
//                    hidden = true;
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

    void removeFragmentIfAttached(final String tag){
        if (fragmentManager.findFragmentByTag(tag) != null){
            switch (tag){
                case "completeSymptomList":
                    actionBar.show();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                            .remove(fragmentManager.findFragmentByTag(tag))
                            .commit();
                    layoutContainer.setVisibility(View.VISIBLE);
                    somethingIsActive = false;
                    break;
                case "about":
                    animationReversed(this.findViewById(R.id.menu_fragment_container));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragmentManager.beginTransaction()
                                    .remove(getSupportFragmentManager().findFragmentByTag(tag))
                                    .commit();
                        }
                    }, 200);
                    somethingIsActive = false;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (somethingIsActive) {
            removeFragmentIfAttached("completeSymptomList");
            removeFragmentIfAttached("about");
        }
        else
            super.onBackPressed();
    }
}
