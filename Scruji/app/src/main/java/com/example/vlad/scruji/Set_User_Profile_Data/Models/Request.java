package com.example.vlad.scruji.Set_User_Profile_Data.Models;

public class Request {
    private String user_id;
    private String name;
    private String surname;
    private String age;
    private String country;
    private String city;
    private String sex;
    private String height;
    private String eye_clr;
    private String hair_clr;

    public String getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getEye_clr() {
        return eye_clr;
    }

    public String getHair_clr() {
        return hair_clr;
    }

    public String getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
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

    public void setEye_clr(String eye_clr) {
        this.eye_clr = eye_clr;
    }

    public void setHair_clr(String hair_clr) {
        this.hair_clr = hair_clr;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
