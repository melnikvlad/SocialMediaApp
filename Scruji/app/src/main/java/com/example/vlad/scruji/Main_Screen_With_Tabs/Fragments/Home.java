package com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;


import com.example.vlad.scruji.Main_Screen_With_Tabs.DB.TagSQL;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.MyTag;
import com.example.vlad.scruji.Main_Screen_With_Tabs.RecyclerVIews.TagsAdapter;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.Set_User_Profile_Data.DB.UserProfileDB;
import com.example.vlad.scruji.Set_User_Profile_Data.Models.User;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment{
    private SharedPreferences pref;
    private CircularImageView roundedImageView;
    private TextView name_lastname_age,country_city,textAdd;
    private EditText editTag;
    private Button addTagbtn;
    private UserProfileDB userdb;
    private TagSQL tagdb;
    private int  id;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<String> list;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        list = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);
        roundedImageView = (CircularImageView) view.findViewById(R.id.imageView1);
        name_lastname_age = (TextView)view.findViewById(R.id.name_lastname_age);
        country_city = (TextView)view.findViewById(R.id.country_city);
        addTagbtn = (Button)view.findViewById(R.id.addTagbtn);
        editTag = (EditText)view.findViewById(R.id.editTag);
        textAdd = (TextView)view.findViewById(R.id.textAdd);
        rv = (RecyclerView)view.findViewById(R.id.recycler_view);

        manager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
        adapter = new TagsAdapter(getActivity().getApplication(),list);
        rv.setAdapter(adapter);

        pref = getActivity().getPreferences(0);
        userdb = new UserProfileDB(getActivity().getApplicationContext());
        tagdb = new TagSQL(getActivity().getApplicationContext());
        Log.d("TAG+","HOME SCREEN MUST BE SMTHNG "+pref.getString(Constants.UNIQUE_ID,""));
        id = userdb.getCertainUser(pref.getString(Constants.UNIQUE_ID,""));
        Log.d("TAG+","CURRENT USER № "+ id);
        //==========================================MAIN METHODS=============================================================================
        viewData(id);
        loadPicture(pref.getString(Constants.UNIQUE_ID,""));
        //===================================================================================================================================

        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = 0;
                String itemTag = editTag.getText().toString();
                editTag.setText("");
                list.add(position,"#" + itemTag);
                tagdb.insertTag(new MyTag(pref.getString(Constants.UNIQUE_ID,""),itemTag));
                adapter.notifyItemInserted(position);

                rv.scrollToPosition(position);
                Log.d("TAG+","Tag was inserted: " + itemTag);
            }
        });

        return view;
    }

    public void viewData (int i) {
        List<User> users = userdb.getAllData();
        List<MyTag> tags = tagdb.getUserTags(pref.getString(Constants.UNIQUE_ID,""));

        Log.d("TAG+","=================================USERS PROFILE INFO================================================= ");
        for (User u : users) {

            String log =
                    " ID: " + u.getUser_id() + " " +
                            ",NAME: " + u.getName() + " " +
                            ",LASTNAME: " + u.getSurname() + " " +
                            ",AGE: " + u.getAge() + " " +
                            ",COUNTRY: " + u.getCountry() + " " +
                            ",CITY: " + u.getCity();
            Log.d("TAG+",log);

            name_lastname_age.setText(users.get(i).getName() + " " + users.get(i).getSurname() + ", " + users.get(i).getAge() + " y.o.");
            country_city.setText(users.get(i).getCountry() + ", " + users.get(i).getCity());
        }
        Log.d("TAG+","==================================================================================================");



        Log.d("TAG+","=================================CURRENT USER TAGS INFO================================================= ");
        if(tags.size() == 0)
        {
            Log.d("TAG+","SQLite TagsDB is empty!");
        }
        for (MyTag t : tags) {

            list.add(t.getTagname());
            Log.d("TAG+","Tag :" + t.getTagname());
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
