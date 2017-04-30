package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Post;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


public class AddPost extends Fragment {
    private Bitmap bitmap = null;
    public TextView add,back;
    public EditText post_description;
    public ImageView post_image;
    public SharedPreferences pref;
    public MyDB db;

    public List<Post> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getPreferences();
        db = new MyDB(getActivityContex());

        View view = inflater.inflate(R.layout.fragment_add_post,container,false);
        back        = (TextView) view.findViewById(R.id.btn_back);
        add         = (TextView) view.findViewById(R.id.btn_add);
        post_description = (EditText)view.findViewById(R.id.post_description);
        post_image  = (ImageView)view.findViewById(R.id.post_image);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat df = new SimpleDateFormat("EEE,d MMM yyyy HH mm");
                String desc = post_description.getText().toString();
                String date = df.format(Calendar.getInstance().getTime());
                uploadImage(date);
                insertPostToMySQLandToSQLite(desc,date);
            }
        });

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });

        return view;
    }

    private void insertPostToMySQLandToSQLite(final String description, final String date){
        final String id = pref.getString(Constants.UNIQUE_ID,"");
        String photo_name = null;
        if(bitmap != null){
            photo_name = id+"_"+date;
        }

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<String> call = service.insert_post(id, date, description, photo_name);
        final String finalPhoto_name = photo_name;
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Post post = new Post(id, date, description, finalPhoto_name);
                db.insertPost(post);
                goToHome();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    public void uploadImage(String date){
        String image;
        if(getStringImage(bitmap)!=""){
            image = getStringImage(bitmap);
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            Service service = retrofit.create(Service.class);

            Call<String> call = service.upload_post_photo(image,pref.getString(Constants.UNIQUE_ID,""),date);
            call.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("TAG+","Error "+ t.getMessage());
                }
            });
        }
    }


    public String getStringImage(Bitmap bmp){
        if(bmp != null)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        else{
            return "";
        }

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                post_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToHome(){
        Home fragment = new Home();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.my_posts_frame,fragment);
        ft.commit();
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }
}
