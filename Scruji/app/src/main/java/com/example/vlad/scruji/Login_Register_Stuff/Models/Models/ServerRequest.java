package com.example.vlad.scruji.Login_Register_Stuff.Models.Models;

/**
 * Created by Vlad on 12.02.2017.
 */

public class ServerRequest {

    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }
}