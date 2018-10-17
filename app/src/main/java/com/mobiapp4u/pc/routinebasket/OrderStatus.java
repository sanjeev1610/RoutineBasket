package com.mobiapp4u.pc.routinebasket;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Model.Request;
import com.mobiapp4u.pc.routinebasket.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

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
        setContentView(R.layout.activity_order_status);



        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_order_status);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
//        if(getIntent() == null) {
//            loadOrders(Common.currentUser.getPhone());
//        }else{
//            loadOrders(getIntent().getStringExtra("userPhone"));
//        }
        loadOrders(Common.currentUser.getPhone());
    }

    private void loadOrders(final String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class, R.layout.layout_order_status, OrderViewHolder.class, requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderStatus.setText(Common.converCodeToStatus(model.getStatus()));
                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adapter.getItem(position).getStatus().equals("0")) {
                            cancelOrder(adapter.getRef(position).getKey());
                        }else{
                            Toast.makeText(OrderStatus.this,"You can't Delete this Order",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };




        recyclerView.setAdapter(adapter);
    }

    private void cancelOrder(final String key) {
          FirebaseDatabase.getInstance().getReference("Requests")
                  .child(key).removeValue()
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          Toast.makeText(OrderStatus.this,new StringBuilder("Order").append(key)
                                  .append("has been deleted").toString(),Toast.LENGTH_LONG).show();

                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Toast.makeText(OrderStatus.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

                      }
                  });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
