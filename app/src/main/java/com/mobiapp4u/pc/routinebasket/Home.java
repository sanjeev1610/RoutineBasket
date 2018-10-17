package com.mobiapp4u.pc.routinebasket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Database.Database;
import com.mobiapp4u.pc.routinebasket.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasket.Model.Banner;
import com.mobiapp4u.pc.routinebasket.Model.Category;
import com.mobiapp4u.pc.routinebasket.Model.Token;
import com.mobiapp4u.pc.routinebasket.Model.User;
import com.mobiapp4u.pc.routinebasket.ViewHolder.MenuViewHolder;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView textfullName;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference category;

    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> firebaseRecyclerAdapter;

    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;

    SliderLayout mSlider;
    HashMap<String,String> image_list;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/r_f.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("Restaurants").child(Common.restaurantSelected).child("details").child("Category");

         fab = (CounterFab)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCounterCount(Common.currentUser.getPhone()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       //set nav header name
        View headerView = navigationView.getHeaderView(0);
        textfullName = (TextView)headerView.findViewById(R.id.nav_txtFullname);
        textfullName.setText(Common.currentUser.getName());



        //swipe refresh
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.home_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu();

                }else{
                    Toast.makeText(Home.this,"Please Check your Internet Connection!!!",Toast.LENGTH_LONG).show();

                }
            }
        });

        //default first item load
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu();

                }else{
                    Toast.makeText(Home.this,"Please Check your Internet Connection!!!",Toast.LENGTH_LONG).show();

                }
            }
        });

     //load menu

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getLink()).into(viewHolder.imageView);
                final Category clickItem = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent foodListIntent = new Intent(Home.this, Foodlist.class);
                        foodListIntent.putExtra("categoryId", firebaseRecyclerAdapter.getRef(position).getKey());
                        startActivity(foodListIntent);
                        Toast.makeText(Home.this, ""+clickItem.getName(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        };

        recyclerView_menu = (RecyclerView)findViewById(R.id.recycler_menu);

           //aniamtion
        LayoutAnimationController controller  = AnimationUtils.loadLayoutAnimation(recyclerView_menu.getContext(),R.anim.layout_falldown);
        recyclerView_menu.setLayoutAnimation(controller);

        recyclerView_menu.setLayoutManager(new GridLayoutManager(this,2));
        if(Common.isConnectedToInternet(getBaseContext())) {
            loadMenu();
        }else{
            Toast.makeText(Home.this,"Please Check your Internet Connection!!!",Toast.LENGTH_LONG).show();

        }


        updateToken(FirebaseInstanceId.getInstance().getToken());
        //slider
        setupSlider();


    }

    private void setupSlider() {
        mSlider = (SliderLayout)findViewById(R.id.slider_home);
        image_list = new HashMap<>();
        final DatabaseReference banners = firebaseDatabase.getReference("Banner");
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Banner banner = postSnapshot.getValue(Banner.class);
                    //we concat imageName and id like name@@@01 and use name for description id for foodId to click
                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }
                for(String key:image_list.keySet()){
                    String[] key_split = key.split("@@@");
                    String nameOfFood = key_split[0];
                    String idOfFood = key_split[1];

                    //create slider
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView.description(nameOfFood)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .image(image_list.get(key))
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Home.this,FoodDetail.class);
                                    //we will send food id to foodDetail
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    //Add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("foodId",idOfFood);

                    mSlider.addSlider(textSliderView);
                    //remove event after finish
                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mSlider.moveNextPosition();
        mSlider.movePrevPosition();
        mSlider.setDuration(4000);

    }

    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, "false");
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu(){

        recyclerView_menu.setAdapter(firebaseRecyclerAdapter);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView_menu.getAdapter().notifyDataSetChanged();
        recyclerView_menu.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        mSlider.startAutoCycle();
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item.getItemId() == R.id.refresh){
            if(Common.isConnectedToInternet(getBaseContext())) {
                loadMenu();
            }else{
                Toast.makeText(Home.this,"Please check your Internet connection!!", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action

        } else if (id == R.id.nav_orders) {

            startActivity(new Intent(Home.this, OrderStatus.class));

        } else if (id == R.id.nav_logout) {
          //  Paper.book().destroy();
            AccountKit.logOut();
          Intent signIn = new Intent(Home.this, MainActivity.class);
          signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(signIn);

        } else if (id == R.id.nav_cart) {

            startActivity(new Intent(Home.this, Cart.class));

        }
         else if(id == R.id.nav_settings){
            showSubscribeNews();
        }else if(id == R.id.nav_fav){
            startActivity(new Intent(Home.this, FavouratesActivity.class));
        }else if(id == R.id.nav_share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hai,this is Routine Basket App shortly RB,Here you can shop online plz download it from play store and share to others ,thank you.");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSubscribeNews() {
        AlertDialog.Builder  alert = new AlertDialog.Builder(Home.this);
        alert.setTitle("Settings");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_settings_news,null);
        final CheckBox ckb_sub_news = (CheckBox) view.findViewById(R.id.ckb_checkbox_sub_news);
        final MaterialEditText home_address1 = (MaterialEditText)view.findViewById(R.id.edit_home_address1);
        final MaterialEditText editName1 = (MaterialEditText)view.findViewById(R.id.edit_update_name1);
        final MaterialEditText editEmail = (MaterialEditText)view.findViewById(R.id.edit_email);

        DatabaseReference usr = firebaseDatabase.getReference("Users");
        usr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()){
                    User u = new User();
                    u = dataSnapshot.child(Common.currentUser.getPhone()).getValue(User.class);
                    if(u.getEmail()!=null){
                        editEmail.setText(u.getEmail());
                    }else {
                        editEmail.setText("");
                    }
                    if(u.getHomeAdress()!=null){
                        home_address1.setText(u.getHomeAdress());
                    }else{
                        home_address1.setText("");
                    }
                    if(u.getName() !=null){
                        editName1.setText(u.getName());

                    }else {
                        editName1.setText("");

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        alert.setView(view);
        alert.setIcon(R.drawable.ic_settings_black_24dp);
        Paper.init(this);
        final String isSub_news = Paper.book().read("sub_news");
        if(isSub_news == null || TextUtils.isEmpty(isSub_news) || isSub_news.equals("false")){
            ckb_sub_news.setChecked(false);
        }else {
            ckb_sub_news.setChecked(true);
        }
        alert.setPositiveButton("Yes/Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final android.app.AlertDialog waitDialog = new  SpotsDialog.Builder().setContext(Home.this).build();
                waitDialog.show();
                if(ckb_sub_news.isChecked()){
                    waitDialog.dismiss();
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.TOPIC_NEWS);
                    Paper.book().write("sub_news","true");
                    Toast.makeText(Home.this,"Your Subscribed ",Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.TOPIC_NEWS);
                    Paper.book().write("sub_news","false");
                    Toast.makeText(Home.this,"Your UnSubscribed ",Toast.LENGTH_SHORT).show();

                }//checkbox

                getLalng(home_address1.getText().toString());
                final DatabaseReference dbRef = firebaseDatabase.getReference("Users");
                Common.currentUser.setHomeAdress(home_address1.getText().toString());
                Common.currentUser.setName(editName1.getText().toString());
                Common.currentUser.setEmail(editEmail.getText().toString());
                dbRef.child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitDialog.dismiss();
                                if(task.isSuccessful()){
                                    Toast.makeText(Home.this,"Updated Name Email Address",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(Home.this,"error in updated",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }//onclick
        })
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();



}


    private void getLalng(String s) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(s, 5);
            if (address == null) {
                return ;
            }
            if(address.size()<1){
                Toast.makeText(Home.this,"Null Address",Toast.LENGTH_SHORT).show();
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        Common.currentUser.setLatlng(String.format("%s,%s",p1.latitude,p1.longitude));
        Toast.makeText(Home.this,"Address"+String.valueOf(p1.latitude),Toast.LENGTH_SHORT).show();
        Log.d("LATLNG", String.valueOf(p1.latitude));


    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCounterCount(Common.currentUser.getPhone()));
    }
}
