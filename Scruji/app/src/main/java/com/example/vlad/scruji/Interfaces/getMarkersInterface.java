package com.example.vlad.scruji.Interfaces;

import com.example.vlad.scruji.Models.Markers;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.POST;

public interface getMarkersInterface {
    @POST("get_all_markers.php")
    Call<ArrayList<Markers>> operation();
}
