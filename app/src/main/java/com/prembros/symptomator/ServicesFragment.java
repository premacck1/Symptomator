package com.prembros.symptomator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServicesFragment extends Fragment implements View.OnClickListener {
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

//    private OnServicesInteractionListener mListener;

    public ServicesFragment() {
        // Required empty public constructor
    }

//    public static ServicesFragment newInstance(String param1, String param2) {
//        ServicesFragment fragment = new ServicesFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_services, container, false);

//        SET ACTION BAR
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.services));
            actionBar.setSubtitle(getString(R.string.always_improving));
        }

        TextView call_108 = (TextView) rootView.findViewById(R.id.call_108);
        TextView nearby_hospitals = (TextView) rootView.findViewById(R.id.find_hospitals_nearby);
        call_108.setOnClickListener(this);
        nearby_hospitals.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.call_108:
                new CallEmergencyServices(getContext());
                break;
            case R.id.find_hospitals_nearby:
                startActivity(new Intent(getContext(), MapsActivity.class));
                break;
            default:
//                Toast.makeText(this, "This feature is coming soon!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnServicesInteractionListener) {
//            mListener = (OnServicesInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnServicesInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    interface OnServicesInteractionListener {
//        void onServicesInteraction(Uri uri);
//    }
}
