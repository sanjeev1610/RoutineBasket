package com.mobiapp4u.pc.routinebasket.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.mobiapp4u.pc.routinebasket.Cart;
import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Database.Database;
import com.mobiapp4u.pc.routinebasket.Model.Order;
import com.mobiapp4u.pc.routinebasket.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{
    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(""+listData.get(position).getQuantity(), Color.RED);
//        holder.img_cart_count.setImageDrawable(drawable);
        holder.txt_cart_name.setText(listData.get(position).getProductName());
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //update total price
                int total = 0;
                List<Order> orders = new Database(cart).getCart(Common.currentUser.getPhone());
                for(Order item:orders)
                    total+= (Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
               // Locale locale = new Locale("en","IN");
              //  NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

               // cart.txtTotalPrice.setText(fmt.format(total));
                 cart.txtTotalPrice.setText(String.valueOf(total));


            }
        });

        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
       // holder.txt_cart_price.setText(fmt.format(price));
        holder.txt_cart_price.setText(String.valueOf(price));
        Picasso.with(cart.getBaseContext()).load(listData.get(position).getImage()).into(holder.cart_image);


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeItem(int pos){
        listData.remove(pos);
        notifyItemRemoved(pos);
    }
    public void restoreItem(Order item, int pos){
        listData.add(pos,item);
        notifyItemInserted(pos);
    }
    public Order getItem(int position)
    {
        return listData.get(position);
    }
}
