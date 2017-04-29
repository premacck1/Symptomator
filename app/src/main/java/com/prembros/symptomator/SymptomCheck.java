package com.prembros.symptomator;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;

import static io.codetail.animation.ViewAnimationUtils.createCircularReveal;

public class SymptomCheck extends AppCompatActivity implements CompleteSymptomList.OnFragmentInteractionListener {

    private ActionBar actionBar;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    private List<String> symptomList;
    private RecyclerView recyclerView;
    private FragmentManager fragmentManager;
    private FrameLayout revealView;
    boolean somethingIsActive = false;
    private SupportAnimator animator;
    private int[] touchCoordinate = new int[2];
    private DatabaseHolder db;
    private List<String> selectedSymptoms;
    private LinearLayout floatingActionMenu;
    private boolean clearFlag = false;

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
        db = new DatabaseHolder(this);
        selectedSymptoms = new ArrayList<>();

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left);
            actionBar.setTitle(R.string.symptom);
        }

        floatingActionMenu = (LinearLayout) this.findViewById(R.id.fab_menu);
        FloatingActionButton fabShowSelectedSymptoms = (FloatingActionButton) this.findViewById(R.id.fab_1_show_selected_symptoms);
        FloatingActionButton fabClearSymptomSelection = (FloatingActionButton) this.findViewById(R.id.fab_2_delete_all_symptoms);


        /*FLOATING ACTION BUTTON ON CLICK LISTENERS*/
        fabShowSelectedSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShowSelectedSymptoms().execute("showSelectedSymptoms");
            }
        });
        fabClearSymptomSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast;
                if (clearFlag) {
                    new ShowSelectedSymptoms().execute("delete");
                    toast = Toast.makeText(SymptomCheck.this, "Selection cleared!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    uncheckAllViews(recyclerView, recyclerViewAdapter);
                    return;
                }
                clearFlag = true;
                toast = Toast.makeText(SymptomCheck.this, "Click again to confirm.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearFlag = false;
                    }
                }, 2000);

//                recyclerView.smoothScrollToPosition(0);
//                recyclerView.removeAllViews();
            }
        });

        /*HELPER TOASTS FOR FLOATING ACTION BUTTONS*/
        fabShowSelectedSymptoms.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                makeToast(view, getString(R.string.show_selected_symptoms));
                return true;
            }
        });
        fabClearSymptomSelection.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                makeToast(view, getString(R.string.clear_all_selected_symptoms));
                return true;
            }
        });

        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        final String selectedSex = intent.getExtras().getString("selectedSex");
//        String selectedBodyArea = intent.getExtras().getString("selectedBodyArea");
        final String selectedBodyPart = intent.getExtras().getString("selectedBodyPart");
        String headerText = selectedSex + ", " + selectedBodyPart;
        actionBar.setSubtitle(headerText);

        symptomList = new ArrayList<>();
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerview_list);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.open();
                Cursor cursor = db.returnSymptoms(selectedSex, selectedBodyPart);
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
                symptomList.add("Could not find what you were looking for?\nLook in the whole Symptom directory");
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        }
        ).start();

        recyclerViewAdapter = new MyRecyclerViewAdapter(true, this, symptomList, null, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

//        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this, this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10) {
                    hideOrShow(floatingActionMenu, false);
                }
                else if (dy < -10) {
                    hideOrShow(floatingActionMenu, true);
                }
            }
        });
    }

    public void makeToast(View view, String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.LEFT, view.getLeft(), view.getTop() + view.getHeight() * 3);
        toast.show();
    }

    public void uncheckAllViews(final RecyclerView recyclerView, final MyRecyclerViewAdapter adapter) {
        final CheckedTextView[] checkedTextView = new CheckedTextView[1];
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
//            MyRecyclerViewAdapter.mCheckedItems.put(i, false);
            checkedTextView[0] = (CheckedTextView) ((MaterialRippleLayout)((RelativeLayout) recyclerView.getChildAt(0)).getChildAt(0)).getChildAt(0);
            checkedTextView[0].setChecked(false);
        }
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
    }

    public void onRecyclerViewItemClick(CheckedTextView checkedTextView, int position, int lastPosition) {
        if (position == lastPosition) {
            checkedTextView.setChecked(false);
            actionBar.hide();
            recyclerView.setVisibility(View.INVISIBLE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                    .add(R.id.symptom_check_fragment_container, new CompleteSymptomList(), "completeSymptomList")
                    .commit();
            somethingIsActive = true;
        } else {
            final String viewText = checkedTextView.getText().toString();

//                        checkedArray[pos] = !checkedArray[pos];
//                        notifyItemChanged(pos);

            if (checkedTextView.isChecked()) {
                checkedTextView.setChecked(false);
                checkedTextView.setTextColor(Color.parseColor("#000000"));
                checkedTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_deselected));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.open();
                        db.removeFromSelectedSymptomsTable(viewText);
                        db.close();
                    }
                }).start();
            } else {
                checkedTextView.setChecked(true);
                checkedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                checkedTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_selected));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            db.open();
                            db.insertInSelectedSymptomsTable(viewText);
                            db.insertInSelectedSymptomsTable(viewText);
                            db.close();
                            new SymptomCheck.ShowSelectedSymptoms().execute("addSelectedSymptoms");
                        } catch (SQLException | IllegalStateException e) {
                            Log.d("SQLException ERROR!", e.getMessage());
                        }
                    }
                }).start();
            }
