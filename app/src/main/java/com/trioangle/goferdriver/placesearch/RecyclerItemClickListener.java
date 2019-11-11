package com.trioangle.goferdriver.placesearch;

/**
 * @package com.trioangle.gofer
 * @subpackage placesearch
 * @category Recycler view Item click listener
 * @author Trioangle Product Team
 * @version 1.1
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.trioangle.goferdriver.util.CommonMethods;
/* ************************************************************
Place search list click listener
*************************************************************** */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        CommonMethods.DebuggableLogI("MotionEvent", "motionEvent");
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        CommonMethods.DebuggableLogI("disallowIntercept", "disallowIntercept");

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}