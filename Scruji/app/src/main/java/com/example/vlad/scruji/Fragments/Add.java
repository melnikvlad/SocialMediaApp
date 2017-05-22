package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vlad.scruji.Adapters.TagsPopularAdapter;
import com.example.vlad.scruji.Adapters.TagsVerticalAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Post;
import com.example.vlad.scruji.Models.Tag;
import com.example.vlad.scruji.Models.UserTagsResponse;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


public class Add extends Fragment implements View.OnClickListener{
    TextView back,add_p,add_t,add_n,add,add_post;
    EditText editText,post_description;
    LinearLayout container_p,container_t,container_n;
    Bitmap bitmap;
    Button saveButton;
    ImageView imageView,post_image;
    SharedPreferences pref;
    MyDB db;
    RecyclerView rv,rv_popular;
    TagsVerticalAdapter adapter;
    TagsPopularAdapter popularAdapter;
    RecyclerView.LayoutManager manager,manager2;
    List<String> list = new ArrayList<>();
    SearchView searchView;
    List<String> popular_tags_list = new ArrayList<>();
    List<String> popular_tags_count_list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getPreferences();
        View view = inflater.inflate(R.layout.fragment_add_all,container,false);
        back  = (TextView)view.findViewById(R.id.btn_back);
        add_p = (TextView)view.findViewById(R.id.add_p);
        add_t = (TextView)view.findViewById(R.id.add_t);
        add_n = (TextView)view.findViewById(R.id.add_n);
        container_p = (LinearLayout)view.findViewById(R.id.container_p);
        container_t = (LinearLayout)view.findViewById(R.id.container_t);
        container_n = (LinearLayout)view.findViewById(R.id.container_n);

        imageView   = (ImageView) view.findViewById(R.id.imageView);
        saveButton  = (Button) view.findViewById(R.id.btn_save);

        add         = (TextView) view.findViewById(R.id.btn_add_tag);
        editText    = (EditText) view.findViewById(R.id.et_add_tag);
        rv          = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv_popular  = (RecyclerView) view.findViewById(R.id.popular_tags);
        searchView  = (SearchView)view.findViewById(R.id.serchview);
        manager = new LinearLayoutManager(getActivity());
        manager2 = new LinearLayoutManager(getActivity());
        rv_popular.setLayoutManager(manager);
        rv_popular.setAdapter(popularAdapter);
        rv.setLayoutManager(manager2);
        rv.setAdapter(adapter);

        add_post         = (TextView) view.findViewById(R.id.btn_add_post);
        post_description = (EditText)view.findViewById(R.id.post_description);
        post_image  = (ImageView)view.findViewById(R.id.post_image);

        back.setOnClickListener(this);
        add_p.setOnClickListener(this);
        add_t.setOnClickListener(this);
        add_n.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        add.setOnClickListener(this);
        add_post.setOnClickListener(this);
        post_image.setOnClickListener(this);
        imageView.setOnClickListener(this);

