package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlad.scruji.Adapters.TagsAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.getUsersWithEqualTagsInterface;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Tag;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_with_equal_tag,container,false);
        sharedPreferences = getPreferences();
        getUsers();
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
                for(UsersWithEqualTags i : mResponse) {
                   Log.d(Constants.TAG,"USER : "
                           +i.getId()+" "
                           +i.getName()+" "
                           +i.getLastname()+" "
                           +i.getAge()+" y.o. "
                           +i.getCountry()+", "
                           +i.getCity());
                }
            }
            @Override
            public void onFailure(Call<ArrayList<UsersWithEqualTags>> call, Throwable t) {
                Log.d(Constants.TAG,"FAILURE " +t.getMessage());
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
