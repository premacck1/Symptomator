package com.prembros.symptomator;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.Locale;

import io.codetail.animation.SupportAnimator;
import io.codetail.widget.RevealFrameLayout;

import static io.codetail.animation.ViewAnimationUtils.createCircularReveal;

public class MainActivity extends AppCompatActivity implements
        SymptomFragment.OnSymptomFragmentInteractionListener,
        FirstAidFragment.OnFirstAidListFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;
    private LinearLayout revealView;
    private boolean hidden = true;
    boolean somethingIsActive = false;
    private SupportAnimator animator;
    private RevealFrameLayout revealViewContainer;
    private Rect rect = null;
    private ActionBar actionBar;
    private String[] previousTitles = new String[2];
    private int[] touchCoordinate = new int[2];
//    private GoogleApiClient googleApiClient;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_by_symptom:
                    if (fragmentManager.findFragmentByTag("symptomFragment") == null) {
                        fragmentManager.beginTransaction().setCustomAnimations(
                                R.anim.fragment_anim_in, android.R.anim.fade_out,
                                android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(R.id.main_fragment_container, new SymptomFragment(), "symptomFragment")
                                .commit();
                    }
                    return true;
                case R.id.navigation_by_first_aid:
                    if (fragmentManager.findFragmentByTag("firstAidFragment") == null) {
                        fragmentManager.beginTransaction().setCustomAnimations(
                                R.anim.fragment_anim_in, android.R.anim.fade_out,
                                android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(R.id.main_fragment_container, new FirstAidFragment(), "firstAidFragment")
                                .commit();
                    }
                    return true;
                case R.id.navigation_by_services:
                    if (hidden) {
                        if (actionBar != null) {
                            previousTitles[0] = actionBar.getTitle().toString();
                            previousTitles[1] = actionBar.getSubtitle().toString();
                            actionBar.setTitle(R.string.services);
                            actionBar.setSubtitle(R.string.services_subtitle);
                        }
                        animationForward(revealView, touchCoordinate);
                        hidden = false;
                    } else {
                        animationReversed(revealView);
                    }
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        if( googleApiClient != null )
//            googleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();

        revealViewContainer = (RevealFrameLayout) this.findViewById(R.id.reveal_view_container);
        revealView = (LinearLayout) this.findViewById(R.id.services_revealView);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.fragment_anim_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.main_fragment_container, new SymptomFragment(), "symptomFragment")
                .commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        googleApiClient = new GoogleApiClient
//                .Builder(this)
//                .enableAutoManage(this, 0, this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
    }

    public void onButtonClick(View view){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationReversed(revealView);
            }
        }, 250);
        switch (view.getId()){
            case R.id.call_108:
                callEmergencyServices(MainActivity.this);
                break;
            case R.id.find_hospitals_nearby:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            default:
//                Toast.makeText(this, "This feature is coming soon!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static void callEmergencyServices(final Context context){
        DatabaseHolder db = new DatabaseHolder(context);
        db.open();
        Cursor cursor = db.returnEmergencyNumber(getUserCountry(context));
        cursor.moveToFirst();
        call(
                cursor.getString(cursor.getColumnIndex("Country")),                         //Country name
                Integer.parseInt(cursor.getString(cursor.getColumnIndex("Number"))),        //Emergency number
                context
        );
        cursor.close();
        db.close();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchCoordinate[0] = (int) ev.getX();
        touchCoordinate[1] = (int) ev.getY();
        if (rect != null) {
            int height = findViewById(R.id.call_108).getHeight();
            height = height + (height / 2);
            revealViewContainer.getHitRect(rect);
            rect.bottom += height;
            rect.top += height;
            if (!hidden && !rect.contains((int) ev.getX(), (int) ev.getY())) {
                animationReversed(revealView);
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static void call(String country, int number, final Context context){
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setIcon(R.drawable.ic_call)
                .setTitle("Emergency number")
                .setMessage("*__ " + country + " __*\n\nWe're going to call \"" + number + "\" for you\nClick OKAY to confirm.")
                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(callIntent);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void animationForward(View mRevealView, int[] center){
        int centerX = center[0];
        int centerY = center[1];
        int startRadius = 0;
        int endRadius = (int) (Math.hypot(mRevealView.getWidth() * 1.6, mRevealView.getHeight() * 1.6));
        animator = createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);

        animator.start();
        mRevealView.setVisibility(View.VISIBLE);
        rect = new Rect(revealViewContainer.getLeft(), revealViewContainer.getTop(),
                revealViewContainer.getRight(), revealViewContainer.getBottom());
    }

    public void animationReversed(@Nullable final View mRevealView){
        if (actionBar != null) {
            actionBar.setTitle(previousTitles[0]);
            actionBar.setSubtitle(previousTitles[1]);
        }
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
                    if (mRevealView != null) {
                        mRevealView.setVisibility(View.INVISIBLE);
                    }
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
            case R.id.action_about:
                animationForward(this.findViewById(R.id.menu_fragment_container), touchCoordinate);
                navigation.setVisibility(View.GONE);
                fragmentManager.beginTransaction().add(R.id.menu_fragment_container, new About(), "about").commit();
                somethingIsActive = true;
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
    protected void onStop() {
//        if( googleApiClient != null && googleApiClient.isConnected() ) {
//            mAdapter.setGoogleApiClient( null );
//            googleApiClient.disconnect();
//        }
        super.onStop();
    }

    void removeFragmentIfAttached(final String tag){
        if (fragmentManager.findFragmentByTag(tag) != null) {
            animationReversed(this.findViewById(R.id.menu_fragment_container));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigation.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.float_up));
                    navigation.setVisibility(View.VISIBLE);
                    fragmentManager.beginTransaction()
                            .remove(fragmentManager.findFragmentByTag(tag))
                            .commit();
                }
            }, 200);
        }
    }

    @Override
    public void onBackPressed() {
        if (!hidden) animationReversed(revealView);
        else if (somethingIsActive){
            removeFragmentIfAttached("about");
            somethingIsActive = false;
        }
        else
            super.onBackPressed();
    }
}
