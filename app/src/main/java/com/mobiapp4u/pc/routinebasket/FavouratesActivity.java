package com.mobiapp4u.pc.routinebasket;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Database.Database;
import com.mobiapp4u.pc.routinebasket.Helper.RecyclerItemTouchHelper;
import com.mobiapp4u.pc.routinebasket.Interface.RecyclerItemTouchHelperListner;
import com.mobiapp4u.pc.routinebasket.Model.Favourates;
import com.mobiapp4u.pc.routinebasket.ViewHolder.FavouratesAdapter;
import com.mobiapp4u.pc.routinebasket.ViewHolder.FavouratesViewHolder;

public class FavouratesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListner {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;
    FavouratesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourates);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_fav_view);
        rootLayout = (RelativeLayout)findViewById(R.id.root_layout_fav);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_falldown);
        recyclerView.setLayoutAnimation(controller);
        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        loadFavFood();

    }
    public void loadFavFood(){
        adapter = new FavouratesAdapter(new Database(getBaseContext()).getAllFavourates(Common.currentUser.getPhone()),this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof FavouratesViewHolder) {
            String name = ((FavouratesAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getFoodName();
            final Favourates deleteItem = ((FavouratesAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deletedIndex);
            new Database(getBaseContext()).removeFromFav(deleteItem.getFoodId(), Common.currentUser.getPhone());


            //snake bar
            Snackbar snackbar = Snackbar.make(rootLayout, name + "removed from favourates", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deletedIndex);
                    new Database(getBaseContext()).addToFav(deleteItem);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }
}
