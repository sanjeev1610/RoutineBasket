package com.mobiapp4u.pc.routinebasket.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtFoodName,txtFoodPrice;
    public ImageView imageView,imageV_fav,quick_cart,btn_share;

    private ItemClickListner itemClickListner;

    public FoodViewHolder(View itemView) {
        super(itemView);

        txtFoodName = (TextView)itemView.findViewById(R.id.menu_name_food);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image_food);
        imageV_fav = (ImageView)itemView.findViewById(R.id.menu_fav_food);
        txtFoodPrice = (TextView)itemView.findViewById(R.id.food_price);
        quick_cart = (ImageView)itemView.findViewById(R.id.menu_add_cart);
        btn_share =  (ImageView)itemView.findViewById(R.id.menu_share_food);
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
