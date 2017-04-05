package com.example.vlad.scruji.Fragments;


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
import android.widget.EditText;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Models.Tag;
import com.example.vlad.scruji.Adapters.TagsAdapter;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.example.vlad.scruji.Models.User;
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
    private MyDB db;
    private int  id;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<String> list;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pref = getActivity().getPreferences(0);
        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);
        roundedImageView = (CircularImageView) view.findViewById(R.id.imageView1);
        name_lastname_age = (TextView)view.findViewById(R.id.name_lastname_age);
        country_city = (TextView)view.findViewById(R.id.country_city);
        editTag = (EditText)view.findViewById(R.id.editTag);
        textAdd = (TextView)view.findViewById(R.id.textAdd);
        rv = (RecyclerView)view.findViewById(R.id.recycler_view);
        db = new MyDB(getActivity().getApplicationContext());

        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = 0;
                String itemTag = editTag.getText().toString();
                editTag.setText("");
                list.add(position,"#" + itemTag);
                db.insertTag(new Tag(pref.getString(Constants.UNIQUE_ID,""),"#"+itemTag));
                adapter.notifyItemInserted(position);
                rv.scrollToPosition(position);
            }
        });

        Log.d("TAG+","- HOME - USER_ID : "+pref.getString(Constants.UNIQUE_ID,""));
        viewData();
        loadPicture(pref.getString(Constants.UNIQUE_ID,""));
        return view;
    }

    public void viewData () {
        if(db.getUserTags(pref.getString(Constants.UNIQUE_ID,"")).size() == 0){
            //make Retrofit call
            list = new ArrayList<>();
            manager = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(manager);
            adapter = new TagsAdapter(getActivity().getApplication(),list);
            adapter.notifyDataSetChanged();
            rv.setAdapter(adapter);
        }
        else {
            list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));
            manager = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(manager);
            adapter = new TagsAdapter(getActivity().getApplication(),list);
            adapter.notifyDataSetChanged();
            rv.setAdapter(adapter);
        }

            User user = db.getUser(pref.getString(Constants.UNIQUE_ID,""));
            name_lastname_age.setText(user.getName() + " " + user.getSurname() + ", " + user.getAge() + " y.o.");
            country_city.setText(user.getCountry() + ", " + user.getCity());
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
