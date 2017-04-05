package com.example.vlad.scruji.Interfaces;
import retrofit2.Call;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SetUserProfileInterface {

 @FormUrlEncoded
    @POST("scruji_create_profile.php")
    Call<String> operation(@Field("user_id")    String user_id,
                           @Field("name")       String name,
                           @Field("surname")    String surname,
                           @Field("age")        String age,
                           @Field("country")    String country,
                           @Field("city")       String city);
}
