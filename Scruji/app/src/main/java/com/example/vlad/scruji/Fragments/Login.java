package com.example.vlad.scruji.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UniqID;
import com.example.vlad.scruji.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Login extends Fragment implements View.OnClickListener{

    private AppCompatButton btn_login;
    private EditText name_et,et_password;
    private TextView tv_register;
    private ProgressBar progress;
    private SharedPreferences pref;
    String user,pass,email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);

        initViews(view);

        return view;
    }

    private void initViews(View view){
        pref = getPreferences();

        btn_login = (AppCompatButton)view.findViewById(R.id.btn_login);
        tv_register = (TextView)view.findViewById(R.id.tv_register);
        name_et = (EditText)view.findViewById(R.id.nameField);
        et_password = (EditText)view.findViewById(R.id.et_password);
        progress = (ProgressBar)view.findViewById(R.id.progress);
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_register:
                goToRegister();
                break;

            case R.id.btn_login:
                loginProcess();
                break;
        }
    }

    private void loginProcess(){
        user = name_et.getText().toString();
        pass = et_password.getText().toString();

        if(user.equals("")){
            name_et.setError("can't be blank");
        }
        else if(pass.equals("")){
            et_password.setError("can't be blank");
        }
        else{
            String url = "https://scrujichat.firebaseio.com/users.json";
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("Loading...");
            pd.show();

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    if(s.equals("null")){
                        Toast.makeText(getActivity(), "user not found", Toast.LENGTH_LONG).show();
                    }
                    else{
                        try {
                            JSONObject obj = new JSONObject(s);

                            if(!obj.has(user)){
                                Toast.makeText(getActivity(), "user not found", Toast.LENGTH_LONG).show();
                            }
                            else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                FirebaseUserDetails.username = user;
                                FirebaseUserDetails.password = pass;
                                getUniqID(user);
                            }
                            else {
                                Toast.makeText(getActivity(), "incorrect password", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    pd.dismiss();
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                    pd.dismiss();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(getActivity());
            rQueue.add(request);
        }
    }

    private void getUniqID(String name){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        Service requestInterface = retrofit.create(Service.class);
        Call<ArrayList<UniqID>> response = requestInterface.get_uniq_id(name);
        response.enqueue(new Callback<ArrayList<UniqID>>() {
            @Override
            public void onResponse(Call<ArrayList<UniqID>> call, retrofit2.Response<ArrayList<UniqID>> response) {

                ArrayList<UniqID> resp = response.body();

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.EMAIL,resp.get(0).getEmail());
                    editor.putString(Constants.NAME,resp.get(0).getName());
                    editor.putString(Constants.UNIQUE_ID,resp.get(0).getUserId());
                    editor.apply();

                progress.setVisibility(View.INVISIBLE);
                goToProfile();
            }

            @Override
            public void onFailure(Call<ArrayList<UniqID>> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToRegister(){

        Register register = new Register();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,register);
        ft.commit();
    }

    private void goToProfile(){
        if(pref.getBoolean(Constants.PROFILE_CREATED,true))
        {
            MainScreen profile = new MainScreen();
            android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame,profile);
            ft.commit();
        }
        else {
            goToCreateProfile();
        }
    }

    private void goToCreateProfile(){

        CreateProfile profile = new CreateProfile();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,profile);
        ft.commit();
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
