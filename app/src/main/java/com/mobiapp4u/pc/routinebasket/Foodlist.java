package com.mobiapp4u.pc.routinebasket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Database.Database;
import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.Model.Favourates;
import com.mobiapp4u.pc.routinebasket.Model.Food;
import com.mobiapp4u.pc.routinebasket.Model.Order;
import com.mobiapp4u.pc.routinebasket.ViewHolder.FoodViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Foodlist extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference food;

    RecyclerView recyclerView_food;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;

    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    Database localDB;

    SwipeRefreshLayout swipeRefreshLayout;

    //FacebookInit
    CallbackManager callbackManager;
    ShareDialog shareDialog;
   //load image from picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
               //Create photo from bitmap
            SharePhoto photo = new SharePhoto.Builder()
                                   .setBitmap(bitmap)
                                   .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo)
                                    .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);
       //init facebook

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/r_f.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        firebaseDatabase = FirebaseDatabase.getInstance();
        food = firebaseDatabase.getReference("Restaurants").child(Common.restaurantSelected).child("details").child("Foood");

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.foodlist_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("categoryId");
                if(!categoryId.isEmpty() && categoryId != null) {
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        loadFoodList(categoryId);
                    }else{
                        Toast.makeText(Foodlist.this, "Please check your Internet Connection!!!", Toast.LENGTH_LONG).show();

                    }
                }

            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("categoryId");
                if(!categoryId.isEmpty() && categoryId != null) {
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        loadFoodList(categoryId);
                    }else{
                        Toast.makeText(Foodlist.this, "Please check your Internet Connection!!!", Toast.LENGTH_LONG).show();

                    }
                }
                materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter Your Food");
                // materialSearchBar.setSpeechMode(false);

                loadSuggest();//load suggesionlist from firebase

                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> suggesion = new ArrayList<>();
                        for(String search:suggestList)
                        {
                            if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggesion.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggesion);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if(!enabled){
                            recyclerView_food.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {

                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {
                        super.onButtonClicked(buttonCode);
                    }
                });
            }
        });

        recyclerView_food = (RecyclerView)findViewById(R.id.recycler_food_list);
        recyclerView_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_food.setLayoutManager(layoutManager);

        //database
        localDB = new Database(this);

       if(getIntent() != null)
           categoryId = getIntent().getStringExtra("categoryId");
       if(!categoryId.isEmpty() && categoryId != null) {
           if(Common.isConnectedToInternet(getBaseContext())) {
               loadFoodList(categoryId);
           }else{
               Toast.makeText(Foodlist.this, "Please check your Internet Connection!!!", Toast.LENGTH_LONG).show();

           }
       }



    }

    private void  startSearch(CharSequence text){
System.out.println(text);
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_list, FoodViewHolder.class, food.orderByChild("name1").equalTo(text.toString())) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.txtFoodName.setText(model.getName1());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                viewHolder.txtFoodPrice.setText(model.getPrice());
                final Food foodItem = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent foodDetailIntent = new Intent(Foodlist.this, FoodDetail.class);
                        foodDetailIntent.putExtra("foodId", searchAdapter.getRef(position).getKey());
                        Toast.makeText(Foodlist.this, ""+foodItem.getName1(), Toast.LENGTH_LONG).show();
                        startActivity(foodDetailIntent);
                    }
                });
            }
        };

        recyclerView_food.setAdapter(searchAdapter);

    }
    private void loadSuggest(){
        food.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName1());
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodList(String categoryid){
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class, R.layout.food_list, FoodViewHolder.class,
                food.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.txtFoodName.setText(model.getName1());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                viewHolder.txtFoodPrice.setText(String.format("%s",model.getPrice().toString()));

                viewHolder.btn_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("HELLO","btnShare");
                      //  Picasso.with(getBaseContext()).load(model.getImage()).into(target);
                        try {
                            URL url = new URL(model.getImage());
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            SharePhoto photo = new SharePhoto.Builder()
                                    .setBitmap(image)
                                    .build();
                          //  if(ShareDialog.canShow(SharePhotoContent.class)){
                                SharePhotoContent content = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();
                                shareDialog.show(content);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });

                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                        if(!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName1(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()));
                            Toast.makeText(Foodlist.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        }else{
                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey());
                        }
                    }
                });
                final Food foodItem = model;

                if(localDB.isFavourate(adapter.getRef(position).getKey(),Common.currentUser.getPhone())){
                    viewHolder.imageV_fav.setImageResource(R.drawable.ic_favorite_black_24dp);}

                viewHolder.imageV_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourates favourates = new Favourates();
                        favourates.setFoodId(adapter.getRef(position).getKey());
                        favourates.setUserPhone(Common.currentUser.getPhone());
                        favourates.setFoodName(model.getName1());
                        favourates.setFoodPrice(model.getPrice());
                        favourates.setFoodImage(model.getImage());
                        favourates.setFoodDescription(model.getDescription());
                        favourates.setFoodDiscount(model.getDiscount());

                        if(!localDB.isFavourate(adapter.getRef(position).getKey(),Common.currentUser.getPhone())){
                            localDB.addToFav(favourates);
                            viewHolder.imageV_fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Foodlist.this,""+model.getName1()+"was added to favourates",Toast.LENGTH_LONG).show();
                        }else {
                            localDB.removeFromFav(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.imageV_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Foodlist.this,""+model.getName1()+"was removed from favourates",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent foodDetailIntent = new Intent(Foodlist.this, FoodDetail.class);
                        foodDetailIntent.putExtra("foodId", adapter.getRef(position).getKey());
                        Toast.makeText(Foodlist.this, ""+foodItem.getName1(), Toast.LENGTH_LONG).show();
                        startActivity(foodDetailIntent);
                    }
                });
            }
        };
     recyclerView_food.setAdapter(adapter);
     swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
