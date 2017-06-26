package com.prembros.symptomator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/*
 * Created by Prem $ on 4/6/2017.
 */

class RecyclerViewOnItemClickListener implements RecyclerView.OnItemTouchListener {

    private final RecyclerViewOnItemClickListener.OnItemClickListener mListener;
    private final GestureDetector gestureDetector;

    RecyclerViewOnItemClickListener(Context context, RecyclerViewOnItemClickListener.OnItemClickListener listener){
        mListener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && gestureDetector.onTouchEvent(e)){
            mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
