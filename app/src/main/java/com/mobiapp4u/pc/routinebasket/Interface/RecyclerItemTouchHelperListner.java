package com.mobiapp4u.pc.routinebasket.Interface;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelperListner {
    void onSwipe(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
