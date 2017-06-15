package com.prembros.symptomator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowSelectedSymptoms extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> selectedSymptoms = null;
    private ListView listView;

    public ShowSelectedSymptoms() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = this.getArguments();
        selectedSymptoms = args.getStringArrayList("selectedSymptoms");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_selected_symptoms, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_selected_symptoms);
        listView.setVisibility(View.VISIBLE);
//        final ImageButton close = (ImageButton) rootView.findViewById(R.id.button_close_selected_symptoms);
        final FloatingActionButton next = (FloatingActionButton) rootView.findViewById(R.id.button_show_selected_symptoms);

        boolean isListEmpty;
        if (selectedSymptoms != null && selectedSymptoms.isEmpty()) {
            isListEmpty = true;
            selectedSymptoms.add("You have not selected any symptoms\nSelect a symptom to continue");
            next.setEnabled(false);
        } else {
            isListEmpty = false;
            next.setEnabled(true);
        }
        if (isListEmpty) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onShowSelectedSymptomsFragmentInteraction("close", null);
                    }
                }
            }, 2000);
        }
        listView.setAdapter(
                new ArrayAdapter<>(getContext(), R.layout.list_item, selectedSymptoms)
        );

//        close.setOnClickListener(this);
        next.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AppCompatTextView textView = (AppCompatTextView) view;
                ArrayList<String> item = new ArrayList<>();
                item.add(textView.getText().toString());
                mListener.onShowSelectedSymptomsFragmentInteraction("show", item);
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_show_selected_symptoms:
                mListener.onShowSelectedSymptomsFragmentInteraction("show", selectedSymptoms);
                break;
//            case R.id.button_close_selected_symptoms:
//                mListener.onShowSelectedSymptomsFragmentInteraction("close", null);
//                break;
            default:
                break;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPossibleConditionDetailsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        listView.removeAllViews();
        listView.setVisibility(View.INVISIBLE);
        mListener = null;
    }

    interface OnFragmentInteractionListener {
        void onShowSelectedSymptomsFragmentInteraction(String item, @Nullable ArrayList<String> selectedSymptoms);
    }
}