//                    Toast.makeText(context, value + position, Toast.LENGTH_SHORT).show();
        }
    }

    void hideOrShow(LinearLayout floatingActionMenu, boolean shown) {
        if (floatingActionMenu.getVisibility() == View.INVISIBLE && shown) {
            floatingActionMenu.startAnimation(AnimationUtils.loadAnimation(SymptomCheck.this, R.anim.float_up));
            floatingActionMenu.setVisibility(View.VISIBLE);
        } else if (floatingActionMenu.getVisibility() == View.VISIBLE && !shown) {
            floatingActionMenu.startAnimation(AnimationUtils.loadAnimation(SymptomCheck.this, R.anim.sink_down));
            floatingActionMenu.setVisibility(View.INVISIBLE);
        }
    }

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
                if (!filteredList.contains("Could not find")) {
                    filteredList.add("Could not find what you were looking for?\nLook in the whole Symptom directory");
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(SymptomCheck.this));
                recyclerViewAdapter = new MyRecyclerViewAdapter(true, SymptomCheck.this, filteredList, null, null);
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
                    recyclerView.setVisibility(View.VISIBLE);
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

    /*ASYNCTASK FOR SHOWING OR DELETING SELECTED SYMPTOMS*/
    private class ShowSelectedSymptoms extends AsyncTask<String, Void, Void> {

        private boolean showSelectedSymptoms = false;
        StringBuilder builder;

        @Override
        protected Void doInBackground(String... strings) {
            switch (strings[0]) {
                case "showSelectedSymptoms":                                                        /*SHOW SELECTED SYMPTOMS*/
                    showSelectedSymptoms = true;
                    prepareSelectedSymptoms(true);
                    break;
                case "addSelectedSymptoms":                                                         /*ADD SELECTED SYMPTOMS*/
                    showSelectedSymptoms = false;
                    prepareSelectedSymptoms(false);
                    break;
                default:                                                                            /*CLEAR SYMPTOM SELECTION*/
                    showSelectedSymptoms = false;
                    db.open();
                    db.resetSelectedSymptomsTable();
                    db.close();
                    break;
            }
            return null;
        }

        void prepareSelectedSymptoms(boolean show) {
            selectedSymptoms.clear();
            db.open();
            Cursor cursor = db.returnSelectedSymptoms();
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    try {
                        selectedSymptoms.add(cursor.getString(cursor.getColumnIndex("Symptom")));
                        cursor.moveToNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            db.close();

            if (show) {
                builder = new StringBuilder();
                for (String string : selectedSymptoms) {
                    builder.append(string).append("\n");
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (showSelectedSymptoms) {
                new AlertDialog.Builder(SymptomCheck.this)
                        .setTitle("Your selected symptoms")
                        .setMessage(builder.toString())
                        .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(aVoid);
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
