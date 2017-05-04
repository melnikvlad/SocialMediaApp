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
import com.example.vlad.scruji.Models.ServerRequest;
import com.example.vlad.scruji.Models.ServerResponse;
import com.example.vlad.scruji.Models.UniqID;
import com.example.vlad.scruji.Models.UserRegistrationData;
import com.example.vlad.scruji.R;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Register extends Fragment implements View.OnClickListener{

    private AppCompatButton btn_register;
    private EditText et_email,et_password,et_name;
    private TextView tv_login;
    private ProgressBar progress;
    private SharedPreferences pref;
    String user, pass,email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);
        pref = getPreferences();
        Firebase.setAndroidContext(getContext());

        initViews(view);

        return view;
    }

    private void initViews(View view){
        btn_register    = (AppCompatButton)view.findViewById(R.id.btn_register);
        tv_login        = (TextView)view.findViewById(R.id.tv_login);
        et_name         = (EditText)view.findViewById(R.id.et_name);
        et_email        = (EditText)view.findViewById(R.id.et_email);
        et_password     = (EditText)view.findViewById(R.id.et_password);
        progress        = (ProgressBar)view.findViewById(R.id.progress);

        btn_register.setOnClickListener(this);
        tv_login.setOnClickListener(this)   ;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login:
                goToLogin();
                break;
            case R.id.btn_register:
                register_process();
                break;
        }
    }


    private void register_process(){
        user = et_name.getText().toString();
        pass = et_password.getText().toString();
        email= et_email.getText().toString();

        saveDataToMySQL(user,email,pass);

        if(user.equals("")){
            et_name.setError("can't be blank");
        }
        else if(pass.equals("")){
            et_password.setError("can't be blank");
        }
        else if(email.equals("")){
            et_email.setError("can't be blank");
        }
        else if(!user.matches("[A-Za-z0-9]+")){
            et_name.setError("only alphabet or number allowed");
        }
        else if(pass.length()<5){
            et_password.setError("at least 5 characters long");
        }
        else {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("Loading...");
            pd.show();

            String url = "https://scrujichat.firebaseio.com/users.json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    pd.dismiss();
                    setUniqID(user,s);
                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                    pd.dismiss();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(getActivity());
            rQueue.add(request);
        }
    }

    private void setUniqID(String name, final String s){

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

                Firebase reference = new Firebase("https://scrujichat.firebaseio.com/users");

                if(s.equals("null")) {
                    reference.child(user).child("password").setValue(pass);
                    reference.child(user).child("email").setValue(email);
                    reference.child(user).child("id").setValue(resp.get(0).getUserId());
                    Toast.makeText(getActivity(), "registration successful", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(user)) {
                            reference.child(user).child("password").setValue(pass);
                            reference.child(user).child("email").setValue(email);
                            reference.child(user).child("id").setValue(resp.get(0).getUserId());
                            Toast.makeText(getActivity(), "registration successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "username already exists", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                goToLogin();
            }

            @Override
            public void onFailure(Call<ArrayList<UniqID>> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void saveDataToMySQL(String name, String email, String password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        UserRegistrationData user = new UserRegistrationData();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
        request.setUser(user);

        Service service = retrofit.create(Service.class);
        Call<ServerResponse> call = service.index(request);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void goToLogin(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.PROFILE_CREATED,false);
        editor.apply();
        Fragment login = new Login();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,login);
        ft.commit();
    }
    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }
}
