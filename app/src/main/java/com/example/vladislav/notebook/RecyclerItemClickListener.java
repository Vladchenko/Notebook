package com.example.vladislav.notebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by vladislav on 06.02.17.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private Context context;
    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        this.context = context;
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
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {

//        //creating a popup menu
//        PopupMenu popup = new PopupMenu(context, view);
//        //inflating menu from xml resource
//        popup.inflate(R.menu.options_menu);
//        //adding click listener
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.menu1:
//                        //handle menu1 click
//                        break;
//                    case R.id.menu2:
//                        //handle menu2 click
//                        break;
//                    case R.id.menu3:
//                        //handle menu3 click
//                        break;
//                }
//                return false;
//            }
//        });
//        //displaying the popup
//        popup.show();

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
