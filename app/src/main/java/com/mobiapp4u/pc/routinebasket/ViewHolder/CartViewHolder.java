package com.mobiapp4u.pc.routinebasket.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    public TextView txt_cart_name, txt_cart_price;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_image;
    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    private ItemClickListner itemClickListner;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.item_name);
        txt_cart_price = (TextView)itemView.findViewById(R.id.item_price);
        btn_quantity = (ElegantNumberButton)itemView.findViewById(R.id.btn_quantity);
        cart_image = (ImageView)itemView.findViewById(R.id.cart_image);
        view_background = (RelativeLayout)itemView.findViewById(R.id.view_background_cart);
        view_foreground = (LinearLayout)itemView.findViewById(R.id.view_foreground_cart);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Set Action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}
