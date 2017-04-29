package com.example.vlad.scruji.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vlad.scruji.Adapters.TagsVerticalAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Tag;
import com.example.vlad.scruji.Models.UserTagsResponse;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTags extends Fragment {
    private TextView back,add;
    private EditText editText;
    private SharedPreferences pref;
    private MyDB db;
    private RecyclerView rv;
    private TagsVerticalAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<String> list = new ArrayList<>();
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tags,container,false);
        back        = (TextView) view.findViewById(R.id.btn_back);
        add         = (TextView) view.findViewById(R.id.btn_add);
        editText    = (EditText) view.findViewById(R.id.et_add_tag);
        rv          = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchView  = (SearchView)view.findViewById(R.id.serchview);

        pref = getPreferences();
        db = new MyDB(getActivityContex());

        manager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        viewData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemTag = editText.getText().toString();
                if(!itemTag.equals("")){
                    insertTagToMySQLandToSQLite(itemTag);
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Вы уверены,что хотите удалить этот тэг?");
                    builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteUserTag(pref.getString(Constants.UNIQUE_ID,""),adapter.getTag(position));
                            deleteTagFromServer(pref.getString(Constants.UNIQUE_ID,""),adapter.getTag(position));
                            adapter.deleteItem(position);
                            adapter.notifyItemRemoved(position);
                            return;
                        }
                    }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                            return;
                        }
                    }).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv);

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

    private void viewData() {
        if(db.getUserTags(pref.getString(Constants.UNIQUE_ID,"")).size() == 0){
            loadTagsFromServer();
        }
        else {
            loadTagsFromSQLite();
        }
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

                for(UserTagsResponse i : mResponse) {
                    db.insertTag(new Tag(i.getUserId(),i.getTag()));
                }

                list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));

                manager = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager);
                adapter = new TagsVerticalAdapter(getActivity(),list);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<ArrayList<UserTagsResponse>> call, Throwable t) {}
        });
    }

    private void deleteTagFromServer(final String user_id,final String tag){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<String> call = service.delete_user_tag(pref.getString(Constants.UNIQUE_ID,""), tag);
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void loadTagsFromSQLite(){
        list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));

        manager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
        adapter = new TagsVerticalAdapter(getActivity(),list);
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

        Service service = retrofit.create(Service.class);
        Call<String> call = service.insert_tag(pref.getString(Constants.UNIQUE_ID,""), tag);
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Tag tag_new = new Tag(pref.getString(Constants.UNIQUE_ID,""), tag);
                db.insertTag(tag_new);
                editText.setText("");
                list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));

                manager = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager);
                adapter = new TagsVerticalAdapter(getActivity(),list);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void goToHome(){
        Home fragment = new Home();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.my_tags_frame,fragment);
        ft.commit();
    }

    public void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Найти тэг");
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }
}
