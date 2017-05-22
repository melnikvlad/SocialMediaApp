package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Adapters.UsersAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UserResponse;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersWithEqualTagsFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private RecyclerView rv;
    private UsersAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private Button back;
    private SearchView searchView;
    private ArrayList<UserResponse> list = new ArrayList<>();

    Bitmap bitmap = null;
    LinearLayout layout;
    RelativeLayout layout_2;
    ScrollView scrollView;
    TextView add,view_u,new_p,dismiss_u,dismiss;
    LinearLayout container_p,container_news,container_u;
    EditText post_description;

    MyDB db;
    Firebase reference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_with_equal_tag,container,false);

        view_u = (TextView)view.findViewById(R.id.view_u);
        new_p = (TextView)view.findViewById(R.id.new_p);
        dismiss_u = (TextView)view.findViewById(R.id.dismiss_u);
        dismiss = (TextView)view.findViewById(R.id.dismiss_p);
        container_p = (LinearLayout)view.findViewById(R.id.container_p);
        container_news = (LinearLayout)view.findViewById(R.id.container_news);
        container_u = (LinearLayout)view.findViewById(R.id.container_u);
        add = (TextView)view.findViewById(R.id.btn_add);

        post_description = (EditText)view.findViewById(R.id.post_description);
        layout = (LinearLayout) view.findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) view.findViewById(R.id.layout2);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        back = (Button)view.findViewById(R.id.back_to_home);
        rv = (RecyclerView)view.findViewById(R.id.rv_for_equal_tag);
        searchView = (SearchView)view.findViewById(R.id.serchview);
        sharedPreferences = getPreferences();
        adapter = new UsersAdapter(getActivity(),list);

        view_u.setOnClickListener(this);
        dismiss_u.setOnClickListener(this);
        new_p.setOnClickListener(this);
        dismiss.setOnClickListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomeScreen();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat df = new SimpleDateFormat("EEE,d MMM yyyy HH mm");
                String date = df.format(Calendar.getInstance().getTime());
                String desc = post_description.getText().toString();
                addNewsToFirebase(date,desc);
                post_description.setText("");
            }
        });

        reference = new Firebase("https://scrujichat.firebaseio.com/tags/"+sharedPreferences.getString(Constants.TAG_ONCLICK,"")+"/posts/");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                java.util.Map map = dataSnapshot.getValue(java.util.Map.class);
                String user = map.get("user").toString();
                String date = map.get("date").toString();
                String description = map.get("description").toString();

                addNode(user,date,description);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

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


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.view_u:
                getUsers();
                container_u.setVisibility(View.VISIBLE);
                container_p.setVisibility(View.GONE);
                dismiss.setVisibility(View.GONE);
                new_p.setVisibility(View.VISIBLE);
                view_u.setVisibility(View.GONE);
                dismiss_u.setVisibility(View.VISIBLE);
                break;
            case R.id.dismiss_u:
                container_u.setVisibility(View.GONE);
                dismiss_u.setVisibility(View.GONE);
                view_u.setVisibility(View.VISIBLE);
                break;
            case R.id.new_p:
                container_p.setVisibility(View.VISIBLE);
                container_u.setVisibility(View.GONE);
                dismiss_u.setVisibility(View.GONE);
                view_u.setVisibility(View.VISIBLE);
                new_p.setVisibility(View.GONE);
                dismiss.setVisibility(View.VISIBLE);
                break;
            case R.id.dismiss_p:
                container_p.setVisibility(View.GONE);
                dismiss.setVisibility(View.GONE);
                new_p.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void addNewsToFirebase(final String date, final String desc){
        String url = "https://scrujichat.firebaseio.com/tags.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase.setAndroidContext(getContext());

                java.util.Map<String, String> map = new HashMap<String, String>();
                map.put("user",sharedPreferences.getString(Constants.NAME,""));
                map.put("date", date);
                map.put("description",desc);
                reference.push().setValue(map);
            }
        },new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
                Log.d("TAG+","Error"+ volleyError.getMessage());
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    private void addNode(String user, String date, String desc){
        TextView tDate = new TextView(getActivity());
        TextView tText = new TextView(getActivity());
        tDate.setText(date);
        tText.setText(desc);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;
            lp2.gravity = Gravity.LEFT;

        tDate.setLayoutParams(lp2);
        tText.setLayoutParams(lp2);
        layout.addView(tText);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }


    private void getUsers(){
        list = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserResponse>> call = service.users_with_equal_tag(sharedPreferences.getString(Constants.TAG_ONCLICK,""));
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
        fragmentManager.beginTransaction().replace(R.id.users_equal_tag_frame,fragment).commit();
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
