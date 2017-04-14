package com.prembros.symptomator;

/*
 * Created by Prem $ on 4/14/2017.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

class FirstAidDetailsAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private ArrayList<Beans> beansArrayList;

    FirstAidDetailsAdapter(Context context, ArrayList<Beans> beansArrayList){
        this.context = context;
        this.beansArrayList = beansArrayList;
    }

    @Override
    public int getCount() {
        return beansArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return beansArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        AppCompatTextView headingTextView;
        AppCompatTextView contentTextView;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.first_aid_check_list_item, null);
        }

        headingTextView = (AppCompatTextView) view.findViewById(R.id.heading);
        if (headingTextView != null) {
            headingTextView.setText(beansArrayList.get(position).getHeading());
        }
        contentTextView = (AppCompatTextView) view.findViewById(R.id.content);
        if (contentTextView != null){
            contentTextView.setText(beansArrayList.get(position).getContent());
        }
        return view;
    }
}
