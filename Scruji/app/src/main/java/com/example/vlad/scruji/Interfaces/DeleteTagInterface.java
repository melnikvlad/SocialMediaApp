package com.example.vlad.scruji.Interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DeleteTagInterface {
    @FormUrlEncoded
    @POST("delete_user_tag.php")
    Call<String> operation(@Field("user_id")    String user_id,
                           @Field("tag")        String tag);
}
