package com.prembros.symptomator;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

    private String selectedSex = null;
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

        selectBodyArea = (Spinner) rootView.findViewById(R.id.selected_body_area);
        selectBodyPart = (Spinner) rootView.findViewById(R.id.selected_body_part);
        CheckedTextView male = (CheckedTextView) rootView.findViewById(R.id.radio_male);
        CheckedTextView female = (CheckedTextView) rootView.findViewById(R.id.radio_female);
        allCheckedTextViews = new CheckedTextView[] {male, female};
//        submitButton.setSupportBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E64A19")));

        selectBodyArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

//                selectedBodyArea = selectBodyArea.getSelectedItem().toString();
                String[] bodyPartList;
                ArrayAdapter<String> bodyPartListAdapter;
                switch (position){
                    case 0:
                        if (selectBodyPart.getVisibility() == View.VISIBLE) {
                            selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.sink_up));
                            selectBodyPart.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 1:
                        bodyPartList = getResources().getStringArray(R.array.head_area);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList
                        );
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_down));
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
                        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_down));
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
                        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_down));
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
                        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_down));
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
                        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_down));
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
                        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_down));
                        selectBodyPart.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        bodyPartList = getResources().getStringArray(R.array.legs);
                        bodyPartListAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spinner_item, bodyPartList);
                        bodyPartListAdapter.setDropDownViewResource(R.layout.spinner_item);
                        bodyPartListAdapter.notifyDataSetChanged();
                        selectBodyPart.setAdapter(bodyPartListAdapter);
                        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_down));
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

        selectBodyPart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    startActivityIfFormComplete();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        return rootView;
    }

    void startActivityIfFormComplete(){
        if (selectedSex != null && selectBodyArea.getSelectedItemPosition() > 0 && selectBodyPart.getSelectedItemPosition() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.onSymptomFragmentInteraction(
                            selectedSex,
                            selectBodyArea.getSelectedItem().toString(),
                            selectBodyPart.getSelectedItem().toString());
                }
            }, 200);
        }
        if (selectedSex == null){
            Toast.makeText(getContext(), "Select gender to proceed.", Toast.LENGTH_SHORT).show();
        }
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
                startActivityIfFormComplete();
            } else {
                temp.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_deselected));
            }
        }
    }

    public void resetViews(){
        selectBodyArea.setSelection(0);
//        selectBodyPart.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
//        for (CheckedTextView item : allCheckedTextViews) {
//            item.setChecked(false);
//            item.setTextColor(Color.parseColor("#000000"));
//        }
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
        resetViews();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    interface OnSymptomFragmentInteractionListener {
        void onSymptomFragmentInteraction(String selectedSex, String selectedBodyArea, String selectedBodyPart);
    }
}
