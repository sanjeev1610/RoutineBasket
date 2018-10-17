package com.mobiapp4u.pc.routinebasket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Remote.APIService;

public class ConfirmationActivity extends AppCompatActivity {

    TextView paymentId,orderId;
    Button btnDone;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);


        paymentId = (TextView)findViewById(R.id.txt_paymentid);
        orderId = (TextView)findViewById(R.id.txt_orderid);
        btnDone = (Button)findViewById(R.id.btn_done);
        mService = Common.getFCMService();
        if(getIntent() !=null){
            paymentId.setText(getIntent().getStringExtra("PaymentId"));
            orderId.setText(getIntent().getStringExtra("OrderId"));
        }else {
            return;
        }
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmationActivity.this,Home.class));
            }
        });
    }

}
