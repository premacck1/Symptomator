package com.prembros.symptomator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;

public class SplashScreen extends AppCompatActivity {

    private boolean startedBefore = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createShortcuts();

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    startActivity(new Intent(SplashScreen.this, Introduction.class));
                    startedBefore = false;

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();

                    // Finish This Activity
                    SplashScreen.this.finish();
                }

                //  If the activity has been started before...
                else {
                    startedBefore = true;
                }
            }
        });

        // Start the thread
        t.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startedBefore) {

                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    SplashScreen.this.finish();
                }
            }
        }, 0);
    }

    private void createShortcuts() {
        ShortcutManager shortcutManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);

            Intent call108 = new Intent(this, CallEmergencyServices.class);
            call108.setAction(Intent.ACTION_VIEW);

            ShortcutInfo call108Shortcut = new ShortcutInfo.Builder(this, "call_108")
                    .setIntent(call108)
                    .setShortLabel(getString(R.string.emergency_number))
                    .setLongLabel(getString(R.string.call_emergency_services))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_call_emergency_services))
                    .setRank(5)
                    .build();

            Intent nearerstHospitalIntent = new Intent(this, MapsActivity.class);
            nearerstHospitalIntent.putExtra("showWhat", "hospital");
            nearerstHospitalIntent.putExtra("showNearest", true);
            nearerstHospitalIntent.setAction(Intent.ACTION_VIEW);

            ShortcutInfo nearestHospitalShortcut = new ShortcutInfo.Builder(this, "nearest_hospital")
                    .setIntent(nearerstHospitalIntent)
                    .setShortLabel(getString(R.string.nearest_hospital))
                    .setLongLabel(getString(R.string.get_to_the_nearest_hospital))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_nearest_hospital))
                    .setRank(2)
                    .build();

            Intent nearestDoctorIntent = new Intent(this, MapsActivity.class);
            nearestDoctorIntent.putExtra("showWhat", "doctor");
            nearestDoctorIntent.putExtra("showNearest", true);
            nearestDoctorIntent.setAction(Intent.ACTION_VIEW);

            ShortcutInfo nearestDoctorShortcut = new ShortcutInfo.Builder(this, "nearest_doctor")
                    .setIntent(nearestDoctorIntent)
                    .setShortLabel(getString(R.string.nearest_doctor))
                    .setLongLabel(getString(R.string.get_to_the_nearest_doctor))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_nearest_doctor))
                    .setRank(1)
                    .build();

//        Intent nearbyHospitalsIntent = new Intent(this, MapsActivity.class);
//        nearbyHospitalsIntent.putExtra("showWhat", "hospital");
//        nearbyHospitalsIntent.putExtra("showNearest", false);
//        nearbyHospitalsIntent.setAction(Intent.ACTION_VIEW);
//
//        ShortcutInfo nearbyHospitalsShortcut = new ShortcutInfo.Builder(this, "find_nearby_hospitals")
//                .setIntent(nearbyHospitalsIntent)
//                .setShortLabel(getString(R.string.nearby_hospitals))
//                .setLongLabel(getString(R.string.find_nearby_hospitals))
//                .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_nearby_hospitals))
//                .setRank(4)
//                .build();
//
//        Intent nearbyDoctorsIntent = new Intent(this, MapsActivity.class);
//        nearbyDoctorsIntent.putExtra("showWhat", "doctor");
//        nearbyDoctorsIntent.putExtra("showNearest", false);
//        nearbyDoctorsIntent.setAction(Intent.ACTION_VIEW);
//
//        ShortcutInfo nearbyDoctorsShortcut = new ShortcutInfo.Builder(this, "find_nearby_doctors")
//                .setIntent(nearbyDoctorsIntent)
//                .setShortLabel(getString(R.string.nearby_doctors))
//                .setLongLabel(getString(R.string.find_nearby_doctors))
//                .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_nearby_doctors))
//                .setRank(3)
//                .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(
                    call108Shortcut,
                    nearestHospitalShortcut,
                    nearestDoctorShortcut
//                nearbyHospitalsShortcut,
//                nearbyDoctorsShortcut,
            ));
        }
    }
}
