package com.example.vlad.scruji;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Fragments.Login;
import com.example.vlad.scruji.Fragments.MainScreen;
import com.example.vlad.scruji.Fragments.CreateProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends FragmentActivity {
    private SharedPreferences pref;
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication = getApplicationContext();
        pref = getPreferences(0);
        if(googlePlayServicesAvailiable()){
            initFragment();
        }

    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    public boolean googlePlayServicesAvailiable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailiable = api.isGooglePlayServicesAvailable(this);
        if(isAvailiable == ConnectionResult.SUCCESS){
            return true;
        }else if(api.isUserResolvableError(isAvailiable)){
            Dialog dialog = api.getErrorDialog(this,isAvailiable,0);
            dialog.show();
        }
        else {
            Toast.makeText(this,"Can't connect to services",Toast.LENGTH_LONG).show();
        }
        return false;
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
