package com.prembros.symptomator;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private SparseBooleanArray mCheckedItems = new SparseBooleanArray();
    private List<String> mValues;
    private CompleteSymptomList.OnFragmentInteractionListener mListener2;
    private Context context;
    private boolean isViewChecked;
    private int lastPosition = -1;

    MyRecyclerViewAdapter(boolean isViewChecked, Context context, List<String> items,
                          @Nullable CompleteSymptomList.OnFragmentInteractionListener listener2) {
        this.isViewChecked = isViewChecked;
        mValues = items;
        if (listener2 != null)
            mListener2 = listener2;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (isViewChecked){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_list_item_check, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_list_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String valueForPosition = mValues.get(position);
        holder.mItem = mValues.get(position);
        if (isViewChecked) {
            holder.mIdCheckedTextView.setText(valueForPosition);
//            if (selectedSymptomList != null) {
//                if (selectedSymptomList.contains(valueForPosition)) {
//                    holder.mIdCheckedTextView.setChecked(true);
//                } else {
//                    holder.mIdCheckedTextView.setChecked(false);
//                }
//            }
            holder.mIdCheckedTextView.setChecked(mCheckedItems.get(position));
            holder.mIdCheckedTextView.setSelected(mCheckedItems.get(position));
            if (holder.mIdCheckedTextView.isChecked()) {
                holder.mIdCheckedTextView.setTextColor(Color.parseColor("#FFFFFF"));
            } else holder.mIdCheckedTextView.setTextColor(Color.parseColor("#000000"));
            setAnimation(holder.mIdCheckedTextView, position);
        } else {
            holder.mIdTextView.setText(valueForPosition);
            setAnimation(holder.mIdTextView, position);
        }
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View mView;
        TextView mIdTextView;
        CheckedTextView mIdCheckedTextView;
        String mItem;

        ViewHolder(View view) {
            super(view);
            mIdTextView = null;
            mIdCheckedTextView = null;
            mView = view;
            if (isViewChecked) {
                mIdCheckedTextView = (CheckedTextView) view.findViewById(R.id.list_item_checked_textview);
                mIdCheckedTextView.setOnClickListener(this);
            } else {
                mIdTextView = (TextView) view.findViewById(R.id.recycler_view_list_item);
                mIdTextView.setOnClickListener(this);
            }
//            this.setIsRecyclable(false);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.list_item_checked_textview:
                    int position = this.getAdapterPosition();
                    mCheckedItems.put(position, !mIdCheckedTextView.isChecked());
                    ((SymptomCheck)context).onRecyclerViewItemClick(mIdCheckedTextView, position + 1, getItemCount());
                case R.id.recycler_view_list_item:
//                    if (mListener != null) {
//                        // Notify the active callbacks interface (the activity, if the
//                        // fragment is attached to one) that an item has been selected.
//                        mListener.onListFragmentInteraction(true, mItem);
//                    }
                    if (mListener2 != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener2.onFragmentInteraction(mItem);
                    }
                    break;
            }
        }
        @Override
        public String toString() {
            if (isViewChecked){
                return super.toString() + " '" + mIdCheckedTextView.getText() + "'";
            } else {
                return super.toString() + " '" + mIdTextView.getText() + "'";
            }
        }
}
}
