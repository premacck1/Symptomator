package com.prembros.symptomator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class CompleteSymptomList extends Fragment {

    private OnFragmentInteractionListener mListener;
    private MyRecyclerViewAdapter recyclerViewAdapter;

    public CompleteSymptomList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_complete_symptom_list, container, false);
        final AutoCompleteTextView searchBar = (AutoCompleteTextView) rootView.findViewById(R.id.complete_symptom_search_bar);
        final RecyclerView list = (RecyclerView) rootView.findViewById(R.id.first_aid_list);

        final List<String> completeSymptomList = new SymptomDirectory().showAllSymptoms();
        recyclerViewAdapter = new MyRecyclerViewAdapter(
                getContext(), completeSymptomList, null, mListener
        );
        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(recyclerViewAdapter);

        //Set the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charSequence = charSequence.toString().toLowerCase();
                final List<String> filteredList = new ArrayList<>();

                for (int x = 0; x < completeSymptomList.size(); x++){
                    final String text = completeSymptomList.get(x).toLowerCase();
                    if (text.contains(charSequence))
                        filteredList.add(completeSymptomList.get(x));
                }
                if (filteredList.isEmpty()){
                    filteredList.add("Still didn't find it?\nClick here to search online");
                }

                list.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), filteredList, null, mListener);
                list.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
        return rootView;
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
        mListener = null;
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction(String item);
    }
}
