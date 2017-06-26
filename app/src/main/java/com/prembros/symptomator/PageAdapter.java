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

class PageAdapter extends BaseAdapter implements ListAdapter, View.OnClickListener {

    private final int VIEW_TYPE_NORMAL = 0;
    private final int VIEW_TYPE_EMERGENCY = 1;
    private final Context context;
    private final ArrayList<PageBeans> pageBeansArrayList;
    private int textSize;

    PageAdapter(Context context, ArrayList<PageBeans> pageBeansArrayList, int textSize){
        this.context = context;
        this.pageBeansArrayList = pageBeansArrayList;
        this.textSize = textSize;
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

    @Override
    public int getItemViewType(int position) {
        return pageBeansArrayList.get(position).getHeading().equals("EMERGENCY")? VIEW_TYPE_EMERGENCY : VIEW_TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        AppCompatTextView headingTextView;
        AppCompatTextView contentTextView;
        LayoutInflater inflater;
        if (view == null){
            switch (getItemViewType(position)) {
                case VIEW_TYPE_NORMAL:
                    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.page_list_item, null);
                    break;
                case VIEW_TYPE_EMERGENCY:
                    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.page_list_item_emergency, null);
                    view.findViewById(R.id.call_108).setOnClickListener(this);
                    break;
                default:
                    break;
            }
        }

        if (view != null) {
            headingTextView = (AppCompatTextView) view.findViewById(R.id.heading);
            if (headingTextView != null) {
                headingTextView.setText(pageBeansArrayList.get(position).getHeading());
                headingTextView.setTextSize(textSize + 4);
            }
            contentTextView = (AppCompatTextView) view.findViewById(R.id.content);
            if (contentTextView != null) {
                contentTextView.setText(pageBeansArrayList.get(position).getContent());
                contentTextView.setTextSize(textSize);
            }
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.call_108) {
            new CallEmergencyServices(context);
        }
    }
}
