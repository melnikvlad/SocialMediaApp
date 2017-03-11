package com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.scruji.Constants.Constants;

import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.DB;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.Set_User_Profile_Data.DB.UserProfileDB;
import com.example.vlad.scruji.Set_User_Profile_Data.Models.Request;
import com.example.vlad.scruji.Set_User_Profile_Data.Models.UserDB;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;


public class Home extends Fragment{
    private SharedPreferences pref;
    CircularImageView roundedImageView;
    TextView name_lastname_age,country_city;
    UserProfileDB db;
    int  id;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);
        roundedImageView = (CircularImageView) view.findViewById(R.id.imageView1);
        name_lastname_age = (TextView)view.findViewById(R.id.name_lastname_age);
        country_city = (TextView)view.findViewById(R.id.country_city);

        pref = getActivity().getPreferences(0);
        db = new UserProfileDB(getActivity().getApplicationContext());
        Log.d("TAG+","HOME SCREEN MUST BE SMTHNG "+pref.getString(Constants.UNIQUE_ID,""));
        id = db.getCertainUser(pref.getString(Constants.UNIQUE_ID,""));
        Log.d("TAG+","CURRENT USER № "+ id);
        viewData(id);
        loadPicture(pref.getString(Constants.UNIQUE_ID,""));

        return view;
    }

    public void viewData (int i) {
        List<UserDB> users = db.getAllData();
        Log.d("TAG+","================================================================================================== ");
        for (UserDB cn : users) {

            String log =
                    " ID: " + cn.getUser_id() + " " +
                    ",NAME: " + cn.getName() + " " +
                    ",LASTNAME: " + cn.getSurname() + " " +
                    ",AGE: " + cn.getAge() + " " +
                    ",COUNTRY: " + cn.getCountry() + " " +
                    ",CITY: " + cn.getCity();
            Log.d("TAG+",log);

            name_lastname_age.setText(users.get(i).getName() + " " + users.get(i).getSurname() + ", " + users.get(i).getAge() + " y.o.");
            country_city.setText(users.get(i).getCountry() + ", " + users.get(i).getCity());
        }
        Log.d("TAG+","==================================================================================================");
    }

public void loadPicture(String user_id) {

    Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(3)
            .cornerRadiusDp(30)
            .oval(false)
            .build();

    Picasso.with(getActivity().getApplication().getApplicationContext())
            .load("http://10.0.2.2/server/uploads/main/"+user_id+".png")
            .placeholder(R.mipmap.ic_launcher)
            .transform(transformation)
            .into(roundedImageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("TAG+","PICASSO image loaded");
                }

                @Override
                public void onError() {
                    Log.d("TAG+","PICASSO image failed");
                }
            });
}

}
