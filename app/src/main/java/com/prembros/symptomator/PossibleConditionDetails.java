package com.prembros.symptomator;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PossibleConditionDetails extends Fragment implements View.OnClickListener {

    private static final String SELECTED_CONDITION = "selectedCondition";
    private String selectedCondition;
    private View rootView;
    private ListView listView;
    private FrameLayout viewRoot;
    private int textSize = 14;
    private ArrayList<PageBeans> pageBeansArrayList;
    private FloatingActionButton increaseTextSize;
    private FloatingActionButton decreaseTextSize;

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
            selectedCondition = getArguments().getString(SELECTED_CONDITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_possible_condition_details, container, false);
        viewRoot = (FrameLayout) rootView.findViewById(R.id.condition_details_container);

        listView = (ListView) rootView.findViewById(R.id.selected_condition_list_view);
        increaseTextSize = (FloatingActionButton) rootView.findViewById(R.id.increase_text_size);
        decreaseTextSize = (FloatingActionButton) rootView.findViewById(R.id.decrease_text_size);

        new ParseInBackground().execute(JSONReader.read(getContext(), "conditions.txt"), selectedCondition);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listView.getChildCount() == 1) {
                    view.setEnabled(true);
                }
            }
        });

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                prefs = getActivity().getPreferences(MODE_PRIVATE);
//                SharedPreferences.Editor ed = prefs.edit();
//                ed.putFloat("fontSize", heading.getTextSize());
//                ed.apply();
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                content.setTextSize(TypedValue.COMPLEX_UNIT_PX, progress);
//                heading.setTextSize(TypedValue.COMPLEX_UNIT_PX, progress + 4);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//});

        return rootView;
    }

    @Override
    public void onDestroy() {
        viewRoot.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        viewRoot.setVisibility(View.INVISIBLE);
        super.onDestroy();
//        if (increaseTextSize.getVisibility() == View.VISIBLE) {
//            increaseTextSize.setVisibility(View.INVISIBLE);
//            increaseTextSize.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.sink_down));
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    decreaseTextSize.setVisibility(View.INVISIBLE);
//                    decreaseTextSize.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.sink_down));
//                }
//            }, 200);
//        }
//        else {
//        }
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
            PossibleConditionDetails.this.pageBeansArrayList = pageBeansArrayList;
            progressBar.setVisibility(View.GONE);
            PageAdapter adapter;
            if (pageBeansArrayList != null) {
                adapter = new PageAdapter(getContext(), pageBeansArrayList, textSize);
                super.onPostExecute(pageBeansArrayList);
            } else {
                pageBeansArrayList = new ArrayList<>();
                PageBeans beans = new PageBeans();
                beans.setHeading("Coming soon...");
                beans.setContent("Info for this condition coming soon!\nBut Feel free to click here to directly search online for the condition");
                adapter = new PageAdapter(getContext(), pageBeansArrayList, textSize);
            }
            listView.setAdapter(adapter);

            viewRoot.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            viewRoot.setVisibility(View.VISIBLE);

//            heading = (AppCompatTextView) rootView.findViewById(R.id.heading);
//            content = (AppCompatTextView) rootView.findViewById(R.id.content);

//            content.setTextSize(TypedValue.COMPLEX_UNIT_PX, seekBar.getProgress());
//            heading.setTextSize(TypedValue.COMPLEX_UNIT_PX, seekBar.getProgress() + 4);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_possible_condition, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_text_size:
                increaseTextSize.setVisibility(View.VISIBLE);
                increaseTextSize.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_up));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        decreaseTextSize.setVisibility(View.VISIBLE);
                        decreaseTextSize.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.float_up));
                    }
                }, 200);
                increaseTextSize.setOnClickListener(this);
                decreaseTextSize.setOnClickListener(this);
                return false;
            case R.id.action_info:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(R.string.info)
                        .setMessage(R.string.disclaimer_conditions)
                        .show();
                return false;
            default:
                return false;
        }
    }

    private void alterTextSize(boolean increase) {
        if (increase) textSize += 2;
        else textSize -= 2;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new PageAdapter(getContext(), pageBeansArrayList, textSize));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.increase_text_size:
                alterTextSize(true);
                break;
            case R.id.decrease_text_size:
                alterTextSize(false);
                break;
            default:
                break;
        }
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
