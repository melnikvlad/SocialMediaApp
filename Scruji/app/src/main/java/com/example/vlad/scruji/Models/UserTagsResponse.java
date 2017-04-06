package com.example.vlad.scruji.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserTagsResponse {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("tag")
    @Expose
    private String tag;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}