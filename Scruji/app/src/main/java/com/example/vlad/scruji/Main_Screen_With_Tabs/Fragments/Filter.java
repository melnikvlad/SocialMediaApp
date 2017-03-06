package com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlad.scruji.R;

/**
 * Created by Vlad on 23.02.2017.
 */

public class Filter extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_filter,container,false);
        return view;
    }
}
