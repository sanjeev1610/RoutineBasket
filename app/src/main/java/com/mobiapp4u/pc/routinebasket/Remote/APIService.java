package com.mobiapp4u.pc.routinebasket.Remote;

import com.mobiapp4u.pc.routinebasket.Model.DataMessage;
import com.mobiapp4u.pc.routinebasket.Model.MyResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA2OYvovY:APA91bHS0w3oMy4YRZV0I_1D_d_JVD0sq2g8WyNlhU7qif5uJppUPiUUXkCqc77yCWFkpbt6f0g8qld80olV2Bk6CcmSDoIOGz7zc3vnr-H7iLIZQyWlYfn_-TuVsSfu3UQ2L0PBSysP"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
