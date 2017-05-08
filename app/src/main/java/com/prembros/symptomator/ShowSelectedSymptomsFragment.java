package com.prembros.symptomator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowSelectedSymptomsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> selectedSymptoms = null;
    private ListView listView;

    public ShowSelectedSymptomsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        selectedSymptoms = args.getStringArrayList("selectedSymptoms");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_selected_symptoms, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_selected_symptoms);
        listView.setVisibility(View.VISIBLE);
        final ImageButton close = (ImageButton) rootView.findViewById(R.id.button_close_selected_symptoms);
        final ImageButton next = (ImageButton) rootView.findViewById(R.id.button_show_selected_symptoms);

        boolean isListEmpty = false;
        if (selectedSymptoms != null && selectedSymptoms.isEmpty()) {
            isListEmpty = true;
            selectedSymptoms.add("You have not selected any symptoms\nSelect a symptom to continue");
        }
        if (isListEmpty) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onShowSelectedSymptomsFragmentInteraction("close");
                    }
                }
            }, 2000);
        }
        listView.setAdapter(
                new ArrayAdapter<>(getContext(), R.layout.list_item, selectedSymptoms)
        );

        close.setOnClickListener(this);
        next.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_show_selected_symptoms:
                mListener.onShowSelectedSymptomsFragmentInteraction("show");
                break;
            case R.id.button_close_selected_symptoms:
                mListener.onShowSelectedSymptomsFragmentInteraction("close");
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        void onShowSelectedSymptomsFragmentInteraction(String item);
    }
}
