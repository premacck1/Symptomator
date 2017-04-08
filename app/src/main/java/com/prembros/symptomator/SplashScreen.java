package com.prembros.symptomator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    boolean startedBefore = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        }, 100);
    }
}
