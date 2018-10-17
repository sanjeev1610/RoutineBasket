package com.mobiapp4u.pc.routinebasket;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Database.Database;
import com.mobiapp4u.pc.routinebasket.Helper.RecyclerItemTouchHelper;
import com.mobiapp4u.pc.routinebasket.Interface.RecyclerItemTouchHelperListner;
import com.mobiapp4u.pc.routinebasket.Model.DataMessage;
import com.mobiapp4u.pc.routinebasket.Model.MyResponse;
import com.mobiapp4u.pc.routinebasket.Model.Order;
import com.mobiapp4u.pc.routinebasket.Model.Request;
import com.mobiapp4u.pc.routinebasket.Model.Token;
import com.mobiapp4u.pc.routinebasket.Remote.APIService;
import com.mobiapp4u.pc.routinebasket.Remote.IGoogleService;
import com.mobiapp4u.pc.routinebasket.ViewHolder.CartAdapter;

import com.mobiapp4u.pc.routinebasket.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import com.paypal.android.sdk.payments.PayPalService;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Cart extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, RecyclerItemTouchHelperListner {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

   public TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    String address,comment;

    APIService mService;
    //declare Google place Api
    IGoogleService mGoogleApiSerive;


//integrated Google service
private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;

    private Location mlastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 1000;
    private static int FATEST_INTERVA = 5000;
    private static int DISPLACEMENT = 10;

   // PlaceAutocompleteFragment editAddress;
    MaterialEditText editAddress;
    LatLng editLatLng;

    String  order_number =String.valueOf(System.currentTimeMillis());
    RelativeLayout rootLayout;

    int total1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestRuntimePermission();
        }
        else
        {
            if(checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //init APIService
        mService = Common.getFCMService();
        //init Google API service
        mGoogleApiSerive = Common.getGoogleApi();

        recyclerView = (RecyclerView)findViewById(R.id.recycler_Cart);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout_cart);
        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        txtTotalPrice  = (TextView)findViewById(R.id.total);
        btnPlace = (Button)findViewById(R.id.place_order);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size()>0){
                showAlertDialog();}
                else {
                    Toast.makeText(Cart.this, "Your Cart is empty!!!", Toast.LENGTH_LONG).show();

                }
            }
        });

        loadListFood();


    }
    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, LOCATION_PERMISSION_REQUEST);

    }
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(this, "This device is not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVA);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mlastLocation = location;
        displayLocation();
    }
    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestRuntimePermission();
        }
        else
        {
            mlastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mlastLocation != null){

             Log.d("LOCATION","Your Location:"+mlastLocation.getLatitude()+","+mlastLocation.getLongitude());
            }
            else
            {
                Log.d("LOCATION","Could not get your location");

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();

                        displayLocation();
                    }
                }
                break;
        }
    }

    public void loadListFood(){
      cart = new Database(this).getCart(Common.currentUser.getPhone());
      adapter = new CartAdapter(cart,this);
      adapter.notifyDataSetChanged();
      recyclerView.setAdapter(adapter);

      //cal total price
        int total = 0;
        for(Order order:cart){
            total+= (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));}

        total1 = total;
        txtTotalPrice.setText(String.valueOf(total));
        System.out.println(total);

    }
    public  void showAlertDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(Cart.this);
        alert.setTitle("One More Step Please Enter your Address...");
        alert.setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();

        View v=inflater.inflate(R.layout.layout_place_order,null);
        editAddress = (MaterialEditText)v.findViewById(R.id.edit_address_place_order);
        //Radio button
        final RadioButton shipToThisAddress = (RadioButton)v.findViewById(R.id.rb_setShipAddress);
        final RadioButton shipToHomeAddress = (RadioButton)v.findViewById(R.id.rb_setHomeAddress);
        final RadioButton cashOnDelivery = (RadioButton)v.findViewById(R.id.rb_cash_on);
        final RadioButton paypal = (RadioButton)v.findViewById(R.id.rb_paypal);
        final RadioButton instaMojo = (RadioButton)v.findViewById(R.id.rb_instamojo);

        //event radio
        shipToHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if (Common.currentUser.getHomeAdress() != null || !TextUtils.isEmpty(Common.currentUser.getHomeAdress())) {
                        address = Common.currentUser.getHomeAdress();
                      //  ((EditText) editAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
                        editAddress.setText(address);
                        getLalng(address);

                    } else {
                       // ((EditText) editAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setText("");
                        editAddress.setText("");
                        Toast.makeText(Cart.this, "Please update your home address", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        shipToThisAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(mlastLocation != null) {
                        getCompleteAddressString(mlastLocation.getLatitude(), mlastLocation.getLongitude());
                    }else {
                        Toast.makeText(Cart.this,"Please set your location or GPS on!!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    final MaterialEditText editComment = (MaterialEditText)v.findViewById(R.id.edit_comment_placeO);
        alert.setView(v);
        alert.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
                if(!shipToThisAddress.isChecked() && !shipToHomeAddress.isChecked())
                {

                        getLalng(editAddress.getText().toString());

                }
                if(TextUtils.isEmpty(address)){
                    Toast.makeText(Cart.this, "Please Enter Your address OR Select option for address", Toast.LENGTH_LONG).show();

                }

                comment = editComment.getText().toString();

                if(!cashOnDelivery.isChecked() && !instaMojo.isChecked()){
                    Toast.makeText(Cart.this, "Please select payment method", Toast.LENGTH_LONG).show();

                }
                else if(cashOnDelivery.isChecked()){
                    Request request = new Request(Common.currentUser.getPhone(), Common.currentUser.getName(), address,
                            txtTotalPrice.getText().toString(),
                            "0",
                            comment,
                            "cashOnDelivery",
                            "unPaid",//state from json
                            String.format("%s,%s", editLatLng.latitude, editLatLng.longitude),
                            cart);


                    //store to firebase within in system time as key

                    FirebaseDatabase.getInstance().getReference("Requests").child(String.valueOf(System.currentTimeMillis())).setValue(request);

                    Database db  = new Database(getBaseContext());
                    db.cleanCart(Common.currentUser.getPhone());
                    db.close();
                    Toast.makeText(Cart.this,"Your order is placed",Toast.LENGTH_LONG).show();
                    sendNotification();
                    finish();

                }else if(instaMojo.isChecked()){
                    String phone = Common.currentUser.getPhone();
                    String email,buyerName;
                    if(Common.currentUser.getEmail()!=null){
                        email = Common.currentUser.getEmail();
                    }else {
                        email = "user@gmail.com";
                    }
                    if(Common.currentUser.getName()!=null){
                        buyerName = Common.currentUser.getName();
                    }else {
                        buyerName = "buyer"+Common.currentUser.getPhone();
                    }


                    callInstamojoPay(email, phone.substring(phone.length()-10,phone.length()), txtTotalPrice.getText().toString(), "official", buyerName);

                }


            }
        })
        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });
          alert.show();

    }

    private void getLalng(String s) {
        Geocoder coder = new Geocoder(this);
        List<Address> addr;
        LatLng p1 = null;

        try {
            // May throw an IOException
            addr = coder.getFromLocationName(s, 5);
            if (addr == null) {
                return ;
            }
            if(addr.size()<1){
                Toast.makeText(Cart.this,"Null Address",Toast.LENGTH_SHORT).show();
            }

            Address location = addr.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

         editLatLng = p1;
        address = s;


    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                address = strAdd;
                getLalng(address);
               // ((EditText)editAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
                editAddress.setText(address);
                Log.w("My Current loction", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction", "Canont get Address!");
        }
        return strAdd;
    }

    public void sendNotification() {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Token serverToken = postSnapshot.getValue(Token.class);

                    //create raw payload to send
//                    Notification notification = new Notification("You have new order"+order_number, "Routine Basket");
//                    Sender content = new Sender(serverToken.getToken(),notification);
                    Map<String,String> dataSend = new HashMap<>();
                    dataSend.put("title","Routine Basket");
                    dataSend.put("message","You Have New Order"+String.valueOf(System.currentTimeMillis()));
                    assert serverToken != null;
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(), dataSend);

                    String test = new Gson().toJson(dataMessage);
                    Log.d("Content",test);

                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                    if(response.code() == 200) {
                                        assert response.body() != null;
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank You, Order Placed", Toast.LENGTH_LONG).show();
                                            finish();

                                        } else {
                                            Toast.makeText(Cart.this, "Failed!!!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Error",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)){
            deleteCart(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCart(int position) {
        cart.remove(position);

        new Database(this).cleanCart(Common.currentUser.getPhone());

        for(Order item:cart){
            new Database(this).addToCart(item);
        }
        loadListFood();
    }
    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder){
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deletedIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(),Common.currentUser.getPhone());
            //update total
            //calculate total price
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCart(Common.currentUser.getPhone());

            for(Order order:orders){
                total+= (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));}
            Locale locale = new Locale("en","Us");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));

            //snake bar
            Snackbar snackbar = Snackbar.make(rootLayout,name+"removed from cart",Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deletedIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    //calculate total price
                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCart(Common.currentUser.getPhone());
                    for(Order order:orders){
                        total+= (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));}
                    Locale locale = new Locale("en","Us");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }


    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    InstapayListener listener;


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Request  request = new Request(Common.currentUser.getPhone(), Common.currentUser.getName(), address,
                        txtTotalPrice.getText().toString(),
                        "0",
                        comment,
                        "instamojoPayment",
                        "paid",//state from json
                        String.format("%s,%s", editLatLng.latitude, editLatLng.longitude),
                        cart);


                //store to firebase within in system time as key



                String respons[] = response.split(":");
                String status = respons[0].substring(respons[0].indexOf("=")+1);
                String orderId = respons[1].substring(respons[1].indexOf("=")+1);
                String paymentId = respons[3].substring(respons[3].indexOf("=")+1);
                if(status.equals("success")){
                    FirebaseDatabase.getInstance().getReference("Requests").child(String.valueOf(System.currentTimeMillis())).setValue(request);
                    Database db  = new Database(Cart.this);
                    db.cleanCart(Common.currentUser.getPhone());
                    db.close();
                    sendNotification();
                }

                Intent confirm = new Intent(Cart.this,ConfirmationActivity.class);
                confirm.putExtra("OrderId",orderId);
                confirm.putExtra("PaymentId",paymentId);
                startActivity(confirm);
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}


















