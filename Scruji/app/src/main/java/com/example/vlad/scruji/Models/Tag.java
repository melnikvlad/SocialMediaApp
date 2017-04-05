package com.example.vlad.scruji.Models;

/**
 * Created by Vlad on 12.03.2017.
 */

public class Tag {
    private String user_id;
    private String tagname;

    public Tag() {
    }

    public Tag(String user_id, String tagname) {
        this.user_id = user_id;
        this.tagname = tagname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }
}
