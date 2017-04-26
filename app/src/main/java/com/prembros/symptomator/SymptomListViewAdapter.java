package com.prembros.symptomator;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * Created by Prem $ on 4/26/2017.
 */

class SymptomListViewAdapter extends BaseAdapter {
    private List<String> symptoms;
    Context context;
    private LayoutInflater inflter;
//    private String value;

    SymptomListViewAdapter(Context context, List<String> symptoms) {
        this.context = context;
        this.symptoms = symptoms;
        inflter = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return symptoms.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflter.inflate(R.layout.recycler_view_list_item_check, parent, false);
            final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.list_item_checked_textview);
            simpleCheckedTextView.setText(symptoms.get(position));

            simpleCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatabaseHolder db = new DatabaseHolder(context);
                    final String viewText = simpleCheckedTextView.getText().toString();
                    if (simpleCheckedTextView.isChecked()) {
//                        value = "un-Checked";
//                        simpleCheckedTextView.setCheckMarkDrawable(0);
                        simpleCheckedTextView.setChecked(false);
                        simpleCheckedTextView.setTextColor(Color.parseColor("#000000"));
                        simpleCheckedTextView.setBackgroundResource(R.color.colorDisabled);
                        simpleCheckedTextView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_deselected));

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
//                        simpleCheckedTextView.setCheckMarkDrawable(R.drawable.checked);
                        simpleCheckedTextView.setChecked(true);
                        simpleCheckedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                        simpleCheckedTextView.setBackgroundResource(R.color.colorSecondaryText);
                        simpleCheckedTextView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_selected));

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
        }
        return view;
    }
}
