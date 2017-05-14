package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vlad.scruji.Adapters.UsersAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UserResponse;
import com.example.vlad.scruji.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Friends extends Fragment {
    private SharedPreferences sharedPreferences;
    private RecyclerView rv;
    private UsersAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private Button back,map;
    private SearchView searchView;
    private ArrayList<UserResponse> list = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends,container,false);

        back = (Button)view.findViewById(R.id.back_to_home);
        map  = (Button)view.findViewById(R.id.map);
        rv = (RecyclerView)view.findViewById(R.id.rv);
        searchView = (SearchView)view.findViewById(R.id.serchview);
        sharedPreferences = getPreferences();
        adapter = new UsersAdapter(getActivity(),list);

        getUsers();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomeScreen();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.MAP_TYPE,"6");
                editor.apply();
                goToMap();
            }
        });

        setupSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        return view;
    }

    private void getUsers(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserResponse>> call = service.get_user_friends(sharedPreferences.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserResponse>> call, Response<ArrayList<UserResponse>> response) {

                ArrayList<UserResponse> mResponse = response.body();
                list.addAll(mResponse);
                manager = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager);
                adapter = new UsersAdapter(getActivity(),list);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);

            }
            @Override
            public void onFailure(Call<ArrayList<UserResponse>> call, Throwable t) {
                Log.d(Constants.TAG,"FAILURE " +t.getMessage());
            }
        });
    }

    private void goToHomeScreen(){
        Home fragment = new Home();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.friends_frame,fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
    }

    private void goToMap(){
        Map fragment = new Map();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.friends_frame,fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
    }

    public void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Найти пользователя");
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }
}
