package com.prembros.symptomator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.prembros.symptomator.FirstAidFragment.OnFirstAidListFragmentInteractionListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private List<String> mValues;
    private OnFirstAidListFragmentInteractionListener mListener;
    private CompleteSymptomList.OnFragmentInteractionListener mListener2;
    private Context context;
    private int lastPosition = -1;

    MyRecyclerViewAdapter(Context context, List<String> items,
                          FirstAidFragment.OnFirstAidListFragmentInteractionListener listener,
                          CompleteSymptomList.OnFragmentInteractionListener listener2) {
        mValues = items;
        if (listener != null)
            mListener = listener;
        if (listener2 != null)
            mListener2 = listener2;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position));

        setAnimation(holder.mIdView, position);
//        holder.mContentView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
                if (mListener2 != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener2.onFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(mValues.get(position).charAt(0));
    }

//    public void updateList(List<String> list){
//        mValues = list;
//        notifyDataSetChanged();
//    }

    private void setAnimation(View viewToAnimate, int position){
        if (position > lastPosition){
            viewToAnimate.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            lastPosition = position;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
//        final TextView mContentView;
        String mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.Recycler_view_list_item);
//            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}
