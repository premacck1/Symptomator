package com.prembros.symptomator;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;

import static io.codetail.animation.ViewAnimationUtils.createCircularReveal;

public class SymptomCheck extends AppCompatActivity implements CompleteConditionList.OnCompleteConditionsInteractionListener,
        ShowSelectedSymptoms.OnFragmentInteractionListener, PossibleConditions.OnPossibleConditionsFragmentInteractionsListener {

    private ActionBar actionBar;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<String> symptomList;
    private RecyclerView recyclerView;
    private String selectedBodyPart;
    private FragmentManager fragmentManager;
    private FrameLayout revealView;
    private SupportAnimator animator;
    private FloatingActionButton fabShowSelectedSymptoms;
    private FloatingActionButton fabClearSymptomSelection;
    private final int[] touchCoordinate = new int[2];
    private DatabaseHolder db;
    private ArrayList<String> selectedSymptoms;
    private boolean clearFlag = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchCoordinate[0] = (int) ev.getX();
        touchCoordinate[1] = (int) ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_check);

        revealView = (FrameLayout) this.findViewById(R.id.menu_fragment_container);

        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerview);
        db = new DatabaseHolder(this);
        selectedSymptoms = new ArrayList<>();

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setTitle(R.string.symptom);
        }

        Intent intent = getIntent();
        final String selectedSex = intent.getExtras().getString("selectedSex");
//        String selectedBodyArea = intent.getExtras().getString("selectedBodyArea");
        selectedBodyPart = intent.getExtras().getString("selectedBodyPart");
        String headerText = selectedSex + ", " + selectedBodyPart;
        actionBar.setSubtitle(headerText);

        symptomList = new ArrayList<>();

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
                symptomList.add("Could not find what you were looking for?\nClick here to browse all the conditions");
//                symptomList.add(" ");
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        }
        ).start();

        recyclerViewAdapter = new RecyclerViewAdapter(true, this, symptomList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        fabShowSelectedSymptoms = (FloatingActionButton) this.findViewById(R.id.fab_1_show_selected_symptoms);
        fabClearSymptomSelection = (FloatingActionButton) this.findViewById(R.id.fab_2_delete_all_symptoms);

//        fabShowSelectedSymptoms.setOnTouchListener(this);
//        fabClearSymptomSelection.setOnTouchListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    hideOrShow(fabClearSymptomSelection, fabShowSelectedSymptoms, false);
                }
                else if (dy < -5) {
                    hideOrShow(fabClearSymptomSelection, fabShowSelectedSymptoms, true);
                }
            }
        });

        fragmentManager = getSupportFragmentManager();

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
                if (clearFlag) {
                    new ShowSelectedSymptoms().execute("delete");
                    uncheckAllViews(recyclerView, symptomList);
                    return;
                }
                clearFlag = true;
                makeToast(view, "Click again to clear selected symptoms.");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearFlag = false;
                    }
                }, 2000);
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
    }

    private void makeToast(View view, String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.LEFT, view.getLeft(), view.getTop() + view.getHeight() * 3);
        toast.show();
    }

    private void uncheckAllViews(final RecyclerView recyclerView, final List<String> items) {
        final View revealView = this.findViewById(R.id.fab_revealView);
        revealView.setBackgroundResource(R.color.colorSecondary);
        animationForward(revealView, touchCoordinate[0], touchCoordinate[1]);
//        final CheckedTextView[] checkedTextView = new CheckedTextView[1];
//        for (int i = 0; i < recyclerView.getChildCount(); i++) {
////            RecyclerViewAdapter.mCheckedItems.put(i, false);
//            checkedTextView[0] = (CheckedTextView) ((MaterialRippleLayout)((RelativeLayout) recyclerView.getChildAt(0)).getChildAt(0)).getChildAt(0);
//            checkedTextView[0].setChecked(false);
//        }
//        adapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(new RecyclerViewAdapter(true, SymptomCheck.this, items));
            }
        }, 400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView.startAnimation(AnimationUtils.loadAnimation(SymptomCheck.this, android.R.anim.fade_out));
                revealView.setVisibility(View.INVISIBLE);
            }
        }, 500);