        db = new MyDB(getActivityContex());
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Firebase firebase = new Firebase("https://scrujichat.firebaseio.com/tags");
                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Вы уверены,что хотите удалить этот тэг?");
                    builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteUserTag(pref.getString(Constants.UNIQUE_ID,""),adapter.getTag(position));
                            deleteTagFromServer(pref.getString(Constants.UNIQUE_ID,""),adapter.getTag(position));
                            firebase.child(adapter.getTag(position)).removeValue();
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                popularAdapter.filter(newText);
                return true;
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                goToHome();
                break;
            case R.id.add_p:
                viewPhotoContainer();
                break;
            case R.id.add_t:
                viewTagContainer();
                setupSearchView();
                viewData();
                break;
            case R.id.add_n:
                viewPostContainer();
                break;
            case R.id.btn_save:
                uploadImage();
                break;
            case R.id.btn_add_tag:
                addTag();
                break;
            case R.id.btn_add_post:
                addPost();
                break;
            case R.id.post_image:
                showFileChooserPost();
                break;
            case R.id.imageView:
                showFileChooser();
                break;
            default:
                break;
        }
    }

    private void viewPhotoContainer(){
        container_p.setVisibility(View.VISIBLE);
        container_t.setVisibility(View.GONE);
        container_n.setVisibility(View.GONE);
    }

    private void viewTagContainer(){
        container_p.setVisibility(View.GONE);
        container_t.setVisibility(View.VISIBLE);
        container_n.setVisibility(View.GONE);
    }

    private void viewPostContainer(){
        container_p.setVisibility(View.GONE);
        container_t.setVisibility(View.GONE);
        container_n.setVisibility(View.VISIBLE);
    }

    private void goToHome(){
        Home profile = new Home();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.add_frame,profile);
        ft.commit();
    }

    private void addPost(){
        DateFormat df = new SimpleDateFormat("EEE,d MMM yyyy HH mm");
        String date = df.format(Calendar.getInstance().getTime());
        String desc = post_description.getText().toString();

        uploadPostImage(date);
        insertPostToMySQLandToSQLite(desc,date);
    }

    private void addTag(){
        String itemTag = editText.getText().toString();
        if(!itemTag.equals("")){
            List<String> temp_list_of_tags = new ArrayList<String>();
            boolean can_add_tag = true;
            temp_list_of_tags = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));
            for(String item:temp_list_of_tags){
                if(Objects.equals(item, itemTag)){
                    can_add_tag = false;
                }
            }
            if(can_add_tag){
                insertTagToMySQLandToSQLite(itemTag);
                addTagToFirebase(itemTag);
            }
            else{
                Toast.makeText(getActivityContex(),"Тэг уже был добавлен",Toast.LENGTH_LONG).show();
            }
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.TEMP_TAG, "");
        editor.apply();
    }

    public void uploadImage(){

        String image = getStringImage(bitmap);
        DateFormat df = new SimpleDateFormat("EEE,d MMM yyyy HH mm");
        String date = df.format(Calendar.getInstance().getTime());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);

        Call<String> call = service.upload_other_photos(image,pref.getString(Constants.UNIQUE_ID,""),date);
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                goToHome();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG+","Error "+ t.getMessage());
            }
        });
    }

    public void uploadPostImage(String date){
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

                }
            });
        }
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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
                post_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void viewData() {
        editText.setText(pref.getString(Constants.TEMP_TAG,""));
        getPopularTagsFromFirebase();
        if(db.getUserTags(pref.getString(Constants.UNIQUE_ID,"")).size() == 0){
            loadTagsFromServer();
        }
        else {
            loadTagsFromSQLite();

        }
    }

    private void getPopularTagsFromFirebase(){
        String url = "https://scrujichat.firebaseio.com/tags.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase.setAndroidContext(getContext());
                try {
                    JSONObject obj = new JSONObject(s);
                    Iterator x = obj.keys();
                    JSONArray jsonArray = new JSONArray();

                    while (x.hasNext()){
                        String key = x.next().toString();
                        popular_tags_list.add(key);
                        jsonArray.put(obj.get(key));
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject users = (JSONObject) jsonArray.get(i);
                        popular_tags_count_list.add(String.valueOf(users.getJSONObject("users").length()));
                    }


                    manager = new LinearLayoutManager(getActivity());
                    rv_popular.setLayoutManager(manager);
                    popularAdapter = new TagsPopularAdapter(getActivity(),popular_tags_list,popular_tags_count_list);
                    popularAdapter.notifyDataSetChanged();
                    rv_popular.setAdapter(popularAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void addTagToFirebase(final String tag){
        String url = "https://scrujichat.firebaseio.com/tags.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase.setAndroidContext(getContext());
                Firebase reference = new Firebase("https://scrujichat.firebaseio.com/tags");
                
                reference.child(tag).child("users").push().setValue(pref.getString(Constants.UNIQUE_ID,""));


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

                manager2 = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager2);
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

        manager2 = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager2);
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

                list = db.getUserTags(pref.getString(Constants.UNIQUE_ID,""));

                manager2 = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager2);
                adapter = new TagsVerticalAdapter(getActivity(),list);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
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

    public String getStringImagePost(Bitmap bmp){
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

    private void showFileChooserPost() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
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
