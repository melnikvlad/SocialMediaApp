package com.example.vlad.scruji.Main_Screen_With_Tabs.Models;

/**
 * Created by Vlad on 12.03.2017.
 */

public class MyTag {
    private String user_id;
    private String tagname;

    public MyTag() {
    }

    public MyTag(String user_id, String tagname) {
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
