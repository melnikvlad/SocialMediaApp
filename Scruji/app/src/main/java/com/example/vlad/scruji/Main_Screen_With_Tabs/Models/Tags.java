package com.example.vlad.scruji.Main_Screen_With_Tabs.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;



public abstract class Tags extends RealmObject {

    @PrimaryKey
    private long id;

    private String description;

    public Tags() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
