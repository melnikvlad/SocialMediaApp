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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vlad.scruji.Adapters.TagsAdapter;
import com.example.vlad.scruji.Adapters.UsersAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.getUsersWithEqualTagsInterface;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UsersWithEqualTags;
import com.example.vlad.scruji.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersWithEqualTagsFragment extends Fragment{
    private SharedPreferences sharedPreferences;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private Button back;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_with_equal_tag,container,false);
        back = (Button)view.findViewById(R.id.back_to_home);
        rv = (RecyclerView)view.findViewById(R.id.rv_for_equal_tag);
        sharedPreferences = getPreferences();
        getUsers();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomeScreen();
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

        getUsersWithEqualTagsInterface service = retrofit.create(getUsersWithEqualTagsInterface.class);
        Call<ArrayList<UsersWithEqualTags>> call = service.operation(sharedPreferences.getString(Constants.TAG_ONCLICK,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UsersWithEqualTags>>() {
            @Override
            public void onResponse(Call<ArrayList<UsersWithEqualTags>> call, Response<ArrayList<UsersWithEqualTags>> response) {

                ArrayList<UsersWithEqualTags> mResponse = response.body();

                manager = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager);
                adapter = new UsersAdapter(getActivity(),mResponse);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);

            }
            @Override
            public void onFailure(Call<ArrayList<UsersWithEqualTags>> call, Throwable t) {
                Log.d(Constants.TAG,"FAILURE " +t.getMessage());
            }
        });
    }

    private void goToHomeScreen(){
        Home fragment = new Home();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.users_equal_tag_frame,fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
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
