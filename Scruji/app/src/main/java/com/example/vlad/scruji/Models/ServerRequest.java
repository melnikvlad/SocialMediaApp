package com.example.vlad.scruji.Models;

public class ServerRequest {

    private String operation;
    private UserRegistrationData user;

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public void setUser(UserRegistrationData user) {
        this.user = user;
    }
}