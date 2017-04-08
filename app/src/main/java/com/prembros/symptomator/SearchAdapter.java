package com.prembros.symptomator;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

/*
 * Created by Prem $ on 4/8/2017.
 */

public class SearchAdapter extends CursorAdapter{

    private List<String> itemList;
    private TextView item;

    public SearchAdapter(Context context, Cursor cursor, List<String> items) {
        super(context, cursor, false);
        itemList = items;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        item.setText(itemList.get(cursor.getPosition()));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.recycler_view_list_item, parent, false);
        item = (TextView) rootView.findViewById(R.id.Recycler_view_list_item);
        return rootView;
    }
}
