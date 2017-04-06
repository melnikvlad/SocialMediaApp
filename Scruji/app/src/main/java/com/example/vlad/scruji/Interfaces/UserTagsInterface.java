package com.example.vlad.scruji.Interfaces;

import com.example.vlad.scruji.Models.UserTagsResponse;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserTagsInterface {
    @FormUrlEncoded
    @POST("get_user_tags.php")
    Call<ArrayList<UserTagsResponse>> operation(
            @Field("user_id") String user_id
    );
}
