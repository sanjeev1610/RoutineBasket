package com.mobiapp4u.pc.routinebasket;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Model.Rating;
import com.mobiapp4u.pc.routinebasket.ViewHolder.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComments extends AppCompatActivity {
    DatabaseReference comments;
    FirebaseRecyclerAdapter<Rating,CommentHolder> adapter;

    RecyclerView comment_recView;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    String foodid;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/r_f.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_show_comments);
        comment_recView = (RecyclerView)findViewById(R.id.show_cmnt_recview);
        comment_recView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        comment_recView.setLayoutManager(layoutManager);
        comments = FirebaseDatabase.getInstance().getReference("Rating");

        foodid = getIntent().getStringExtra(Common.COMMENT_FOODID);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.comment_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {

                    loadComments();
                }else{
                    Toast.makeText(ShowComments.this,"Please Check your Internet Connection!!!",Toast.LENGTH_LONG).show();

                }
            }
        });

        //default first item load
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadComments();
                }else{
                    Toast.makeText(ShowComments.this,"Please Check your Internet Connection!!!",Toast.LENGTH_LONG).show();

                }
            }
        });

        loadComments();

    }

    private void loadComments() {
     adapter = new FirebaseRecyclerAdapter<Rating, CommentHolder>(Rating.class,
             R.layout.layout_show_comments,
             CommentHolder.class,
             comments.orderByChild("foodId").equalTo(foodid)) {
         @Override
         protected void populateViewHolder(CommentHolder viewHolder, Rating model, int position) {

                 viewHolder.userComment.setText(model.getComment());
                 viewHolder.userPhone.setText("XXXXXX" + model.getUserPhone().substring(model.getUserPhone().length() - 4, model.getUserPhone().length()));
                 viewHolder.userRating.setRating(Float.valueOf(model.getRateValue()));

         }
     };
     comment_recView.setAdapter(adapter);
     swipeRefreshLayout.setRefreshing(false);
    }
}
