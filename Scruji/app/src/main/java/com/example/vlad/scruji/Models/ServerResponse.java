package com.example.vlad.scruji.Models;



public class ServerResponse {

    private String result;
    private String message;
    private UserRegistrationData user;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public UserRegistrationData getUser() {
        return user;
    }
}
