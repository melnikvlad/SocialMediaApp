package com.example.vlad.scruji.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.scruji.Adapters.OtherPhotosAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.InsertTagInterface;
import com.example.vlad.scruji.Interfaces.UserOtherPhotosInterface;
import com.example.vlad.scruji.Interfaces.UserTagsInterface;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Tag;
import com.example.vlad.scruji.Adapters.TagsAdapter;
import com.example.vlad.scruji.Models.UserOtherPhoto;
import com.example.vlad.scruji.Models.UserTagsResponse;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.example.vlad.scruji.Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Home extends Fragment  {
    private SharedPreferences pref;
    private CircularImageView roundedImageView;
    private TextView name_lastname_age,country_city,textAdd;
    private EditText editTag;
    private MyDB db;
    private RecyclerView rv,photos_rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<String> list = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pref = getPreferences();
        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);
        roundedImageView = (CircularImageView) view.findViewById(R.id.imageView1);
        name_lastname_age = (TextView)view.findViewById(R.id.name_lastname_age);
        country_city = (TextView)view.findViewById(R.id.country_city);
        editTag = (EditText)view.findViewById(R.id.editTag);
        textAdd = (TextView)view.findViewById(R.id.textAdd);
        rv = (RecyclerView)view.findViewById(R.id.recycler_view);
        photos_rv = (RecyclerView)view.findViewById(R.id.photos_rv);
        db = new MyDB(getActivityContex());

        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemTag = editTag.getText().toString();
                insertTagToMySQLandToSQLite(itemTag);
            }
        });

        viewData();

        return view;
    }

    public void viewData () {
        if(db.getUserTags(pref.getString(Constants.UNIQUE_ID,"")).size() == 0){
            loadTagsFromServer();
        }
        else {
            loadTagsFromSQLite();
        }

        loadOtherPhotosFromServer();
        loadPicture(pref.getString(Constants.UNIQUE_ID,""));

        User user = db.getUser(pref.getString(Constants.UNIQUE_ID,""));
        name_lastname_age.setText(user.getName() + " " + user.getSurname() + ", " + user.getAge() + " y.o.");
        country_city.setText(user.getCountry() + ", " + user.getCity());
    }

    private void loadTagsFromServer(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserTagsInterface service = retrofit.create(UserTagsInterface.class);
        Call<ArrayList<UserTagsResponse>> call = service.operation(pref.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserTagsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserTagsResponse>> call, Response<ArrayList<UserTagsResponse>> response) {
                Log.d("TAG+","LOAD FROM SERVER ");
                ArrayList<UserTagsResponse> mResponse = response.body();
                for(UserTagsResponse i : mResponse) {
                    db.insertTag(new Tag(i.getUserId(),i.getTag()));
                }

                list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));
                manager = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager);
                adapter = new TagsAdapter(getActivity(),list);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<ArrayList<UserTagsResponse>> call, Throwable t) {}
        });
    }

    private void loadOtherPhotosFromServer(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserOtherPhotosInterface service = retrofit.create(UserOtherPhotosInterface.class);
        Call<ArrayList<UserOtherPhoto>> call = service.operation(pref.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserOtherPhoto>>() {
            @Override
            public void onResponse(Call<ArrayList<UserOtherPhoto>> call, Response<ArrayList<UserOtherPhoto>> response) {

                ArrayList<UserOtherPhoto> mResponse = response.body();


                manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                photos_rv.setLayoutManager(manager);
                adapter = new OtherPhotosAdapter(getActivity(),mResponse);
                adapter.notifyDataSetChanged();
                photos_rv.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<ArrayList<UserOtherPhoto>> call, Throwable t) {}
        });
    }

    private void loadTagsFromSQLite(){
        list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));
        manager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
        adapter = new TagsAdapter(getActivity(),list);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    private void insertTagToMySQLandToSQLite(final String tag){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        InsertTagInterface service = retrofit.create(InsertTagInterface.class);
        Call<String> call = service.operation(pref.getString(Constants.UNIQUE_ID,""), tag);
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int position = 0;
                Tag tag_new = new Tag(pref.getString(Constants.UNIQUE_ID,""), tag);
                db.insertTag(tag_new);
                editTag.setText("");
                list.add(position,tag);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void loadPicture(String user_id) {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(getActivityContex())
                .load(Constants.PICASSO_URL+user_id+".png")
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
    public Context getActivityContex(){
        Context applicationContext = MainActivity.getContextOfApplication();
        return applicationContext;
    }

    public SharedPreferences getPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivityContex());
        return prefs;
    }

}
