package com.prembros.symptomator;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.codetail.animation.SupportAnimator;

public class FirstAidFragment extends Fragment implements RecyclerView.OnItemTouchListener{

    private OnFirstAidListFragmentInteractionListener mListener;
    private MyRecyclerViewAdapter myFirstAidRecyclerViewAdapter;
    private boolean hidden = true;
    SupportAnimator animator;
    private RecyclerView list;
    private Context context;
    private List<String> firstAidList;

    public FirstAidFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        setHasOptionsMenu(true);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_first_aid, container, false);

//        SET ACTION BAR
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.first_aid_info);
            actionBar.setSubtitle(getString(R.string.first_aid_subtitle));
        }

        list = (RecyclerView) view.findViewById(R.id.first_aid_list);
        // Set the list
        firstAidList = Arrays.asList(getResources().getStringArray(R.array.first_aid));
//        firstAidList.add("Find more tips online");
        // Set the adapter
        context = list.getContext();
        myFirstAidRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), firstAidList, mListener, null);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setAdapter(myFirstAidRecyclerViewAdapter);

        return view;
    }

//    public void animationForward(View v, FrameLayout mRevealView, AppCompatImageButton searchButton,
//                                 TextView header, AutoCompleteTextView searchBar){
////        float pixelDensity = getResources().getDisplayMetrics().density;
//        int centerX = (v.getLeft() + v.getRight()) / 2;
//        int centerY = (v.getTop() + v.getBottom()) / 2;
//        int startRadius = 0;
//        int endRadius = (int) Math.hypot(mRevealView.getWidth(), mRevealView.getHeight());
//        animator = ViewAnimationUtils.createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.setDuration(400);
//        if (hidden){
//            animator.start();
//            mRevealView.setVisibility(View.VISIBLE);
//            searchButton.setVisibility(View.INVISIBLE);
//            header.setVisibility(View.INVISIBLE);
//            if(searchBar.requestFocus()) {
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
//            }
//            hidden = false;
//        }
//    }
//
//    public void animationReversed(final FrameLayout mRevealView, final AppCompatImageButton searchButton,
//                                  final TextView header, final AutoCompleteTextView searchBar){
//        if (animator != null && !animator.isRunning()){
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
//            animator = animator.reverse();
//            animator.addListener(new SupportAnimator.AnimatorListener() {
//                @Override
//                public void onAnimationStart() {
//
//                }
//
//                @Override
//                public void onAnimationEnd() {
//                    searchBar.setText("");
//                    searchBar.clearFocus();
//                    mRevealView.setVisibility(View.GONE);
//                    searchButton.setVisibility(View.VISIBLE);
//                    header.setVisibility(View.VISIBLE);
//                    hidden = true;
//                }
//
//                @Override
//                public void onAnimationCancel() {
//
//                }
//
//                @Override
//                public void onAnimationRepeat() {
//
//                }
//            });
//            animator.start();
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_first_aid, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setActionView(new SearchView(getContext()));
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<String> filteredList = new ArrayList<>();

                for (int x = 0; x < firstAidList.size(); x++) {
                    final String text = firstAidList.get(x).toLowerCase();
                    if (text.contains(newText))
                        filteredList.add(firstAidList.get(x));
                }
                filteredList.add("Didn't find what you were looking for?\nClick here to search online");
//                if (!newText.equals("")) {
//                }

                list.setLayoutManager(new LinearLayoutManager(context));
                myFirstAidRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), filteredList, mListener, null);
                list.setAdapter(myFirstAidRecyclerViewAdapter);
                myFirstAidRecyclerViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
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
