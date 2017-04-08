package com.prembros.symptomator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SymptomFragment.OnSymptomFragmentInteractionListener,
        ServicesFragment.OnServicesFragmentInteractionListener,
        FirstAidFragment.OnFirstAidListFragmentInteractionListener {

    private FragmentManager fragmentManager;

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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        return super.onOptionsItemSelected(item);
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
}
