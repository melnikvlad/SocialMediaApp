package com.example.vlad.scruji.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Markers {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longtitude")
    @Expose
    private String longtitude;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }
}