//        recyclerView.smoothScrollToPosition(0);
    }

    public void onRecyclerViewItemClick(CheckedTextView checkedTextView, int position, int lastPosition) {
        if (position == lastPosition) {
            checkedTextView.setChecked(false);
            actionBar.hide();
            recyclerView.setVisibility(View.INVISIBLE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                    .add(R.id.symptom_check_fragment_container, new CompleteConditionList(), "completeSymptomList")
                    .commit();
        } else {
            final String viewText = checkedTextView.getText().toString();

            if (fabClearSymptomSelection.getVisibility() == View.INVISIBLE)
                hideOrShow(fabClearSymptomSelection, fabShowSelectedSymptoms, true);

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
                            new ShowSelectedSymptoms().execute("addSelectedSymptoms");
                        } catch (IllegalStateException e) {
                            Log.d("SQLException ERROR!", e.getMessage());
                        }
                    }
                }).start();
            }
//                    Toast.makeText(context, value + position, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideOrShow(final FloatingActionButton fab1, final FloatingActionButton fab2, boolean shown) {
        if (fab2.getVisibility() == View.INVISIBLE && shown) {
            fab2.startAnimation(AnimationUtils.loadAnimation(SymptomCheck.this, R.anim.float_up));
            fab2.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab1.startAnimation(AnimationUtils.loadAnimation(SymptomCheck.this, R.anim.float_up));
                    fab1.setVisibility(View.VISIBLE);
                }
            }, 100);
        } else if (fab1.getVisibility() == View.VISIBLE && !shown) {
            fab2.startAnimation(AnimationUtils.loadAnimation(SymptomCheck.this, R.anim.sink_down));
            fab2.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab1.startAnimation(AnimationUtils.loadAnimation(SymptomCheck.this, R.anim.sink_down));
                    fab1.setVisibility(View.INVISIBLE);
                }
            }, 100);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
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
                recyclerViewAdapter = new RecyclerViewAdapter(true, SymptomCheck.this, filteredList);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().add(R.id.menu_fragment_container, new About(), "about").commit();
                    }
                }, 600);
                return true;
            case R.id.action_text_size:
                return false;
            default:
                return false;
        }
    }

    private void animationForward(View mRevealView, int centerX, int centerY){
        int startRadius = 0;
        int endRadius = (int) (Math.hypot(mRevealView.getWidth() * 2, mRevealView.getHeight() * 2));
        animator = createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(600);
        animator.start();
        mRevealView.setVisibility(View.VISIBLE);
    }

    private void animationReversed(final View mRevealView, int[] centerCoords){
        int startRadius = 0;
        int endRadius = (int) (Math.hypot(mRevealView.getWidth() * 2, mRevealView.getHeight() * 2));
        animator = createCircularReveal(mRevealView, centerCoords[0], centerCoords[1], startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(800);
        if (!animator.isRunning()){
            animator = animator.reverse();
            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    mRevealView.setVisibility(View.INVISIBLE);
                    mRevealView.setBackgroundResource(android.R.color.transparent);
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

    @Override
    public void onShowSelectedSymptomsFragmentInteraction(String item, @Nullable ArrayList<String> selectedSymptoms) {
        switch (item) {
            case "show":
                if (selectedSymptoms != null) {
                    Bundle args = new Bundle();
                    args.putString("bodyPart", selectedBodyPart);
                    args.putStringArrayList("selectedSymptoms", selectedSymptoms);

                    PossibleConditions possibleConditions = new PossibleConditions();
                    possibleConditions.setArguments(args);

                    animationForward(this.findViewById(R.id.fab_show_conditions_revealView), touchCoordinate[0], touchCoordinate[1]);
                    fragmentManager.beginTransaction()
//                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .add(R.id.fab_show_conditions_revealView, possibleConditions, "possibleConditions")
                            .commit();

                }
                break;
            case "close":
                removeFragmentIfAttached("selectedSymptoms");
                break;
            default:
                break;
        }
    }

    @Override
    public void OnPossibleConditionsInteractionListener(final String item) {
        final View revealView = this.findViewById(R.id.fab_condition_details_revealView);
        revealView.setBackgroundResource(R.color.colorSecondary);
        animationForward(revealView, touchCoordinate[0], touchCoordinate[1]);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView.setBackgroundResource(R.color.colorDisabledLight);
                fragmentManager.beginTransaction()
                        .add(
                                R.id.fab_condition_details_revealView,
                                PossibleConditionDetails.newInstance(item),
                                "conditionDetails"
                        )
                        .commit();
            }
        }, 500);
    }

    @Override
    public void onCompleteConditionsInteraction(String item) {
        OnPossibleConditionsInteractionListener(item);
    }

    /*ASYNCTASK FOR SHOWING OR DELETING SELECTED SYMPTOMS*/
    private class ShowSelectedSymptoms extends AsyncTask<String, Void, Void> {

        private boolean showSelectedSymptoms = false;
//        StringBuilder builder;

        @Override
        protected Void doInBackground(String... strings) {
            switch (strings[0]) {
                case "showSelectedSymptoms":                                                        /*SHOW SELECTED SYMPTOMS*/
                    showSelectedSymptoms = true;
                    prepareSelectedSymptoms();
                    break;
                case "addSelectedSymptoms":                                                         /*ADD SELECTED SYMPTOMS*/
                    showSelectedSymptoms = false;
                    prepareSelectedSymptoms();
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

        void prepareSelectedSymptoms() {
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

//            if (show) {
//                builder = new StringBuilder();
//                for (String string : selectedSymptoms) {
//                    builder.append(string).append("\n");
//                }
//            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (showSelectedSymptoms) {
                animationForward(SymptomCheck.this.findViewById(R.id.fab_show_revealView), touchCoordinate[0], touchCoordinate[1]);
                com.prembros.symptomator.ShowSelectedSymptoms selectedSymptomsFragment = new com.prembros.symptomator.ShowSelectedSymptoms();
                Bundle args = new Bundle();
                args.putStringArrayList("selectedSymptoms", selectedSymptoms);
                selectedSymptomsFragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .add(R.id.fab_show_revealView, selectedSymptomsFragment, "selectedSymptoms")
                        .commit();
            }
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onDestroy() {
        new ShowSelectedSymptoms().execute("delete");
        super.onDestroy();
    }

    private void removeFragmentIfAttached(final String tag){
        switch (tag) {
            case "completeSymptomList":
                actionBar.show();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                        .remove(fragmentManager.findFragmentByTag(tag))
                        .commit();
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case "about":
                final View revealView = this.findViewById(R.id.menu_fragment_container);
                revealView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                removeFragment(tag);
                revealView.setVisibility(View.INVISIBLE);
                break;
            case "selectedSymptoms":
                animationReversed(this.findViewById(R.id.fab_show_revealView), touchCoordinate);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeFragment(tag);
                    }
                }, 600);
                break;
            case "possibleConditions":
                animationReversed(this.findViewById(R.id.fab_show_conditions_revealView), touchCoordinate);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeFragment(tag);
                    }
                }, 600);
                break;
            case "conditionDetails":
                animationReversed(this.findViewById(R.id.fab_condition_details_revealView), touchCoordinate);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeFragment(tag);
                    }
                }, 600);
                break;
            default:
                break;
        }
    }

    private void removeFragment(String tag) {
        if (fragmentManager.findFragmentByTag(tag) != null) {
            fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(tag))
                    .commit();
        }
    }

    private boolean isFragmentActive(String tag) {
        return fragmentManager.findFragmentByTag(tag) != null && fragmentManager.findFragmentByTag(tag).isAdded();
    }

    @Override
    public void onBackPressed() {
//        if (this.findViewById(R.id.seekbar) != null) {
//            SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekbar);
//            if (seekBar.getVisibility() == View.VISIBLE) {
//                seekBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.sink_up));
//            }
//        }
        if (isFragmentActive("conditionDetails")) removeFragmentIfAttached("conditionDetails");
        else if (isFragmentActive("completeSymptomList")) removeFragmentIfAttached("completeSymptomList");
        else if (isFragmentActive("about")) removeFragmentIfAttached("about");
        else if (isFragmentActive("possibleConditions")) removeFragmentIfAttached("possibleConditions");
        else if (isFragmentActive("selectedSymptoms")) removeFragmentIfAttached("selectedSymptoms");
        else super.onBackPressed();
    }
}
