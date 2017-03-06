package com.example.vlad.scruji.Main_Screen_With_Tabs.Models;


import io.realm.RealmObject;

public  class HomeModel extends RealmObject {

    private String name;
    private String lastname;
    private String age;
    private String sex;
    private String height;
    private String eye_clr;
    private String hair_clr;
    private String country;
    private String city;

    public HomeModel() {
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEye_clr() {
        return eye_clr;
    }

    public void setEye_clr(String eye_clr) {
        this.eye_clr = eye_clr;
    }

    public String getHair_clr() {
        return hair_clr;
    }

    public void setHair_clr(String hair_clr) {
        this.hair_clr = hair_clr;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
