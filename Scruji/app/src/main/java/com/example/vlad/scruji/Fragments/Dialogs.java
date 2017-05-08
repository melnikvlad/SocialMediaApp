package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Adapters.DialogsAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Dialogs extends Fragment {
    private RecyclerView rv;
    private DialogsAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private SearchView searchView;
    SharedPreferences pref;
    Firebase firebase;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_chats,container,false);
        rv = (RecyclerView)view.findViewById(R.id.rv_dialogs);
        manager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        pref = getPreferences();
        Firebase.setAndroidContext(getContext());
        firebase = new Firebase("https://scrujichat.firebaseio.com/messages/" + pref.getString(Constants.NAME,""));
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                parseDataToRecyclerView();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return view;
    }

    private void parseDataToRecyclerView() {
        final ArrayList<String> names_list = new ArrayList<>();
        final ArrayList<String> messages_list  = new ArrayList<>();
        final ArrayList<String> ids_list = new ArrayList<>() ;

        String url = "https://scrujichat.firebaseio.com/messages/"+pref.getString(Constants.NAME,"")+".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                if(s.equals("null")){
                    Toast.makeText(getActivity(), "no dialogs", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        Iterator i = obj.keys();
                        while(i.hasNext()){
                            String key = i.next().toString();
                            names_list.add(key);
                            JSONObject chat_with = new JSONObject();
                            chat_with = obj.getJSONObject(key);
                            JSONObject lastObj = new JSONObject();
                            lastObj = chat_with.getJSONObject("last");
                            ids_list.add(lastObj.getString("id"));
                            messages_list.add(lastObj.getString("message"));
                        }

                        manager = new LinearLayoutManager(getActivity());
                        rv.setLayoutManager(manager);
                        adapter = new DialogsAdapter(getActivityContex(),ids_list,names_list,messages_list);
                        adapter.notifyDataSetChanged();
                        rv.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivityContex());
        rQueue.add(request);
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
