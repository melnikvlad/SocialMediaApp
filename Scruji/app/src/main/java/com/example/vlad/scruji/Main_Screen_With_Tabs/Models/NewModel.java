package com.example.vlad.scruji.Main_Screen_With_Tabs.Models;

import io.realm.RealmObject;

/**
 * Created by Vlad on 04.03.2017.
 */

public class NewModel extends RealmObject {
    private String name;
    private String lastname;

    public NewModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
