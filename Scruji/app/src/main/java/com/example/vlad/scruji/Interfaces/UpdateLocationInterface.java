package com.example.vlad.scruji.Interfaces;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UpdateLocationInterface {
    @FormUrlEncoded
    @POST("save_user_location.php")
    Call<String> operation(@Field("user_id")    String user_id,
                           @Field("lat")        String lat,
                           @Field("lng")        String lng);

}
