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

class PageAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private ArrayList<PageBeans> pageBeansArrayList;

    PageAdapter(Context context, ArrayList<PageBeans> pageBeansArrayList){
        this.context = context;
        this.pageBeansArrayList = pageBeansArrayList;
    }

    @Override
    public int getCount() {
        return pageBeansArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return pageBeansArrayList.get(position);
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
            view = inflater.inflate(R.layout.page_list_item, null);
        }

        headingTextView = (AppCompatTextView) view.findViewById(R.id.heading);
        if (headingTextView != null) {
            headingTextView.setText(pageBeansArrayList.get(position).getHeading());
        }
        contentTextView = (AppCompatTextView) view.findViewById(R.id.content);
        if (contentTextView != null){
            contentTextView.setText(pageBeansArrayList.get(position).getContent());
        }
        return view;
    }
}
