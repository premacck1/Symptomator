package com.prembros.symptomator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PossibleConditions extends Fragment {

    private ActionBar actionBar;
    private String[] previousHeading = new String[2];
    private ArrayList<String> selectedSymptoms;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_possible_conditions, container, false);
        Bundle args = getArguments();
        String bodyPart = args.getString("bodyPart");
        selectedSymptoms = args.getStringArrayList("selectedSymptoms");
        String subtitle = "";
        if (selectedSymptoms != null) {
            for (int i = 0; i < selectedSymptoms.size(); i ++) {
                subtitle += selectedSymptoms.get(i);
                if (!(i == selectedSymptoms.size() - 1)) {
                    subtitle += ", ";
                }
            }
        }

        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (actionBar.getTitle() != null) previousHeading[0] = actionBar.getTitle().toString();
            if (actionBar.getSubtitle() != null) previousHeading[1] = actionBar.getSubtitle().toString();
            actionBar.setTitle(R.string.possible_conditions);
            actionBar.setSubtitle(subtitle);
        }

        try {
            if (selectedSymptoms.size() == 1) {
                new ParseInBackground().execute(readJSONFromFile(), bodyPart, selectedSymptoms.get(0));
            } else new ParseInBackground().execute(readJSONFromFile(), bodyPart, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    /**============= Read from JSON file in Internal Memory ===============*/
    public String readJSONFromFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        getActivity().getAssets().open("ConditionsReference.txt")
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

    private class ParseInBackground extends AsyncTask<String, Void, ArrayList<ConditionReferenceBeans>> {

        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ConditionReferenceBeans> doInBackground(String... strings) {
            /*strings[0] contains the  whole json, strings[1] contains the selected body part, strings[2] contains selected symptom*/
            if (strings[0] != null) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(strings[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (strings[2] != null)
                    return new ConditionsReferenceJSONParser().parse(jsonObject, strings[1], strings[2]);
                else return new ConditionsReferenceJSONParser().parseAll(jsonObject, strings[1],
                        selectedSymptoms.toArray(new String[selectedSymptoms.size()]));
            }
            else {
                Log.d("ERROR:", "Class - PossibleConditions, method - parseInBackground, JSONString was null");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ConditionReferenceBeans> conditionReferenceBeanses) {
            progressBar.setVisibility(View.GONE);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
            if (conditionReferenceBeanses != null) {
                List<String> conditions = new ArrayList<>();
                for (int i = 0; i < conditionReferenceBeanses.size(); i++) {
                    String[] individualConditions = conditionReferenceBeanses.get(0).getConditions().split(",");
                    for (String individualCondition : individualConditions) {
                        if (!conditions.contains(individualCondition)) {
                            conditions.add(individualCondition);
                        }
                    }
                }
                recyclerView.setAdapter(new MyRecyclerViewAdapter(false, getContext(), conditions, null));
            }
            else {
                List<String> item = new ArrayList<>();
                item.add("Conditions List returned NULL!");
                recyclerView.setAdapter(new MyRecyclerViewAdapter(false, getContext(), item, null));
            }
        }
    }

    @Override
    public void onDestroy() {
        actionBar.setTitle(previousHeading[0]);
        actionBar.setSubtitle(previousHeading[1]);
        super.onDestroy();
    }
}
