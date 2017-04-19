package com.example.vlad.scruji.Interfaces;

import com.example.vlad.scruji.Models.UserOtherPhoto;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface UserOtherPhotosInterface {
    @FormUrlEncoded
    @POST("get_other_photos.php")
    Call<ArrayList<UserOtherPhoto>> operation(
            @Field("user_id") String user_id
    );
}
