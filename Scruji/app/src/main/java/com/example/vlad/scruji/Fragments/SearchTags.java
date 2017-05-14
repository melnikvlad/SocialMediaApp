package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Adapters.TagsPopularAdapter;
import com.example.vlad.scruji.Adapters.TagsVerticalAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UserTagsResponse;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchTags extends Fragment implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    LinearLayout range_container;
    TextView back,tv_range,tv_city,tv_world,tv_my_tags,tv_all_tags,tv_map ;
    EditText et_input_range;
    private MyDB db;
    private RecyclerView rv;
    private TagsVerticalAdapter adapter;
    private TagsPopularAdapter popularAdapter;
    private RecyclerView.LayoutManager manager,manager2;
    private List<String> list;
    List<String> popular_tags_list = new ArrayList<>();
    List<String> popular_tags_count_list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_tag,container,false);
        range_container = (LinearLayout)view.findViewById(R.id.radius_container);
        et_input_range  = (EditText)view.findViewById(R.id.et_metres);
        back            = (TextView)view.findViewById(R.id.btn_back);
        tv_map          = (TextView)view.findViewById(R.id.tv_go_to_map);
        tv_range        = (TextView)view.findViewById(R.id.circle);
        tv_city         = (TextView)view.findViewById(R.id.city);
        tv_world        = (TextView)view.findViewById(R.id.world);
        tv_my_tags      = (TextView)view.findViewById(R.id.myTag);
        tv_all_tags     = (TextView)view.findViewById(R.id.alTags);
        rv              = (RecyclerView)view.findViewById(R.id.rv);

        back.setOnClickListener(this);
        tv_range.setOnClickListener(this);
        tv_city.setOnClickListener(this);
        tv_world.setOnClickListener(this);
        tv_my_tags.setOnClickListener(this);
        tv_all_tags.setOnClickListener(this);

        sharedPreferences = getPreferences();
        manager = new LinearLayoutManager(getActivity());
        manager2 = new LinearLayoutManager(getActivity());

        return view;
    }

    @Override
    public void onClick(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = view.getId();
        switch (id){
            case R.id.btn_back:
                goToHomePage();
                break;
            case R.id.circle:
                editor.putString(Constants.MAP_TYPE,"1");
                editor.apply();
                viewRangeInputField();
                break;
            case R.id.city:
                editor.putString(Constants.MAP_TYPE,"2");
                editor.apply();
                goToMap();
                break;
            case R.id.world:
                editor.putString(Constants.MAP_TYPE,"3");
                editor.apply();
                goToMap();
                break;
            case R.id.myTag:
                rv.setLayoutManager(manager2);
                rv.setAdapter(adapter);
                viewMyTags();
                editor.putString(Constants.MAP_TYPE,"4");
                editor.apply();
                break;
            case R.id.alTags:
                rv.setLayoutManager(manager);
                rv.setAdapter(popularAdapter);
                viewAllTags();
                editor.putString(Constants.MAP_TYPE,"5");
                editor.apply();
                break;
        }
    }

    private void viewAllTags(){
        String url = "https://scrujichat.firebaseio.com/tags.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                popular_tags_count_list = new ArrayList<>();
                Firebase.setAndroidContext(getContext());
                try {
                    JSONObject obj = new JSONObject(s);
                    Iterator x = obj.keys();
                    JSONArray jsonArray = new JSONArray();

                    while (x.hasNext()){
                        String key = x.next().toString();
                        popular_tags_list.add(key);
                        jsonArray.put(obj.get(key));
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject users = (JSONObject) jsonArray.get(i);
                        popular_tags_count_list.add(String.valueOf(users.getJSONObject("users").length()));
                    }


                    manager = new LinearLayoutManager(getActivity());
                    rv.setLayoutManager(manager);
                    popularAdapter = new TagsPopularAdapter(getActivity(),popular_tags_list,popular_tags_count_list);
                    rv.setAdapter(popularAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    private void viewMyTags() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserTagsResponse>> call = service.get_user_tags(sharedPreferences.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserTagsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserTagsResponse>> call, Response<ArrayList<UserTagsResponse>> response) {
                list = new ArrayList<>();
                for(UserTagsResponse r: response.body()){
                    list.add(r.getTag());
                }
                manager2 = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager2);
                adapter = new TagsVerticalAdapter(getActivity(),list);
                rv.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<ArrayList<UserTagsResponse>> call, Throwable t) {
            }
        });
    }

    private void viewRangeInputField() {
        range_container.setVisibility(View.VISIBLE);
        tv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String metres = et_input_range.getText().toString();

                if(Objects.equals(metres, "")){
                    editor.putString(Constants.RANGE,"400");
                    editor.apply();
                }
                else{
                    editor.putString(Constants.RANGE, metres);
                    editor.apply();
                }
                goToMap();
            }
        });
    }

    private void goToMap() {
        Map fragment = new Map();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.search_frame,fragment);
        ft.commit();
    }

    private void goToHomePage() {
        Home fragment = new Home();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.search_frame,fragment);
        ft.commit();
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }
}
