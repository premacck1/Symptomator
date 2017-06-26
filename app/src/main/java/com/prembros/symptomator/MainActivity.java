package com.prembros.symptomator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import io.codetail.animation.SupportAnimator;

import static io.codetail.animation.ViewAnimationUtils.createCircularReveal;

public class MainActivity extends AppCompatActivity implements
        SymptomFragment.OnSymptomFragmentInteractionListener,
        FirstAidFragment.OnFirstAidListFragmentInteractionListener, CompleteConditionList.OnCompleteConditionsInteractionListener {

    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;
    private ActionBar actionBar;
    private final int[] touchCoordinate = new int[2];

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_by_symptom:
                    if (fragmentManager.findFragmentByTag("symptomFragment") == null) {
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.float_up, R.anim.sink_up)
                                .replace(R.id.main_fragment_container, new SymptomFragment(), "symptomFragment")
                                .commit();
                        navigation.setItemBackgroundResource(R.color.colorDividerLight);
                        navigation.setItemTextColor(ContextCompat.getColorStateList(navigation.getContext(), R.color.colorSecondaryText));
                        navigation.setItemIconTintList(ContextCompat.getColorStateList(navigation.getContext(), R.color.colorSecondaryText));
                    }
                    return true;
                case R.id.navigation_by_first_aid:
                    if (fragmentManager.findFragmentByTag("firstAidFragment") == null) {
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.float_up, R.anim.sink_up)
                                .replace(R.id.main_fragment_container, new FirstAidFragment(), "firstAidFragment")
                                .commit();
                        navigation.setItemBackgroundResource(R.color.colorDividerLight);
                        navigation.setItemTextColor(ContextCompat.getColorStateList(navigation.getContext(), R.color.colorSecondaryText));
                        navigation.setItemIconTintList(ContextCompat.getColorStateList(navigation.getContext(), R.color.colorSecondaryText));
                    }
                    return true;
                case R.id.navigation_by_services:
                    if (fragmentManager.findFragmentByTag("servicesFragment") == null) {
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.float_up, R.anim.sink_up)
                                .replace(R.id.main_fragment_container, new ServicesFragment(), "servicesFragment")
                                .commit();
                        navigation.setItemBackgroundResource(R.color.colorSecondaryDark);
                        navigation.setItemTextColor(ContextCompat.getColorStateList(navigation.getContext(), R.color.white_text_icons));
                        navigation.setItemIconTintList(ContextCompat.getColorStateList(navigation.getContext(), R.color.white_text_icons));
                    }
                    return true;
                default:
                    return false;
            }
        }
    };

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if( googleApiClient != null )
//            googleApiClient.connect();
//    }


    @Override
    protected void onResume() {
        if (navigation.getVisibility() == View.INVISIBLE) {
            navigation.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

//        revealView = (LinearLayout) this.findViewById(R.id.services_revealView);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.float_up, R.anim.sink_up)
                .add(R.id.main_fragment_container, new SymptomFragment(), "symptomFragment")
                .commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchCoordinate[0] = (int) ev.getX();
        touchCoordinate[1] = (int) ev.getY();
//        if (rect != null) {
//            int height = findViewById(R.id.call_108).getHeight();
//            height = height + (height / 2);
//            revealViewContainer.getHitRect(rect);
//            rect.bottom += height;
//            rect.top += height;
//            if (!hidden && !rect.contains((int) ev.getX(), (int) ev.getY())) {
//                animationReversed(revealView);
//                return true;
//            }
//        }
        return super.dispatchTouchEvent(ev);
    }

    private void animationForward(View mRevealView, int[] center){
        int centerX = center[0];
        int centerY = center[1];
        int startRadius = 0;
        int endRadius = (int) (Math.hypot(mRevealView.getWidth() * 2, mRevealView.getHeight() * 2));
        SupportAnimator animator = createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(800);

        animator.start();
        mRevealView.setVisibility(View.VISIBLE);
    }

//    private void animationReversed(@Nullable final View mRevealView){
//        if (animator != null && !animator.isRunning()){
//            animator = animator.reverse();
//            animator.addListener(new SupportAnimator.AnimatorListener() {
//                @Override
//                public void onAnimationStart() {
//
//                }
//
//                @Override
//                public void onAnimationEnd() {
//                    if (mRevealView != null) {
//                        mRevealView.setVisibility(View.INVISIBLE);
//                    }
////                    hidden = true;
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
//        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_all_conditions:
                navigation.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.sink_down));
                navigation.setVisibility(View.INVISIBLE);
                final View revealView1 = this.findViewById(R.id.menu_fragment_container);
                revealView1.setBackgroundResource(R.color.colorSecondary);
                animationForward(revealView1, touchCoordinate);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        revealView1.setBackgroundResource(R.color.colorDisabledLight);
                        if (isFragmentActive("about")) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.menu_fragment_container, new CompleteConditionList(), "completeConditionList")
                                    .commit();
                        } else fragmentManager.beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .add(R.id.menu_fragment_container, new CompleteConditionList(), "completeConditionList")
                                .commit();

