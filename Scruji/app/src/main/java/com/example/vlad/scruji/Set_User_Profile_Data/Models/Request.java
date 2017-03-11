package com.example.vlad.scruji.Set_User_Profile_Data.Models;

public class Request {
    private String user_id;
    private String name;
    private String surname;
    private String age;
    private String country;
    private String city;


    public String getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }


    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
