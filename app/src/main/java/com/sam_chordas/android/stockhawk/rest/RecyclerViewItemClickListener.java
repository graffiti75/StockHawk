package com.sam_chordas.android.stockhawk.rest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sam_chordas on 11/9/15.
 */
public class RecyclerViewItemClickListener implements RecyclerView.OnItemTouchListener {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    //--------------------------------------------------
    // Interface
    //--------------------------------------------------

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public RecyclerViewItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    //--------------------------------------------------
    // RecyclerView.OnItemTouchListener
    //--------------------------------------------------

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    @SuppressWarnings("deprecation")
    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {}
}