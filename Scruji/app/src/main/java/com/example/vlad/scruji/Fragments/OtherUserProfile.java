package com.example.vlad.scruji.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Adapters.OtherPhotosAdapter;
import com.example.vlad.scruji.Adapters.PostsAdapter;
import com.example.vlad.scruji.Adapters.TagsAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.CheckResponse;
import com.example.vlad.scruji.Models.Post;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OtherUserProfile extends Fragment{
    CircularImageView roundedImageView;
    LinearLayout box,mess;
    TextView friendship,write;
    SharedPreferences pref;
    private MyDB db;
    private RecyclerView rv,photos_rv,posts_rv;
    private TextView name_lastname_age,country_city,back;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<String> list = new ArrayList<>();
    private String name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getPreferences();
        View view = inflater.inflate(R.layout.fragment_other_user_home,container,false);
        roundedImageView    = (CircularImageView)view.findViewById(R.id.myPic);
        name_lastname_age   = (TextView)view.findViewById(R.id.name_lastname_age);
        back                = (TextView)view.findViewById(R.id.btn_back);
        country_city        = (TextView)view.findViewById(R.id.country_city);
        box                 = (LinearLayout)view.findViewById(R.id.container_for_add_to_friends);
        mess                = (LinearLayout)view.findViewById(R.id.container_for_chat) ;
        friendship          = (TextView)view.findViewById(R.id.add_to_friends);
        write               = (TextView)view.findViewById(R.id.write_message);
        rv                  = (RecyclerView)view.findViewById(R.id.recycler_view);
        photos_rv           = (RecyclerView)view.findViewById(R.id.photos_rv);
        posts_rv            = (RecyclerView)view.findViewById(R.id.posts_rv);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUsersWithSameTag();
            }
        });
        friendship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(friendship.getText().toString() == "Добавить в друзья")  {
                    addToFriends(pref.getString(Constants.UNIQUE_ID,""),pref.getString(Constants.TEMP_ID,""));
                }
                else{
                    Toast.makeText(getActivityContex(),"Уже ваш друг",Toast.LENGTH_LONG).show();
                }
            }
        });
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeMessage();
            }
        });

        viewData();

        return view;
    }

    private void goToUsersWithSameTag() {
            UsersWithEqualTagsFragment fragment = new UsersWithEqualTagsFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.other_profile_frame,fragment).commit();
            fragmentManager.beginTransaction().addToBackStack(null);
    }

    private void goToChat() {
        Chat fragment = new Chat();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.other_profile_frame,fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
    }

    private void viewData() {
        checkForFriendship();
        loadIcon(pref.getString(Constants.TEMP_ID,""));
        loadPersonalInfo(pref.getString(Constants.TEMP_ID,""));
        loadPhotos(pref.getString(Constants.TEMP_ID,""));
        loadTags(pref.getString(Constants.TEMP_ID,""));
        loadPosts(pref.getString(Constants.TEMP_ID,""));
    }

    private void loadPersonalInfo(String user_id) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserResponse>> call = service.get_user_personal_info(user_id);
        call.enqueue(new retrofit2.Callback<ArrayList<UserResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserResponse>> call, Response<ArrayList<UserResponse>> response) {

                ArrayList<UserResponse> user = response.body();
                name = user.get(0).getName();
                FirebaseUserDetails.chatWith = name;
                name_lastname_age.setText(name +", " + user.get(0).getAge() + " y.o.");
                country_city.setText(user.get(0).getCountry() + ", " + user.get(0).getCity());
            }
            @Override
            public void onFailure(Call<ArrayList<UserResponse>> call, Throwable t) {}
        });
    }


    private void checkForFriendship() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<CheckResponse>> call = service.check_for_friendship(pref.getString(Constants.UNIQUE_ID,""),pref.getString(Constants.TEMP_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<CheckResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<CheckResponse>> call, Response<ArrayList<CheckResponse>> response) {
                ArrayList<CheckResponse> mResponse = response.body();
                Log.d("TAG+","FRIEND:"+mResponse.get(0).getMessage());
                if(Objects.equals(mResponse.get(0).getMessage(), "Yes")){
                    friendship.setText("У вас в друзьях");
                    friendship.setTextColor(Color.WHITE);
                    box.setBackgroundColor(Color.rgb(9,239,93));
                }
                else {
                    friendship.setText("Добавить в друзья");
                    friendship.setTextColor(Color.WHITE);
                    box.setBackgroundColor(Color.rgb(14,122,53));
                }
            }
            @Override
            public void onFailure(Call<ArrayList<CheckResponse>> call, Throwable t) {}
        });
    }

    private void writeMessage(){
        String url = "https://scrujichat.firebaseio.com/users.json";
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                if(s.equals("null")){
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        JSONObject obj = new JSONObject(s);

                        if(!obj.has(name)){
                            Toast.makeText(getActivity(), "user not found", Toast.LENGTH_LONG).show();
                        }
                        else {
                            goToChat();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                pd.dismiss();

            }
        },new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    private void addToFriends(String user_id,String other_user_id) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<String> call = service.insert_friend(user_id,other_user_id);
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                    friendship.setText("У вас в друзьях");
                    friendship.setTextColor(Color.WHITE);
                    box.setBackgroundColor(Color.rgb(9,239,93));
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });
    }

    private void loadIcon(String user_id) {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(getActivityContex())
                .load(Constants.PICASSO_MAIN +user_id+".png")
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


    private void loadPhotos(String user_id) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserOtherPhoto>> call = service.get_other_photos(user_id);
        call.enqueue(new retrofit2.Callback<ArrayList<UserOtherPhoto>>() {
            @Override
            public void onResponse(Call<ArrayList<UserOtherPhoto>> call, Response<ArrayList<UserOtherPhoto>> response) {

                ArrayList<UserOtherPhoto> mResponse = response.body();

                manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                photos_rv.setLayoutManager(manager);
                adapter = new OtherPhotosAdapter(getActivity(),mResponse);
                photos_rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<ArrayList<UserOtherPhoto>> call, Throwable t) {}
        });
    }

    private void loadTags(String user_id){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserTagsResponse>> call = service.get_user_tags(user_id);
        call.enqueue(new retrofit2.Callback<ArrayList<UserTagsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserTagsResponse>> call, Response<ArrayList<UserTagsResponse>> response) {

                ArrayList<UserTagsResponse> mResponse = response.body();
                for(UserTagsResponse t : mResponse){
                    list.add(t.getTag());
                }

                manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                rv.setLayoutManager(manager);
                adapter = new TagsAdapter(getActivity(),list);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<ArrayList<UserTagsResponse>> call, Throwable t) {}
        });
    }

    private void loadPosts(String user_id){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<Post>> call = service.get_user_posts(user_id);
        call.enqueue(new retrofit2.Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {

                ArrayList<Post> mResponse = response.body();

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
            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable t) {}
        });
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }
}
