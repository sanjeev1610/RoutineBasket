package com.mobiapp4u.pc.routinebasket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Database.Database;
import com.mobiapp4u.pc.routinebasket.Model.Food;
import com.mobiapp4u.pc.routinebasket.Model.Order;
import com.mobiapp4u.pc.routinebasket.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener{
    String foodId = "";

    TextView food_name, food_price, food_desc;
    ImageView food_image;
    ElegantNumberButton numberButton;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton  rateBtn;
    CounterFab btnCart;
    RatingBar ratingBar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference foood;
    DatabaseReference ratingTbl;
    Food currentFood;
    Button showComment;
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

        setContentView(R.layout.activity_food_detail);


        firebaseDatabase = FirebaseDatabase.getInstance();
        foood = firebaseDatabase.getReference("Restaurants").child(Common.restaurantSelected).child("details").child("Foood");
        ratingTbl = firebaseDatabase.getReference("Rating");

        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (CounterFab) findViewById(R.id.btnCart);

        food_desc = (TextView)findViewById(R.id.food_description);
        food_image = (ImageView)findViewById(R.id.img_food);
        food_name = (TextView)findViewById(R.id.food_name);
        food_price = (TextView)findViewById(R.id.food_price);
        rateBtn = (FloatingActionButton) findViewById(R.id.btn_rate);
        ratingBar = (RatingBar)findViewById(R.id.rateBar);
        showComment = (Button)findViewById(R.id.show_comment);
        
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null){
            foodId = getIntent().getStringExtra("foodId");

            }
            if(!foodId.isEmpty()){
              if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getFoodRating(foodId);
              }else{
                Toast.makeText(FoodDetail.this, "Please check your Internet Connection!!!", Toast.LENGTH_LONG).show();

              }
            }
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isExists = new Database(getBaseContext()).checkFoodExists(foodId,Common.currentUser.getPhone());
                if(!isExists) {
                    new Database(getBaseContext()).addToCart(new Order(Common.currentUser.getPhone(),
                            foodId,
                            currentFood.getName1(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage()));
                    Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    new Database(getBaseContext()).increaseCartfoodDetail(Common.currentUser.getPhone(),foodId, Integer.parseInt(numberButton.getNumber()));
                    Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }
            }

        });
        btnCart.setCount(new Database(this).getCounterCount(Common.currentUser.getPhone()));
        showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetail.this, ShowComments.class);
                intent.putExtra(Common.COMMENT_FOODID,foodId);
                startActivity(intent);
            }
        });


    }


    private void getFoodRating(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int sum = 0,count=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+= Integer.parseInt(item.getRateValue());
                    count++;

                }
                if(count !=0){
                float average = sum/count;
                ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quick Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodid){
        foood.child(foodid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName1());

                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName1());
                food_desc.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        final Rating rating = new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(value),comments);

        ratingTbl.push().setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(FoodDetail.this,"Your Rating added ",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(FoodDetail.this,"Your Rating not added ",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}

























