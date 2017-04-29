package com.prembros.symptomator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CompleteSymptomList extends Fragment {

    private OnFragmentInteractionListener mListener;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    List<String> completeSymptomList;
    DatabaseHolder db;

    public CompleteSymptomList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_complete_symptom_list, container, false);
        final TextInputEditText searchBar = (TextInputEditText) rootView.findViewById(R.id.complete_symptom_search_bar);
        final RecyclerView list = (RecyclerView) rootView.findViewById(R.id.recyclerview_list);

        completeSymptomList = new ArrayList<>();
        db = new DatabaseHolder(getContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.open();
                Cursor cursor = db.returnAllSymptoms();
                if (cursor != null) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()){
                        try {
                            completeSymptomList.add(cursor.getString(cursor.getColumnIndex("Symptom")));
                            cursor.moveToNext();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                completeSymptomList.add("Still could not find it?\nClick here to search online");
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        }).start();

        recyclerViewAdapter = new MyRecyclerViewAdapter( false,
                getContext(), completeSymptomList, mListener
        );
        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(recyclerViewAdapter);

        list.addOnItemTouchListener(new RecyclerViewOnItemClickListener(
                getContext(), new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                View v = inflater.inflate(R.layout.web_search_dialog, new ViewGroup(getContext()) {
                    @Override
                    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
                    }
                }, false);
                final TextInputEditText editText = (TextInputEditText) v.findViewById(R.id.web_search_dialog_search_bar);
                editText.append(searchBar.getText());
                int itemCount = recyclerViewAdapter.getItemCount();
                if (position == itemCount - 1){
//                    SEARCH ONLINE
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setView(v)
                            .setTitle("Search online")
                            .setPositiveButton("search", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String query = null;
                                    try {
                                        query = URLEncoder.encode(editText.getText().toString(), "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    String url = "http://www.google.com/search?q=" + query;
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editText.requestFocus();
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }, 500);
                }
            }
        }));

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
                if (filteredList.size() != completeSymptomList.size())
                    filteredList.add("Still could not find it?\nClick here to search online");

                list.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewAdapter = new MyRecyclerViewAdapter(false, getContext(), filteredList, mListener);
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
