package com.example.vlad.scruji.Interfaces;

import com.example.vlad.scruji.Models.UsersWithEqualTags;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface getUsersWithEqualTagsInterface {
    @FormUrlEncoded
    @POST("users_with_equal_tag.php")
    Call<ArrayList<UsersWithEqualTags>> operation(
            @Field("tag") String tag
    );
}
