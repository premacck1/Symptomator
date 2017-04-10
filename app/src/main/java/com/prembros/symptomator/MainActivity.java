package com.prembros.symptomator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SymptomFragment.OnSymptomFragmentInteractionListener,
        ServicesFragment.OnServicesFragmentInteractionListener,
        FirstAidFragment.OnFirstAidListFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_by_symptom:
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.fragment_anim_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.main_fragment_container, new SymptomFragment(), "symptomFragment")
                            .commit();
                    return true;
                case R.id.navigation_by_first_aid:
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.fragment_anim_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.main_fragment_container, new FirstAidFragment(), "firstAidFragment")
                            .commit();
                    return true;
                case R.id.navigation_by_services:
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.fragment_anim_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.main_fragment_container, new ServicesFragment(), "hospitalFragment")
                            .commit();
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.fragment_anim_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.main_fragment_container, new SymptomFragment(), "symptomFragment")
                .commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void onButtonClick(View view){
        switch (view.getId()){
            case R.id.call_108:
                String locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = getResources().getConfiguration().getLocales().get(0).getCountry();
                } else {
                    //noinspection deprecation
                    locale = getResources().getConfiguration().locale.getCountry();
                }
                if (locale.equalsIgnoreCase("GB") || locale.equalsIgnoreCase("IN")){
                    call(108);
                }
                break;
            case R.id.find_hospitals_nearby:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            default:
                Toast.makeText(this, "This feature is coming soon!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    void call(int number){
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setIcon(R.drawable.ic_call)
                .setTitle("Emergency number")
                .setMessage("We're going to call \"" + number + "\" for you\nClick OKAY to confirm.")
                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(callIntent);
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
                navigation.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.sink_down));
                navigation.setVisibility(View.GONE);
                fragmentManager.beginTransaction().add(R.id.menu_fragment_container, new About(), "about").commit();
            default:
                return false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onSymptomFragmentInteraction(String selectedAge, String selectedSex, String selectedBodyArea, String selectedBodyPart) {
        Intent intent = new Intent(this, SymptomCheck.class);
        intent.putExtra("selectedAge" , selectedAge);
        intent.putExtra("selectedSex", selectedSex);
        intent.putExtra("selectedBodyArea", selectedBodyArea);
        intent.putExtra("selectedBodyPart", selectedBodyPart);
        startActivity(intent);
    }

    @Override
    public void onServicesFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(String item) {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("about") != null && getSupportActionBar() != null){
            navigation.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.float_up));
            navigation.setVisibility(View.VISIBLE);
            getSupportActionBar().show();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                    .remove(getSupportFragmentManager().findFragmentByTag("about"))
                    .commit();
        } else super.onBackPressed();
    }
}
