package com.prembros.symptomator;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FirstAidCheck extends AppCompatActivity implements View.OnClickListener {

    private final String [] JSONString = new String[2];
    private int textSize = 14;
    private ListView listView;
    private ArrayList<PageBeans> pageBeansArrayList;
    private FloatingActionButton increaseTextSize;
    private FloatingActionButton decreaseTextSize;
//    ArrayList<PageBeans> firstAidTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid_check);

        increaseTextSize = (FloatingActionButton) this.findViewById(R.id.increase_text_size);
        decreaseTextSize = (FloatingActionButton) this.findViewById(R.id.decrease_text_size);

        String topic = getIntent().getExtras().getString("topic");
        if (topic != null){
            topic = topic.replace(" ", "_");
            JSONString[0] = topic;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && topic != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setTitle("First aid treatment");
            actionBar.setSubtitle(topic.replace("_", " "));
        }

        String string = JSONReader.read(this, "FirstAidJSON.txt");
        JSONString[1] = string;
        new ParseInBackground().execute(JSONString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_first_aid_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_text_size:
                increaseTextSize.setVisibility(View.VISIBLE);
                increaseTextSize.startAnimation(AnimationUtils.loadAnimation(this, R.anim.float_up));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        decreaseTextSize.setVisibility(View.VISIBLE);
                        decreaseTextSize.startAnimation(AnimationUtils.loadAnimation(FirstAidCheck.this, R.anim.float_up));
                    }
                }, 200);
                increaseTextSize.setOnClickListener(this);
                decreaseTextSize.setOnClickListener(this);
                return true;
            case R.id.action_call_108:
                new CallEmergencyServices(this);
                return true;
            default:
                return false;
        }
    }

    private void alterTextSize(boolean increase) {
        if (increase) textSize += 2;
        else textSize -= 2;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new PageAdapter(FirstAidCheck.this, pageBeansArrayList, textSize));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.increase_text_size:
                alterTextSize(true);
                break;
            case R.id.decrease_text_size:
                alterTextSize(false);
                break;
            default:
                break;
        }
    }

    private class ParseInBackground extends android.os.AsyncTask<String, Void, ArrayList<PageBeans>>{

        private ArrayList<PageBeans> result;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) FirstAidCheck.this.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<PageBeans> doInBackground(String... strings) {
            if (strings[1] != null){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(strings[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result = new JSONParser().parseFirstAidJSON(jsonObject, strings[0]);
            } else {
            Log.e("ERROR in firstAidCheck:", "Class - FirstAidCheck, method - parseInBackground, JSONString was null");
            result = null;
        }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<PageBeans> pageBeansArrayList) {
            if (pageBeansArrayList != null) {
                FirstAidCheck.this.pageBeansArrayList = pageBeansArrayList;
                listView = (ListView) FirstAidCheck.this.findViewById(R.id.first_aid_check_list_view);
                PageAdapter adapter = new PageAdapter(FirstAidCheck.this, pageBeansArrayList, textSize);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                super.onPostExecute(pageBeansArrayList);
            } else {
                Toast.makeText(FirstAidCheck.this, "First aid info for this topic coming soon!", Toast.LENGTH_SHORT).show();
                FirstAidCheck.this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (increaseTextSize.getVisibility() == View.VISIBLE) {
            increaseTextSize.setVisibility(View.INVISIBLE);
            increaseTextSize.startAnimation(AnimationUtils.loadAnimation(this, R.anim.sink_down));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    decreaseTextSize.setVisibility(View.INVISIBLE);
                    decreaseTextSize.startAnimation(AnimationUtils.loadAnimation(FirstAidCheck.this, R.anim.sink_down));
                }
            }, 200);
        }
        else super.onBackPressed();
    }
}