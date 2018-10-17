package com.mobiapp4u.pc.routinebasket.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.R;

public class FavouratesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
public TextView txtFoodName,txtFoodPrice;
public ImageView imageView,quick_cart;
public RelativeLayout view_background;
public LinearLayout view_foreground;

private ItemClickListner itemClickListner;

public FavouratesViewHolder(View itemView) {
        super(itemView);
        txtFoodName = (TextView)itemView.findViewById(R.id.menu_name_food_fav);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image_fav);
        txtFoodPrice = (TextView)itemView.findViewById(R.id.food_price_fav);
        quick_cart = (ImageView)itemView.findViewById(R.id.menu_add_cart_fav);
        view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground = (LinearLayout)itemView.findViewById(R.id.view_foreground);

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

