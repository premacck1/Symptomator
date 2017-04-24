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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FirstAidCheck extends AppCompatActivity {

    private String [] JSONString = new String[2];
//    ArrayList<Beans> firstAidTopic;

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
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left);
            actionBar.setTitle("First aid treatment");
            actionBar.setSubtitle(topic.replace("_", " "));
        }

        try{
            String string = readJSONFromFile();
            if (string != null)
                JSONString[1] = string;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        new ParseInBackground().execute(JSONString);
    }

    /**============= Read from JSON file in Internal Memory ===============*/
    public String readJSONFromFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        getAssets().open("FirstAidJSON.txt")
                )
        );
        String read;
        StringBuilder builder = new StringBuilder("");

        while((read = bufferedReader.readLine()) != null){
            builder.append(read);
        }
        bufferedReader.close();
        return builder.toString();
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
                MainActivity.callEmergencyServices(this);
                return true;
            default:
                return false;
        }
    }

    private class ParseInBackground extends android.os.AsyncTask<String, Void, ArrayList<Beans>>{

        private ArrayList<Beans> result;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) FirstAidCheck.this.findViewById(R.id.first_aid_check_progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Beans> doInBackground(String... strings) {
            if (strings[1] != null){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(strings[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result = new JSONParser().parseFirstAidJSON(jsonObject, "First_aid", strings[0]);
            } else {
            Log.d("ERROR in firstAidCheck:", "Class - FirstAidCheck, method - parseInBackground, JSONString was null");
            result = null;
        }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Beans> beansArrayList) {
            if (beansArrayList != null) {
                ListView listView = (ListView) FirstAidCheck.this.findViewById(R.id.first_aid_check_list_view);
                FirstAidDetailsAdapter adapter = new FirstAidDetailsAdapter(FirstAidCheck.this, beansArrayList);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                super.onPostExecute(beansArrayList);
            } else {
                Toast.makeText(FirstAidCheck.this, "First aid info for this topic coming soon!", Toast.LENGTH_SHORT).show();
                FirstAidCheck.this.finish();
            }
        }
    }
}