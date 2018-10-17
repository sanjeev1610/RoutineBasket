package com.mobiapp4u.pc.routinebasket.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.R;

public class ReataurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtRestaurantName;
    public ImageView imageViewRestaurant;

    private ItemClickListner itemClickListner;

    public ReataurantViewHolder(View itemView) {
        super(itemView);

        txtRestaurantName = (TextView)itemView.findViewById(R.id.restaurant_name);
        imageViewRestaurant = (ImageView)itemView.findViewById(R.id.restaurant_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner){
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }
}
