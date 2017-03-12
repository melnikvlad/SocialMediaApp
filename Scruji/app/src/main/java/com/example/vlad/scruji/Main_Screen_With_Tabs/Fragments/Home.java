package com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.scruji.Constants.Constants;

import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.DB;
import com.example.vlad.scruji.Main_Screen_With_Tabs.RecyclerVIews.TagsAdapter;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.Set_User_Profile_Data.DB.UserProfileDB;
import com.example.vlad.scruji.Set_User_Profile_Data.Models.Request;
import com.example.vlad.scruji.Set_User_Profile_Data.Models.UserDB;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.list;


public class Home extends Fragment{
    private SharedPreferences pref;
    private CircularImageView roundedImageView;
    private TextView name_lastname_age,country_city,textAdd;
    private EditText editTag;
    private Button addTagbtn;
    private UserProfileDB db;
    private int  id;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final List<String> list = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);
        roundedImageView = (CircularImageView) view.findViewById(R.id.imageView1);
        name_lastname_age = (TextView)view.findViewById(R.id.name_lastname_age);
        country_city = (TextView)view.findViewById(R.id.country_city);
        addTagbtn = (Button)view.findViewById(R.id.addTagbtn);
        editTag = (EditText)view.findViewById(R.id.editTag);
        textAdd = (TextView)view.findViewById(R.id.textAdd);
        rv = (RecyclerView)view.findViewById(R.id.recycler_view);
        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = 0;
                String itemTag = editTag.getText().toString();
                list.add(position,"" + itemTag);
                adapter.notifyItemInserted(position);

                rv.scrollToPosition(position);
                Log.d("TAG+","Tag was inserted: " + itemTag);
            }
        });

        manager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
        adapter = new TagsAdapter(getActivity().getApplication(),list);
        rv.setAdapter(adapter);
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
