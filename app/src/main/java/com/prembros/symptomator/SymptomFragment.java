package com.prembros.symptomator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.Toast;

public class SymptomFragment extends Fragment {

//    private String selectedAge = null;
//    private String selectedBodyArea = null;
//    private String selectedBodyPart = null;
    private String selectedSex = null;
    private Spinner selectAge;
    private Spinner selectBodyArea;
    private Spinner selectBodyPart;
    private CheckedTextView[] allCheckedTextViews;

    private OnSymptomFragmentInteractionListener mListener;

    public SymptomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_symptom, container, false);

//        SET ACTION BAR
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle(getString(R.string.symptom_subtitle));
        }

        selectAge = (Spinner) rootView.findViewById(R.id.selected_age);
        selectBodyArea = (Spinner) rootView.findViewById(R.id.selected_body_area);
        selectBodyPart = (Spinner) rootView.findViewById(R.id.selected_body_part);
        CheckedTextView male = (CheckedTextView) rootView.findViewById(R.id.radio_male);
        CheckedTextView female = (CheckedTextView) rootView.findViewById(R.id.radio_female);
        allCheckedTextViews = new CheckedTextView[] {male, female};
        AppCompatButton submitButton = (AppCompatButton) rootView.findViewById(R.id.submit_button);
        submitButton.setSupportBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E64A19")));

//        selectAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                selectedAge = selectAge.getSelectedItem().toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });

        selectBodyArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

//                selectedBodyArea = selectBodyArea.getSelectedItem().toString();
                String[] bodyPartList;
                ArrayAdapter<String> bodyPartListAdapter;
                switch (position){
                    case 1:
                        bodyPartList = getResources().getStringArray(R.array.head_area);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList
                        );
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        bodyPartList = getResources().getStringArray(R.array.chest_area);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList
                        );
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        bodyPartList = getResources().getStringArray(R.array.abdomen);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList
                        );
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        bodyPartList = getResources().getStringArray(R.array.back_area);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList
                        );
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        bodyPartList = getResources().getStringArray(R.array.pelvic);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList
                        );
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        bodyPartList = getResources().getStringArray(R.array.arm);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList
                        );
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        bodyPartList = getResources().getStringArray(R.array.legs);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList);
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    default:
                        selectBodyPart.setSelection(0);
                        selectBodyPart.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        selectBodyPart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                selectedBodyPart = selectBodyPart.getSelectedItem().toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAction(view);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAction(view);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectAge.getSelectedItemPosition()>0 && selectedSex != null
                        && selectBodyArea.getSelectedItemPosition()>0 && selectBodyPart.getSelectedItemPosition()>0) {
                    mListener.onSymptomFragmentInteraction(
                            selectAge.getSelectedItem().toString(),
                            selectedSex,
                            selectBodyArea.getSelectedItem().toString(),
                            selectBodyPart.getSelectedItem().toString());
                } else
                    Toast.makeText(getContext(), "Select proper options and try again", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public void clickAction(View v) {
        CheckedTextView temp = (CheckedTextView) v;
        if(temp !=null) {
            if (!temp.isChecked()) {
                for (CheckedTextView item : allCheckedTextViews) {
                    item.setChecked(false);
                    item.setTextColor(Color.parseColor("#000000"));
                }
                temp.setChecked(true);
                temp.setTextColor(Color.parseColor("#FFFFFF"));
                selectedSex = temp.getText().toString();
                temp.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_selected));
            } else {
                temp.setChecked(false);
                temp.setTextColor(Color.parseColor("#000000"));
                selectedSex = null;
                temp.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_deselected));
            }
        }
    }

    public void resetViews(){
        selectAge.setSelection(0);
        selectBodyPart.setSelection(0);
        selectBodyArea.setSelection(0);
        for (CheckedTextView item : allCheckedTextViews) {
            item.setChecked(false);
            item.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSymptomFragmentInteractionListener) {
            mListener = (OnSymptomFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSymptomFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        resetViews();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    interface OnSymptomFragmentInteractionListener {
        void onSymptomFragmentInteraction(String selectedAge, String selectedSex, String selectedBodyArea, String selectedBodyPart);
    }
}
