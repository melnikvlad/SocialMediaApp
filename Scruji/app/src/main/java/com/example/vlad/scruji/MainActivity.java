package com.example.vlad.scruji;


import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Login_Register_Stuff.Fragments.LoginFragment;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments.MainScreenWithTabsFragment;
import com.example.vlad.scruji.Set_User_Profile_Data.Fragments.SetUserProfile;


public class MainActivity extends FragmentActivity {
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getPreferences(0);
        SharedPreferences.Editor editor = pref.edit();
        initFragment();
    }

    private void initFragment(){
        Fragment fragment;
        if((pref.getBoolean(Constants.PROFILE_CREATED,false))&&(pref.getBoolean(Constants.IS_LOGGED_IN,true))){
            fragment = new SetUserProfile();
        }
        if((pref.getBoolean(Constants.PROFILE_CREATED,true))&&(pref.getBoolean(Constants.IS_LOGGED_IN,false))){
            fragment = new LoginFragment();
        }
        if((pref.getBoolean(Constants.PROFILE_CREATED,true))&&(pref.getBoolean(Constants.IS_LOGGED_IN,true))) {
            fragment = new MainScreenWithTabsFragment();
        }
        else
        {
            fragment = new LoginFragment();
        }




        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,fragment);
        ft.commit();
    }
}
