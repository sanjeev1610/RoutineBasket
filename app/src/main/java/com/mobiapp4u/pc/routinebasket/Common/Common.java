package com.mobiapp4u.pc.routinebasket.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mobiapp4u.pc.routinebasket.Model.User;
import com.mobiapp4u.pc.routinebasket.Remote.APIService;
import com.mobiapp4u.pc.routinebasket.Remote.IGoogleService;
import com.mobiapp4u.pc.routinebasket.Remote.RetrofitClient;
import com.mobiapp4u.pc.routinebasket.Remote.RetrofitClient1;

public class Common {
    public static User currentUser;

    public static String PHONE_TEXT = "userPhone";
    public static String COMMENT_FOODID = "userPhone";
    public static String TOPIC_NEWS = "News";
    public static String restaurantSelected = "";



    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
    public static IGoogleService getGoogleApi(){
        return RetrofitClient1.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static final String DELETE = "delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static String converCodeToStatus(String status){
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On My way";
        else if(status.equals("2"))
            return "Shipping";
        else
            return "Shipped";

    }

    public static Boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }


}
