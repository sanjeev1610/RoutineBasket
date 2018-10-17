package com.mobiapp4u.pc.routinebasket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasket.Common.Common;
import com.mobiapp4u.pc.routinebasket.Model.User;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 7171;
    Button btn_continue;
    TextView textLogo;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/r_f.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);
        if(AccountKit.getCurrentAccessToken() == null){

            serveAlert();
        }

        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

       // printHashKey();



        btn_continue = (Button) findViewById(R.id.btn_continue);

        textLogo = (TextView)findViewById(R.id.logo_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/dancing_script_regular.ttf");
        textLogo.setTypeface(typeface);

      //  Paper.init(this);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startLoginSystem();
            }
        });


        //check session facebook account kit
        if(AccountKit.getCurrentAccessToken() != null){
            //show dialog
            final android.app.AlertDialog waitDialog = new  SpotsDialog.Builder().setContext(this).build();
            waitDialog.show();
            waitDialog.setMessage("Please Wait");
            waitDialog.setCancelable(false);
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    users.child(account.getPhoneNumber().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User localUser = dataSnapshot.getValue(User.class);
                                    Intent homeIntent = new Intent(MainActivity.this,RestaurantList.class);
                                    Common.currentUser = localUser;
                                    startActivity(homeIntent);
                                    waitDialog.dismiss();
                                    finish();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(MainActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });

        }


    }


    private void serveAlert() {

        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.location_auto_complete,null);
        final AutoCompleteTextView place = (AutoCompleteTextView)v.findViewById(R.id.auto_location);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.Locations));
        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,loc);
        final String[] loc = getResources().getStringArray(R.array.Locations);
        place.setThreshold(1);
        place.setAdapter(arrayAdapter);
        alrt.setView(v);

        alrt.setTitle("ROUTINE BASKET")
                .setMessage("Service at Hyderabad")
                .setIcon(R.drawable.ic_launcher_round_logo)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean found = false;
                        for(String s:loc){
                            if(s.equals(place.getText().toString()))
                                found = true;
                        }
                        if(found){
                            dialog.dismiss();
                        }else {
                            Toast.makeText(MainActivity.this,"Sorry,in selected location service not available",Toast.LENGTH_LONG).show();
                            serveAlert();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void startLoginSystem() {


        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }

    private void printHashKey() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.pc.routinebasket", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String password) {
        if (Common.isConnectedToInternet(getBaseContext())) {
            final FirebaseDatabase fd  = FirebaseDatabase.getInstance();
            final DatabaseReference dbRef = fd.getReference("Users");

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please wait......");
            mDialog.show();

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(password)) {
                            Common.currentUser = user;

                            startActivity(new Intent(MainActivity.this, RestaurantList.class));
                            finish();

                            Toast.makeText(MainActivity.this, "Authentication sucess", Toast.LENGTH_LONG).show();

                        } else {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Authentication password error", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else
        {
            Toast.makeText(MainActivity.this,"Please check your Internet connection!!!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(result.getError() != null){
                Toast.makeText(this,""+result.getError().getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                return;
            }else if(result.wasCancelled()){
                Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show();
                return;
            }else if(result.getAccessToken() !=null){
                //show dialog
                 final android.app.AlertDialog waitDialog = new  SpotsDialog.Builder().setContext(this).build();
                 waitDialog.show();
                 waitDialog.setMessage("Please Wait");
                 waitDialog.setCancelable(false);

                 //get current Phone
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        final String userPhone = account.getPhoneNumber().toString();
                        //Check if exists on firebase Users
                        users.orderByKey().equalTo(userPhone)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.child(userPhone).exists()){ //not exists
                                            //create new user and login
                                            User newUser = new User();
                                            newUser.setPhone(userPhone);
                                            newUser.setName("");

                                            users.child(userPhone).setValue(newUser)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(MainActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                                                            }
                                                            users.child(userPhone)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            User localUser = dataSnapshot.getValue(User.class);
                                                                            Intent homeIntent = new Intent(MainActivity.this,RestaurantList.class);
                                                                            Common.currentUser = localUser;
                                                                            startActivity(homeIntent);
                                                                            waitDialog.dismiss();
                                                                            finish();

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {
                                                                            Toast.makeText(MainActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    });
                                                        }
                                                    });


                                        }else{
                                            users.child(userPhone)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            User localUser = dataSnapshot.getValue(User.class);
                                                            Intent homeIntent = new Intent(MainActivity.this,RestaurantList.class);
                                                            Common.currentUser = localUser;
                                                            startActivity(homeIntent);
                                                            waitDialog.dismiss();
                                                            finish();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            Toast.makeText(MainActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();

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
                    public void onError(AccountKitError accountKitError) {

                    }
                });
            }
        }
    }
}


























