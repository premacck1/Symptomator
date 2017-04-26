package com.prembros.symptomator;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.prembros.symptomator.FirstAidFragment.OnFirstAidListFragmentInteractionListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.sql.SQLException;
import java.util.List;

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private DatabaseHolder db;
    private List<String> mValues;
    private List<String> selectedSymptomList;
    private OnFirstAidListFragmentInteractionListener mListener;
    private CompleteSymptomList.OnFragmentInteractionListener mListener2;
    private Context context;
    private boolean isViewChecked;
    private int lastPosition = -1;

    MyRecyclerViewAdapter(boolean isViewChecked, Context context, List<String> items,
                          @Nullable FirstAidFragment.OnFirstAidListFragmentInteractionListener listener,
                          @Nullable CompleteSymptomList.OnFragmentInteractionListener listener2,
                          @Nullable List<String> selectedSymptomList) {
        db = new DatabaseHolder(context);
        this.isViewChecked = isViewChecked;
        mValues = items;
        if (listener != null)
            mListener = listener;
        if (listener2 != null)
            mListener2 = listener2;
        if (selectedSymptomList != null)
            this.selectedSymptomList = selectedSymptomList;
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
            if (selectedSymptomList != null) {
                if (selectedSymptomList.contains(valueForPosition)) {
                    holder.mIdCheckedTextView.setChecked(true);
                } else {
                    holder.mIdCheckedTextView.setChecked(false);
                }
            }
            setAnimation(holder.mIdCheckedTextView, position);

            holder.mIdCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String viewText = holder.mIdCheckedTextView.getText().toString();
                    if (holder.mIdCheckedTextView.isChecked()) {
//                        value = "un-Checked";
//                        holder.mIdCheckedTextView.setCheckMarkDrawable(0);
                        holder.mIdCheckedTextView.setChecked(false);
                        holder.mIdCheckedTextView.setTextColor(Color.parseColor("#000000"));
                        holder.mIdCheckedTextView.setBackgroundResource(R.color.colorDisabled);
                        holder.mIdCheckedTextView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_deselected));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db.open();
                                db.removeFromSelectedSymptomsTable(viewText);
                                db.close();
                            }
                        }).start();
                    } else {
//                        value = "Checked";
//                        holder.mIdCheckedTextView.setCheckMarkDrawable(R.drawable.checked);
                        holder.mIdCheckedTextView.setChecked(true);
                        holder.mIdCheckedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                        holder.mIdCheckedTextView.setBackgroundResource(R.color.colorSecondaryText);
                        holder.mIdCheckedTextView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_selected));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    db.open();
                                    db.insertInSelectedSymptomsTable(viewText);
                                    db.close();
                                } catch (SQLException e) {
                                    Log.d("SQLException ERROR!", e.getMessage());
                                }
                            }
                        }).start();
                    }
//                    Toast.makeText(context, value + position, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.mIdTextView.setText(valueForPosition);
            setAnimation(holder.mIdTextView, position);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(true, holder.mItem);
                    }
                    if (mListener2 != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener2.onFragmentInteraction(holder.mItem);
                    }
                }
            });
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

    class ViewHolder extends RecyclerView.ViewHolder {
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
            } else {
                mIdTextView = (TextView) view.findViewById(R.id.recycler_view_list_item);
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
