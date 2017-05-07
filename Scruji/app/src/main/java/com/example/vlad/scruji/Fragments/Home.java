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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.scruji.Adapters.OtherPhotosAdapter;
import com.example.vlad.scruji.Adapters.PostsAdapter;
import com.example.vlad.scruji.Adapters.TagsAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Post;
import com.example.vlad.scruji.Models.Tag;
import com.example.vlad.scruji.Models.User;
import com.example.vlad.scruji.Models.UserOtherPhoto;
import com.example.vlad.scruji.Models.UserResponse;
import com.example.vlad.scruji.Models.UserTagsResponse;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Home extends Fragment  {
    private SharedPreferences pref;
    private CircularImageView roundedImageView;
    private TextView name_lastname_age,country_city,friends_count;
    private ImageView m_photos,m_tags,m_posts;
    private MyDB db;
    private RecyclerView rv,photos_rv,posts_rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<String> list = new ArrayList<>();
    private String name;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pref = getPreferences();
        db = new MyDB(getActivityContex());

        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);
        roundedImageView    = (CircularImageView) view.findViewById(R.id.imageView1);
        name_lastname_age   = (TextView)view.findViewById(R.id.name_lastname_age);
        country_city        = (TextView)view.findViewById(R.id.country_city);
        friends_count       = (TextView)view.findViewById(R.id.friend_count);
        m_photos            = (ImageView)view.findViewById(R.id.more_photos);
        m_tags              = (ImageView)view.findViewById(R.id.more_tags);
        m_posts             = (ImageView)view.findViewById(R.id.more_posts);
        rv                  = (RecyclerView)view.findViewById(R.id.recycler_view);
        photos_rv           = (RecyclerView)view.findViewById(R.id.photos_rv);
        posts_rv            = (RecyclerView)view.findViewById(R.id.posts_rv);

        viewData();



        m_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddPhoto();
            }
        });

        m_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMyTags();
            }
        });

        m_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddPost();
            }
        });

        return view;
    }


    public void viewData () {
        if(db.getUserTags(pref.getString(Constants.UNIQUE_ID,"")).size() == 0){
            loadTagsFromServer();
        }
        else {
            loadTagsFromSQLite();
        }
        loadFriendsCountFromServer();
        loadPostsFromServer();
        loadOtherPhotosFromServer();
        loadPicture(pref.getString(Constants.UNIQUE_ID,""));

        User user = db.getUser(pref.getString(Constants.UNIQUE_ID,""));
        if(user.getUser_id() == null){
            loadPersonalInfo();
        }
        else{
            name = user.getName();
            name_lastname_age.setText(user.getName()+" " + user.getAge()  + " y.o.");
            country_city.setText(user.getCountry() + ", " + user.getCity());
        }
    }

    private void loadFriendsCountFromServer(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<String> call = service.get_friends_count(pref.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String mResponse = response.body();
//                Log.d("TAG+","FR COUNT "+mResponse.size());
                friends_count.setText(mResponse);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG+","FR COUNT "+ t.getMessage());
            }
        });
    }

    private void loadPersonalInfo() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserResponse>> call = service.get_user_personal_info(pref.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserResponse>> call, Response<ArrayList<UserResponse>> response) {

                ArrayList<UserResponse> user = response.body();
                User new_user = new User(
                        user.get(0).getId(),
                        user.get(0).getName(),
                        user.get(0).getAge(),
                        user.get(0).getCountry(),
                        user.get(0).getCity()
                );
                db.insertUser(new_user);
                name = user.get(0).getName();
                name_lastname_age.setText(user.get(0).getName() +", " + user.get(0).getAge() + " y.o.");
                country_city.setText(user.get(0).getCountry() + ", " + user.get(0).getCity());
            }
            @Override
            public void onFailure(Call<ArrayList<UserResponse>> call, Throwable t) {}
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

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserOtherPhoto>> call = service.get_other_photos(pref.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserOtherPhoto>>() {
            @Override
            public void onResponse(Call<ArrayList<UserOtherPhoto>> call, Response<ArrayList<UserOtherPhoto>> response) {

                ArrayList<UserOtherPhoto> mResponse = response.body();
                if(mResponse.size()==0){
                    photos_rv.setVisibility(View.GONE);
                }
                else{
                    manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    photos_rv.setLayoutManager(manager);
                    adapter = new OtherPhotosAdapter(getActivity(),mResponse);
                    photos_rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<UserOtherPhoto>> call, Throwable t) {}
        });
    }

    private void loadTagsFromServer(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserTagsResponse>> call = service.get_user_tags(pref.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserTagsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserTagsResponse>> call, Response<ArrayList<UserTagsResponse>> response) {

                ArrayList<UserTagsResponse> mResponse = response.body();
                if (mResponse.size() == 0) {
                    rv.setVisibility(View.GONE);
                }
                else{
                    for(UserTagsResponse i : mResponse) {
                        db.insertTag(new Tag(i.getUserId(),i.getTag()));
                    }

                    list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));

                    manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    rv.setLayoutManager(manager);
                    adapter = new TagsAdapter(getActivity(),list);
                    adapter.notifyDataSetChanged();
                    rv.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<UserTagsResponse>> call, Throwable t) {}
        });
    }

    private void loadPostsFromServer(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<Post>> call = service.get_user_posts(pref.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {

                ArrayList<Post> mResponse = response.body();
                if (mResponse.size() == 0) {
                    posts_rv.setVisibility(View.GONE);
                }
                else {
                    manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    posts_rv.setLayoutManager(manager);

                    adapter = new PostsAdapter(
                            getActivity(),
                            mResponse,
                            name
                    );
                    adapter.notifyDataSetChanged();
                    posts_rv.setAdapter(adapter);
                }

            }
            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable t) {}
        });
    }

    private void loadTagsFromSQLite(){
        list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));

        if (list.size() == 0) {
            rv.setVisibility(View.GONE);
        }
        else{
            manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(manager);
            adapter = new TagsAdapter(getActivity(),list);
            adapter.notifyDataSetChanged();
            rv.setAdapter(adapter);
        }
    }

    private void loadPicture(String user_id) {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(getActivity())
                .load(Constants.PICASSO_MAIN +user_id+".png")
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

    private void goToAddPhoto(){
        AddPhoto fragment = new AddPhoto();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_frame,fragment);
        ft.commit();
    }

    private void goToAddPost() {
        AddPost fragment = new AddPost();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_frame,fragment);
        ft.commit();
    }

    private void goToMyTags(){
        AddTags fragment = new AddTags();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_frame,fragment);
        ft.commit();
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }

}
