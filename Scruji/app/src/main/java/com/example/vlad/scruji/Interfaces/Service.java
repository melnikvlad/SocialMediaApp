package com.example.vlad.scruji.Interfaces;

import com.example.vlad.scruji.Models.Markers;
import com.example.vlad.scruji.Models.ServerRequest;
import com.example.vlad.scruji.Models.ServerResponse;
import com.example.vlad.scruji.Models.UserOtherPhoto;
import com.example.vlad.scruji.Models.UserTagsResponse;
import com.example.vlad.scruji.Models.UsersWithEqualTags;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Service {
    @FormUrlEncoded
    @POST("upload_other_photos.php")
    Call<String> upload_other_photos(
            @Field("image")       String image,
            @Field("user_id")     String user_id,
            @Field("description") String description
    );

    @FormUrlEncoded
    @POST("delete_user_tag.php")
    Call<String> delete_user_tag(
            @Field("user_id")    String user_id,
            @Field("tag")        String tag
    );

    @POST("get_all_markers.php")
    Call<ArrayList<Markers>> get_all_markers();

    @FormUrlEncoded
    @POST("users_with_equal_tag.php")
    Call<ArrayList<UsersWithEqualTags>> users_with_equal_tag(
            @Field("tag") String tag
    );

    @FormUrlEncoded
    @POST("insert_tag.php")
    Call<String> insert_tag(
            @Field("user_id")    String user_id,
            @Field("tag")        String tag
    );

    @POST("index.php")
    Call<ServerResponse> index(@Body ServerRequest request);

    @FormUrlEncoded
    @POST("create_profile.php")
    Call<String> create_profile(
            @Field("user_id")    String user_id,
            @Field("name")       String name,
            @Field("surname")    String surname,
            @Field("age")        String age,
            @Field("country")    String country,
            @Field("city")       String city
    );

    @FormUrlEncoded
    @POST("save_user_location.php")
    Call<String> save_user_location(
            @Field("user_id")    String user_id,
            @Field("lat")        String lat,
            @Field("lng")        String lng
    );

    @FormUrlEncoded
    @POST("get_other_photos.php")
    Call<ArrayList<UserOtherPhoto>> get_other_photos(
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("get_user_tags.php")
    Call<ArrayList<UserTagsResponse>> get_user_tags(
            @Field("user_id") String user_id
    );
}