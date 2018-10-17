package com.mobiapp4u.pc.routinebasket.Helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.mobiapp4u.pc.routinebasket.Interface.RecyclerItemTouchHelperListner;
import com.mobiapp4u.pc.routinebasket.ViewHolder.CartViewHolder;
import com.mobiapp4u.pc.routinebasket.ViewHolder.FavouratesViewHolder;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListner listner;


    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListner listner) {
        super(dragDirs, swipeDirs);
        this.listner = listner;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
      if(listner!=null){
          listner.onSwipe(viewHolder,direction,viewHolder.getAdapterPosition());
      }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof CartViewHolder) {
            View foreground_view = ((CartViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foreground_view);
        }else if(viewHolder instanceof FavouratesViewHolder){
            View foreground_view = ((FavouratesViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foreground_view);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof CartViewHolder) {
            View foreground_view = ((CartViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foreground_view, dX, dY, actionState, isCurrentlyActive);
        }else if(viewHolder instanceof FavouratesViewHolder){
            View foreground_view = ((FavouratesViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foreground_view, dX, dY, actionState, isCurrentlyActive);

        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder instanceof CartViewHolder) {
            View foreground_view = ((CartViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onSelected(foreground_view);
        }else if(viewHolder instanceof FavouratesViewHolder){
            View foreground_view = ((FavouratesViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onSelected(foreground_view);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
       if(viewHolder instanceof CartViewHolder) {
           View foreground_view = ((CartViewHolder) viewHolder).view_foreground;
           getDefaultUIUtil().onDrawOver(c, recyclerView, foreground_view, dX, dY, actionState, isCurrentlyActive);
       }else if(viewHolder instanceof FavouratesViewHolder){
           View foreground_view = ((FavouratesViewHolder) viewHolder).view_foreground;
           getDefaultUIUtil().onDrawOver(c, recyclerView, foreground_view, dX, dY, actionState, isCurrentlyActive);
       }
    }

}
