package com.example.vlad.scruji.Set_User_Profile_Data.Fragments;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments.MainScreenWithTabsFragment;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.HomeModel;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.Set_User_Profile_Data.Interfaces.SetUserProfileInterface;
import com.example.vlad.scruji.Set_User_Profile_Data.Models.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.app.Activity.RESULT_OK;


public class SetUserProfile extends Fragment {

    private EditText editName ,editLastname,editCountry,editCity,editAge,editHeight;
    private RadioGroup radioSex,radioHair,radioEye;
    private Button saveButton;
    private String name,surname,age,country,city,sex,height,eye_clr,hair_clr;
    private SharedPreferences pref;
    private MainScreenWithTabsFragment mainScreenWithTabsFragment;
    private ImageView imageView;
    private Bitmap bitmap;
    Realm myRealm;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainScreenWithTabsFragment =new  MainScreenWithTabsFragment();
        pref = getActivity().getPreferences(0);
        myRealm = Realm.getDefaultInstance();

        View view = inflater.inflate(R.layout.fragment_set_user_info,container,false);
        initViews(view);

        return view;
    }


    public void initViews(View view)
    {
        saveButton      = (Button)  view.findViewById(R.id.btn_save);
        editName        = (EditText)view.findViewById(R.id.et_name);
        editLastname    = (EditText)view.findViewById(R.id.et_lastname);
        editCity        = (EditText)view.findViewById(R.id.et_city);
        editCountry     = (EditText)view.findViewById(R.id.et_country);
        editAge         = (EditText)view.findViewById(R.id.et_age);
        editHeight      = (EditText)view.findViewById(R.id.et_height);
        radioSex        = (RadioGroup)view.findViewById(R.id.radioSex);
        radioHair       = (RadioGroup)view.findViewById(R.id.radioHair);
        radioEye        = (RadioGroup)view.findViewById(R.id.radioEye);
        imageView       = (ImageView) view.findViewById(R.id.imageView);


        radioSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(i);
                if(null!=radioButton && i > -1){
                    Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        radioHair.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(i);
                if(null!=radioButton && i > -1){
                    Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        radioEye.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(i);
                if(null!=radioButton && i > -1){
                    Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioButton rbS = (RadioButton) radioSex.findViewById(radioSex.getCheckedRadioButtonId());
                RadioButton rbH = (RadioButton) radioHair.findViewById(radioHair.getCheckedRadioButtonId());
                RadioButton rbE = (RadioButton) radioEye.findViewById(radioEye.getCheckedRadioButtonId());

                name = editName.getText().toString();
                surname = editLastname.getText().toString();
                age = editAge.getText().toString();
                country = editCountry.getText().toString();
                city = editCity.getText().toString();
                sex = rbS.getText().toString();
                height = editHeight.getText().toString();
                eye_clr = rbE.getText().toString();
                hair_clr = rbH.getText().toString();

                insertToRealmDB(name, surname, age, country, city, sex, height, eye_clr, hair_clr);
                uploadImage();
                createProfile(pref.getString(Constants.UNIQUE_ID,""), name, surname, age, country, city, sex, height, eye_clr, hair_clr);

                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(Constants.PROFILE_CREATED,true);
                editor.apply();

                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_frame,mainScreenWithTabsFragment);
                ft.commit();
            }
        });
    }
    public void createProfile(String user_id, String name, String lastname, String age,
                              String country, String city, String sex, String height, String eye_clr, String hair_clr) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        SetUserProfileInterface service = retrofit.create(SetUserProfileInterface.class);

        Request request = new Request();
        request.setUser_id(user_id);
        request.setName(name);
        request.setSurname(lastname);
        request.setAge(age);
        request.setCountry(country);
        request.setCity(city);
        request.setSex(sex);
        request.setHeight(height);
        request.setEye_clr(eye_clr);
        request.setHair_clr(hair_clr);

        Call<String> call = service.operation(
                request.getUser_id().toString(),
                request.getName().toString(),
                request.getSurname().toString(),
                request.getAge().toString(),
                request.getCountry().toString(),
                request.getCity().toString(),
                request.getSex().toString(),
                request.getHeight().toString(),
                request.getEye_clr().toString(),
                request.getHair_clr().toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void insertToRealmDB(String name, String lastname, String age,
                                String country, String city, String sex, String height, String eye_clr, String hair_clr)
    {

        myRealm.beginTransaction();
        HomeModel homeModel = myRealm.createObject(HomeModel.class);
        homeModel.setName(name);
        homeModel.setLastname(lastname);
        homeModel.setAge(age);
        homeModel.setCountry(country);
        homeModel.setCity(city);
        homeModel.setSex(sex);
        homeModel.setHeight(height);
        homeModel.setEye_clr(eye_clr);
        homeModel.setHair_clr(hair_clr);
        myRealm.commitTransaction();

        RealmResults<HomeModel> results = myRealm.where(HomeModel.class).findAll();
        for (HomeModel h:results)
        {
            Log.d("REALM",h.getName().toString());
        }
    }

    private void uploadImage(){

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Constants.UPLOAD_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                Map<String,String> params = new Hashtable<String, String>();
                params.put(Constants.KEY_IMAGE, image);
                params.put(Constants.KEY_NAME,pref.getString(Constants.UNIQUE_ID,"key") );
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //Adding request to the queue
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
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}