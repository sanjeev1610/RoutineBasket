package com.mobiapp4u.pc.routinebasket;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.Model.Category;
import com.mobiapp4u.pc.routinebasket.Model.Restaurant;
import com.mobiapp4u.pc.routinebasket.ViewHolder.MenuViewHolder;
import com.mobiapp4u.pc.routinebasket.ViewHolder.ReataurantViewHolder;
import com.squareup.picasso.Picasso;

public class RestaurantList extends AppCompatActivity {

    AlertDialog waitingDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Restaurant,ReataurantViewHolder>  firebaseRecyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);








        //swipe refresh
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.restaurant_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Restaurant, ReataurantViewHolder>(Restaurant.class, R.layout.restaurant_item, ReataurantViewHolder.class, FirebaseDatabase.getInstance().getReference().child("Restaurants")) {
                        @Override
                        protected void populateViewHolder(final ReataurantViewHolder viewHolder, Restaurant model, int position) {
                            viewHolder.txtRestaurantName.setText(model.getName());
                            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageViewRestaurant);
                            Toast.makeText(RestaurantList.this, "Enter Load Restaurant--1", Toast.LENGTH_LONG).show();

                            final Restaurant clickItem = model;

                            viewHolder.setItemClickListner(new ItemClickListner() {
                                @Override
                                public void onClick(View view, int position, Boolean isLongClick) {
                                    Intent foodListIntent = new Intent(RestaurantList.this, Home.class);
                                    Common.restaurantSelected = firebaseRecyclerAdapter.getRef(position).getKey();
                                    startActivity(foodListIntent);
                                    Toast.makeText(RestaurantList.this, ""+clickItem.getName(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    };


                    recyclerView = (RecyclerView)findViewById(R.id.recyclerView_restaurantList);
                    layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);

                    recyclerView.setAdapter(firebaseRecyclerAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else{
                    Toast.makeText(RestaurantList.this,"Please Check your Internet Connection!!!",Toast.LENGTH_LONG).show();

                }
            }
        });

        //default first item load
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Restaurant, ReataurantViewHolder>(Restaurant.class, R.layout.restaurant_item, ReataurantViewHolder.class, FirebaseDatabase.getInstance().getReference().child("Restaurants")) {
                        @Override
                        protected void populateViewHolder(final ReataurantViewHolder viewHolder, Restaurant model, int position) {
                            viewHolder.txtRestaurantName.setText(model.getName());
                            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageViewRestaurant);
                            Toast.makeText(RestaurantList.this, "Enter Load Restaurant--1", Toast.LENGTH_LONG).show();

                            final Restaurant clickItem = model;

                            viewHolder.setItemClickListner(new ItemClickListner() {
                                @Override
                                public void onClick(View view, int position, Boolean isLongClick) {
                                    Intent foodListIntent = new Intent(RestaurantList.this, Home.class);
                                    Common.restaurantSelected = firebaseRecyclerAdapter.getRef(position).getKey();
                                    startActivity(foodListIntent);
                                    Toast.makeText(RestaurantList.this, ""+clickItem.getName(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    };


                    recyclerView = (RecyclerView)findViewById(R.id.recyclerView_restaurantList);
                    layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);

                    recyclerView.setAdapter(firebaseRecyclerAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else{
                    Toast.makeText(RestaurantList.this,"Please Check your Internet Connection!!!",Toast.LENGTH_LONG).show();

                }
            }
        });





    }
//    private void loadRestaurant(){
//        Toast.makeText(RestaurantList.this, ""+firebaseRecyclerAdapter, Toast.LENGTH_LONG).show();
//
//        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_restaurantList);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//
//        recyclerView.setAdapter(firebaseRecyclerAdapter);
//        swipeRefreshLayout.setRefreshing(false);
//        recyclerView.getAdapter().notifyDataSetChanged();
//        Toast.makeText(RestaurantList.this, "Enter Load Restaurant", Toast.LENGTH_LONG).show();
//
//    }
    @Override
    protected void onStop() {
        super.onStop();

    }

}
