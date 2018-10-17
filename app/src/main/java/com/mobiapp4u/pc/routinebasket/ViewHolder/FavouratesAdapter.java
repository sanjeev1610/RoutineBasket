package com.mobiapp4u.pc.routinebasket.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Database.Database;
import com.mobiapp4u.pc.routinebasket.FoodDetail;
import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.Model.Favourates;
import com.mobiapp4u.pc.routinebasket.Model.Order;
import com.mobiapp4u.pc.routinebasket.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouratesAdapter extends RecyclerView.Adapter<FavouratesViewHolder>{

    private List<Favourates> favouratesList;
    private Context context;

    public FavouratesAdapter(List<Favourates> favouratesList, Context context) {
        this.favouratesList = favouratesList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavouratesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favourates_item,parent,false);
        return new FavouratesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouratesViewHolder viewHolder, final int position) {  //
        viewHolder.txtFoodName.setText(favouratesList.get(position).getFoodName());
        Picasso.with(context).load(favouratesList.get(position).getFoodImage()).into(viewHolder.imageView);
        viewHolder.txtFoodPrice.setText(String.format("$ %s",favouratesList.get(position).getFoodPrice().toString()));

        viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isExists = new Database(context).checkFoodExists(favouratesList.get(position).getFoodId(), Common.currentUser.getPhone()) ;
                if(!isExists) {
                    new Database(context).addToCart(new Order(Common.currentUser.getPhone(),
                            favouratesList.get(position).getFoodId(),
                            favouratesList.get(position).getFoodName(),
                            "1",
                            favouratesList.get(position).getFoodPrice(),
                            favouratesList.get(position).getFoodDiscount(),
                            favouratesList.get(position).getFoodImage()));
                    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                }else{
                    new Database(context).increaseCart(Common.currentUser.getPhone(),favouratesList.get(position).getFoodId());
                }
            }
        });
        final Favourates local = favouratesList.get(position);

        viewHolder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position, Boolean isLongClick) {
                Intent foodDetailIntent = new Intent(context, FoodDetail.class);
                foodDetailIntent.putExtra("foodId", favouratesList.get(position).getFoodId());
                Toast.makeText(context, ""+favouratesList.get(position).getFoodName(), Toast.LENGTH_LONG).show();
                context.startActivity(foodDetailIntent);
            }
        });


    }//

    @Override
    public int getItemCount() {
        return favouratesList.size();
    }
    public void removeItem(int pos){
        favouratesList.remove(pos);
        notifyItemRemoved(pos);
    }
    public void restoreItem(Favourates item,int pos){
        favouratesList.add(pos,item);
        notifyItemInserted(pos);
    }
    public  Favourates getItem(int pos){
        return favouratesList.get(pos);
    }
}
