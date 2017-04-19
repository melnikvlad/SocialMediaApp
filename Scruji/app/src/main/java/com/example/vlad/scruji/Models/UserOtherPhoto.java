package com.example.vlad.scruji.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserOtherPhoto {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("photo")
    @Expose
    private String photo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
