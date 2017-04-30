package com.example.vlad.scruji.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckResponse {

    @SerializedName("other_user_id")
    @Expose
    private String otherUserId;
    @SerializedName("message")
    @Expose
    private String message;

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
