package com.prembros.symptomator;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FirstAidCheck extends AppCompatActivity {

    private final String [] JSONString = new String[2];
//    ArrayList<PageBeans> firstAidTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid_check);

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
            case R.id.action_call_108:
                new CallEmergencyServices(this);
                return true;
            default:
                return false;
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
            Log.d("ERROR in firstAidCheck:", "Class - FirstAidCheck, method - parseInBackground, JSONString was null");
            result = null;
        }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<PageBeans> pageBeansArrayList) {
            if (pageBeansArrayList != null) {
                ListView listView = (ListView) FirstAidCheck.this.findViewById(R.id.first_aid_check_list_view);
                PageAdapter adapter = new PageAdapter(FirstAidCheck.this, pageBeansArrayList);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                super.onPostExecute(pageBeansArrayList);
            } else {
                Toast.makeText(FirstAidCheck.this, "First aid info for this topic coming soon!", Toast.LENGTH_SHORT).show();
                FirstAidCheck.this.finish();
            }
        }
    }
}