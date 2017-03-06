package com.example.vlad.scruji.Login_Register_Stuff.Interfaces;

import com.example.vlad.scruji.Login_Register_Stuff.Models.Models.ServerRequest;
import com.example.vlad.scruji.Login_Register_Stuff.Models.Models.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;



public interface RequestInterface {

    @POST("index.php")
    Call<ServerResponse> operation(@Body ServerRequest request);

}
