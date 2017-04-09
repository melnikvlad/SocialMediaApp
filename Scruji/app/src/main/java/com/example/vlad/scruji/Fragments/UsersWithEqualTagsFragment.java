package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.R;

public class UsersWithEqualTagsFragment extends Fragment{
    private SharedPreferences sharedPreferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_with_equal_tag,container,false);
        sharedPreferences = getPreferences();
        Log.d(Constants.TAG,"TAG CLICK "+sharedPreferences.getString(Constants.TAG_ONCLICK,""));
        return view;
    }
    public Context getActivityContex(){
        Context applicationContext = MainActivity.getContextOfApplication();
        return applicationContext;
    }

    public SharedPreferences getPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivityContex());
        return prefs;
    }
}
