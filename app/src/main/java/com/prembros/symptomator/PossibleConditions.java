package com.prembros.symptomator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.balysv.materialripple.MaterialRippleLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PossibleConditions extends Fragment {

    private ActionBar actionBar;
    private final String[] previousHeading = new String[2];
    private ArrayList<String> selectedSymptoms;
    private View rootView;
    private RecyclerView recyclerView;
    private OnPossibleConditionsFragmentInteractionsListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            if (actionBar.getTitle() != null) previousHeading[0] = actionBar.getTitle().toString();
            if (actionBar.getSubtitle() != null) previousHeading[1] = actionBar.getSubtitle().toString();
            actionBar.setTitle(R.string.possible_conditions);
            actionBar.setSubtitle(subtitle);
        }

        if (selectedSymptoms.size() == 1) {
            new ParseInBackground().execute(JSONReader.read(getContext(), "ConditionsReference.txt"), bodyPart, selectedSymptoms.get(0));
        } else new ParseInBackground().execute(JSONReader.read(getContext(), "ConditionsReference.txt"), bodyPart, null);

        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(getContext(), new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mListener.OnPossibleConditionsInteractionListener(
                        ((AppCompatTextView)((MaterialRippleLayout)((LinearLayout) view).getChildAt(0)).getChildAt(0)).getText().toString());
            }
        }));
        return rootView;
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
                    return new JSONParser().parseSingleConditionList(jsonObject, strings[1], strings[2]);
                else return new JSONParser().parseAllConditionsList(jsonObject, strings[1],
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
            if (conditionReferenceBeanses != null) {
                List<String> conditions = new ArrayList<>();
                for (int i = 0; i < conditionReferenceBeanses.size(); i++) {
                    String[] individualConditions = conditionReferenceBeanses.get(i).getConditions().split(",");
                    for (String individualCondition : individualConditions) {
                        if (!conditions.contains(individualCondition)) {
                            conditions.add(individualCondition);
                        }
                    }
                }
                recyclerView.setAdapter(new RecyclerViewAdapter(false, getContext(), conditions));
            }
            else {
                List<String> item = new ArrayList<>();
                item.add("We're working on these conditions.");
                recyclerView.setAdapter(new RecyclerViewAdapter(false, getContext(), item));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_symptom_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPossibleConditionsFragmentInteractionsListener) {
            mListener = (OnPossibleConditionsFragmentInteractionsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPossibleConditionDetailsInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        actionBar.setTitle(previousHeading[0]);
        actionBar.setSubtitle(previousHeading[1]);
        super.onDestroy();
    }

    interface OnPossibleConditionsFragmentInteractionsListener {
        void OnPossibleConditionsInteractionListener(String item);
    }
}
