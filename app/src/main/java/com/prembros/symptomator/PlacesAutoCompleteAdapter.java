package com.prembros.symptomator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
 * Created by Prem $ on 4/22/2017.
 */

class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;
    private Context context;
    private PlaceAPI mPlaceAPI = new PlaceAPI();

    PlacesAutoCompleteAdapter(Context context, int layoutRes) {
        super(context, layoutRes);
        this.context = context;
    }

    @Override
    public int getCount() {
        // Last item will be the footer
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    resultList = mPlaceAPI.autocomplete(constraint.toString());

                    // Footer
                    resultList.add("footer");

                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;

        //if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position == (resultList.size() - 1)) {
            view = inflater.inflate(R.layout.autocomplete_google_logo, null);
        }
        else {
            view = inflater.inflate(R.layout.list_item, null);
        }
        //}
        //else {
        //    view = convertView;
        //}

        if (position != (resultList.size() - 1)) {
            TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
            autocompleteTextView.setText(resultList.get(position));
        }
//        else {
//            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            // not sure what to do <img draggable="false" class="emoji" alt="😀" src="https://s.w.org/images/core/emoji/72x72/1f600.png">
//        }

        return view;
    }
}