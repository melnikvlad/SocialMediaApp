package com.example.vlad.scruji.Fragments;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.example.vlad.scruji.Interfaces.SetUserProfileInterface;
import com.example.vlad.scruji.Models.Request;
import com.example.vlad.scruji.Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.app.Activity.RESULT_OK;


public class CreateProfile extends Fragment {

    private EditText editName ,editLastname,editCountry,editCity,editAge;
    private Button saveButton;
    private String name,surname,age,country,city;
    private SharedPreferences pref;
    private ImageView imageView;
    private Bitmap bitmap;
    private MyDB db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pref = getActivity().getPreferences(0);
        db = new MyDB(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_set_user_info,container,false);
        initViews(view);

        return view;
    }
    //============================================VIEWS INITIALIZATION================================================================
    public void initViews(View view)
    {
        Log.d("TAG+","- SETUP - USER ID : "+pref.getString(Constants.UNIQUE_ID,""));
        saveButton      = (Button)  view.findViewById(R.id.btn_save);
        editName        = (EditText)view.findViewById(R.id.et_name);
        editLastname    = (EditText)view.findViewById(R.id.et_lastname);
        editCity        = (EditText)view.findViewById(R.id.et_city);
        editCountry     = (EditText)view.findViewById(R.id.et_country);
        editAge         = (EditText)view.findViewById(R.id.et_age);
        imageView       = (ImageView) view.findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name    = editName.getText().toString();
                surname = editLastname.getText().toString();
                age     = editAge.getText().toString();
                country = editCountry.getText().toString();
                city    = editCity.getText().toString();

                createProfile(pref.getString(Constants.UNIQUE_ID,""), name, surname, age, country, city);
                uploadImage();
            }
        });
    }
    //============================================CREATE PROFILE================================================================
    public void createProfile(String user_id, String name, String lastname, String age, String country, String city) {
       User users = new User();
        db.insertUser(new User(user_id,name,lastname,age,country,city));
            users = db.getUser(user_id);
            Log.d("TAG+","- SETUP - ID IN LIST "+ users.getUser_id());

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            SetUserProfileInterface service = retrofit.create(SetUserProfileInterface.class);
                    Request request = new Request();
                    request.setUser_id(users.getUser_id());
                    request.setName(users.getName());
                    request.setSurname(users.getSurname());
                    request.setAge(users.getAge());
                    request.setCountry(users.getCountry());
                    request.setCity(users.getCity());

                Call<String> call = service.operation(
                        request.getUser_id(),
                        request.getName(),
                        request.getSurname(),
                        request.getAge(),
                        request.getCountry(),
                        request.getCity());

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("TAG+","OKEY RESPONSE : "+ response.body().toString());
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("TAG+","ERROR RESPONSE : "+ t.getMessage().toString());
                    }
                });
            goToProfile();
        }
        //============================================GO TO PROFILE====================================================================
    private void goToProfile(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.PROFILE_CREATED,true);
        editor.apply();

        Log.d("TAG+","- SETUP - GO TO PROFILE "+pref.getString(Constants.UNIQUE_ID,""));
        MainScreen profile = new MainScreen();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,profile);
        ft.commit();
    }
//============================================UPLOAD IMAGE TO SERVER================================================================
    private void uploadImage(){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Constants.UPLOAD_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {}},
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {}}){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                Map<String,String> params = new Hashtable<String, String>();
                params.put(Constants.KEY_IMAGE, image);
                params.put(Constants.KEY_NAME,pref.getString(Constants.UNIQUE_ID,"key") );
                return params;
            }};
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}