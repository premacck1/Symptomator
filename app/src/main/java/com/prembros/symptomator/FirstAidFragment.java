package com.prembros.symptomator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFirstAidListFragmentInteractionListener}
 * interface.
 */
public class FirstAidFragment extends Fragment implements RecyclerView.OnItemTouchListener{

    private OnFirstAidListFragmentInteractionListener mListener;
    private MyRecyclerViewAdapter myFirstAidRecyclerViewAdapter;
    private boolean hidden = true;
    SupportAnimator animator;

    public FirstAidFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_first_aid, container, false);
        final RecyclerView list = (RecyclerView) view.findViewById(R.id.first_aid_list);
        final AppCompatImageButton searchButton = (AppCompatImageButton) view.findViewById(R.id.search_button);
        final AutoCompleteTextView searchBar = (AutoCompleteTextView) view.findViewById(R.id.first_aid_search_bar);
        final FrameLayout mRevealView = (FrameLayout) view.findViewById(R.id.reveal_view);
        final AppCompatImageButton closeButton = (AppCompatImageButton) view.findViewById(R.id.reveal_view_close_button);
        final TextView header = (TextView) view.findViewById(R.id.first_aid_header);

        // Set the list
        final List<String> firstAidList = Arrays.asList(getResources().getStringArray(R.array.first_aid));
        // Set the adapter
        final Context context = list.getContext();
        myFirstAidRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), firstAidList, mListener, null);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setAdapter(myFirstAidRecyclerViewAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationForward(v, mRevealView, searchButton, header, searchBar);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationReversed(mRevealView, searchButton, header, searchBar);
            }
        });

        //Set the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charSequence = charSequence.toString().toLowerCase();
                final List<String> filteredList = new ArrayList<>();

                for (int x = 0; x < firstAidList.size(); x++){
                    final String text = firstAidList.get(x).toLowerCase();
                    if (text.contains(charSequence))
                        filteredList.add(firstAidList.get(x));
                }

                list.setLayoutManager(new LinearLayoutManager(context));
                myFirstAidRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), filteredList, mListener, null);
                list.setAdapter(myFirstAidRecyclerViewAdapter);
                myFirstAidRecyclerViewAdapter.notifyDataSetChanged();
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        return view;
    }

    public void animationForward(View v, FrameLayout mRevealView, AppCompatImageButton searchButton,
                                 TextView header, AutoCompleteTextView searchBar){
//        float pixelDensity = getResources().getDisplayMetrics().density;
        int centerX = (v.getLeft() + v.getRight()) / 2;
        int centerY = (v.getTop() + v.getBottom()) / 2;
        int startRadius = 0;
        int endRadius = (int) Math.hypot(mRevealView.getWidth(), mRevealView.getHeight());
        animator = ViewAnimationUtils.createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        if (hidden){
            animator.start();
            mRevealView.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.INVISIBLE);
            header.setVisibility(View.INVISIBLE);
            if(searchBar.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
            }
            hidden = false;
        }
    }

    public void animationReversed(final FrameLayout mRevealView, final AppCompatImageButton searchButton,
                                  final TextView header, final AutoCompleteTextView searchBar){
        if (animator != null && !animator.isRunning()){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            animator = animator.reverse();
            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    searchBar.setText("");
                    searchBar.clearFocus();
                    mRevealView.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    hidden = true;
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            animator.start();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFirstAidListFragmentInteractionListener) {
            mListener = (OnFirstAidListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFirstAidListFragmentInteractionListener");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    interface OnFirstAidListFragmentInteractionListener {
        void onListFragmentInteraction(String item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
