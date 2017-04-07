package com.example.vlad.scruji;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Fragments.Login;
import com.example.vlad.scruji.Fragments.MainScreen;
import com.example.vlad.scruji.Fragments.CreateProfile;


public class MainActivity extends FragmentActivity {
    private SharedPreferences pref;
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication = getApplicationContext();
        pref = getPreferences(0);

        initFragment();
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    private void initFragment(){
        Fragment fragment;
        if((pref.getBoolean(Constants.PROFILE_CREATED,false))&&(pref.getBoolean(Constants.IS_LOGGED_IN,true))){
            fragment = new CreateProfile();
        }
        if((pref.getBoolean(Constants.PROFILE_CREATED,true))&&(pref.getBoolean(Constants.IS_LOGGED_IN,false))){
            fragment = new Login();
        }
        if((pref.getBoolean(Constants.PROFILE_CREATED,true))&&(pref.getBoolean(Constants.IS_LOGGED_IN,true))) {
            fragment = new MainScreen();
        }
        else {
            fragment = new Login();
        }
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,fragment);
        ft.commit();
    }
}