//                        actionBar.hide();
                    }
                }, 500);
                return true;
            case R.id.action_about:
                final FrameLayout revealView = (FrameLayout) this.findViewById(R.id.menu_fragment_container);
                revealView.setBackgroundResource(R.color.colorSecondary);
                animationForward(revealView, touchCoordinate);
                revealView.setVisibility(View.VISIBLE);
                navigation.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFragmentActive("completeConditionList"))
                            fragmentManager.beginTransaction().replace(R.id.menu_fragment_container, new About(), "about").commit();
                        else fragmentManager.beginTransaction().add(R.id.menu_fragment_container, new About(), "about").commit();
                    }
                }, 600);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSymptomFragmentInteraction(String selectedSex, String selectedBodyArea, String selectedBodyPart) {
        Intent intent = new Intent(this, SymptomCheck.class);
        intent.putExtra("selectedSex", selectedSex);
        intent.putExtra("selectedBodyArea", selectedBodyArea);
        intent.putExtra("selectedBodyPart", selectedBodyPart);
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(boolean flag, final String item) {
        if (flag) {
//            FOR LAUNCHING THE FIRST AID DETAILS
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, FirstAidCheck.class).putExtra("topic", item));
                }
            }, 200);
        } else {
//            FOR SCROLLING OF RECYCLERVIEW
            if (item.equals("up")) {
                if (navigation.getVisibility() == View.VISIBLE) {
                    navigation.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.sink_down));
                    navigation.setVisibility(View.INVISIBLE);
                }
            }
            else if (item.equals("down")) {
                if (navigation.getVisibility() == View.INVISIBLE) {
                    navigation.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.float_up));
                    navigation.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onCompleteConditionsInteraction(final String item) {
        final View revealView = this.findViewById(R.id.fab_condition_details_revealView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView.setBackgroundResource(R.color.colorSecondary);
            }
        }, 400);
        animationForward(revealView, touchCoordinate);
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

//    @Override
//    protected void onStop() {
//        if( googleApiClient != null && googleApiClient.isConnected() ) {
//            mAdapter.setGoogleApiClient( null );
//            googleApiClient.disconnect();
//        }
//        super.onStop();

    /*LINKS FOR ABOUT PAGE*/
    public void goToPremSuman(@SuppressWarnings("UnusedParameters") View view) {
        goToURL("https://facebook.com/premsuman8");
    }

    public void goToPremAnkur(@SuppressWarnings("UnusedParameters") View view) {
        goToURL("https://facebook.com/prem.ankur.14");
    }

    public void goToVikash(@SuppressWarnings("UnusedParameters") View view) {
        goToURL("https://facebook.com/vikashruhelacse");
    }

    public void goToPrateek(@SuppressWarnings("UnusedParameters") View view) {
        goToURL("https://facebook.com/prateek.sen.118");
    }

    public void goToPremBros(@SuppressWarnings("UnusedParameters") View view) {
        goToURL("https://facebook.com/https://play.google.com/store/apps/developer?id=Prem+Bros");
    }

    private void goToURL(String URL) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
        } catch (android.content.ActivityNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, "No app found for this action!", Toast.LENGTH_SHORT).show();
        }
    }

//    private void removeFragmentIfAttached(String tag){
//        switch (tag) {
//            case "conditionDetails":
//                removeFragment(tag);
//                break;
//            case "completeConditionList":
//                removeFragment(tag);
//                break;
//            case "about":
//                FrameLayout revealView = (FrameLayout) this.findViewById(R.id.menu_fragment_container);
//                revealView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
//                revealView.setVisibility(View.INVISIBLE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        navigation.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.float_up));
//                        navigation.setVisibility(View.VISIBLE);
//                        fragmentManager.beginTransaction()
//                                .remove(fragmentManager.findFragmentByTag("about"))
//                                .commit();
//                    }
//                }, 200);
//                break;
//            default:
//                break;
//        }
//    }

    private void removeFragment(String tag) {
        if (fragmentManager.findFragmentByTag(tag) != null) {
            fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(tag))
                    .commit();
        }
        FrameLayout revealView;
        switch (tag) {
            case "about":
                revealView = (FrameLayout) this.findViewById(R.id.menu_fragment_container);
                revealView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                revealView.setVisibility(View.INVISIBLE);
                navigation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.float_up));
                navigation.setVisibility(View.VISIBLE);
                break;
            case "completeConditionList":
                revealView = (FrameLayout) this.findViewById(R.id.menu_fragment_container);
                revealView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                revealView.setVisibility(View.INVISIBLE);
                actionBar.show();
                navigation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.float_up));
                navigation.setVisibility(View.VISIBLE);
                break;
            case "conditionDetails":
                revealView = (FrameLayout) this.findViewById(R.id.fab_condition_details_revealView);
                revealView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                revealView.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private boolean isFragmentActive(String tag) {
        return fragmentManager.findFragmentByTag(tag) != null && fragmentManager.findFragmentByTag(tag).isAdded();
    }

    @Override
    public void onBackPressed() {
        if (isFragmentActive("conditionDetails")) removeFragment("conditionDetails");
        else if (isFragmentActive("completeConditionList")) removeFragment("completeConditionList");
        else if (isFragmentActive("about")) removeFragment("about");
        else if (!isFragmentActive("symptomFragment")) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.float_up, R.anim.sink_up)
                    .replace(R.id.main_fragment_container, new SymptomFragment(), "symptomFragment")
                    .commit();
            navigation.setItemBackgroundResource(R.color.colorDividerLight);
            navigation.setItemTextColor(ContextCompat.getColorStateList(navigation.getContext(), R.color.colorSecondaryText));
            navigation.setItemIconTintList(ContextCompat.getColorStateList(navigation.getContext(), R.color.colorSecondaryText));
        }
        else
            super.onBackPressed();
    }
}