package com.example.vlad.scruji.Models;

public class User {
        private String user_id;
        private String name;
        private String age;
        private String country;
        private String city;


    public User() {
    }

    public User(String user_id, String name,String age, String country, String city) {
        this.user_id = user_id;
        this.name = name;
        this.age = age;
        this.country = country;
        this.city = city;
    }

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

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }


}
