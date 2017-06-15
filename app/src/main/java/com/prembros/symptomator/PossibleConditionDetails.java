package com.prembros.symptomator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PossibleConditionDetails extends Fragment {
    private static final String SELECTED_CONDITION = "selectedCondition";
    private String mParam1;
    private View rootView;
    private ListView listView;

//    private OnPossibleConditionDetailsInteractionListener mListener;

    public PossibleConditionDetails() {
        // Required empty public constructor
    }

    public static PossibleConditionDetails newInstance(String selectedCondition) {
        PossibleConditionDetails fragment = new PossibleConditionDetails();
        Bundle args = new Bundle();
        args.putString(SELECTED_CONDITION, selectedCondition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(SELECTED_CONDITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_possible_condition_details, container, false);
        listView = (ListView) rootView.findViewById(R.id.selected_condition_list_view);

        new ParseInBackground().execute(JSONReader.read(getContext(), "conditions"), mParam1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listView.getChildCount() == 1) {
                    view.setEnabled(true);
                }
            }
        });

        return rootView;
    }

    private class ParseInBackground extends android.os.AsyncTask<String, Void, ArrayList<PageBeans>>{

        private ArrayList<PageBeans> result;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<PageBeans> doInBackground(String... strings) {
            if (strings[0] != null){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(strings[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result = new JSONParser().parseConditionPage(jsonObject, strings[1]);
            } else {
                Log.d("ERROR in firstAidCheck:", "Class - FirstAidCheck, method - parseInBackground, JSONString was null");
                result = null;
            }
            if (result != null && result.isEmpty()) {
                PageBeans pageBeans = new PageBeans();
                pageBeans.setHeading("Coming soon");
                pageBeans.setContent("We're working on this page. It will most probably be provided in the next update very, very soon!");
                result.add(0, pageBeans);
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<PageBeans> pageBeansArrayList) {
            progressBar.setVisibility(View.GONE);
            PageAdapter adapter;
            if (pageBeansArrayList != null) {
                adapter = new PageAdapter(getContext(), pageBeansArrayList);
                super.onPostExecute(pageBeansArrayList);
            } else {
                pageBeansArrayList = new ArrayList<>();
                PageBeans beans = new PageBeans();
                beans.setHeading("Coming soon...");
                beans.setContent("Info for this condition coming soon!\nBut Feel free to click here to directly search online for the condition");
                adapter = new PageAdapter(getContext(), pageBeansArrayList);
            }
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_possible_condition, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onPossibleConditionDetailsInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnPossibleConditionDetailsInteractionListener) {
//            mListener = (OnPossibleConditionDetailsInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnPossibleConditionDetailsInteractionListener");
//        }
//    }

//    interface OnPossibleConditionDetailsInteractionListener {
//        void onPossibleConditionDetailsInteraction(Uri uri);
//    }
}
