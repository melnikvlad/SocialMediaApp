package com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Adapters.ToDoRealmAdapter;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.HomeModel;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.NewModel;
import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.Tags;
import com.example.vlad.scruji.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class Home extends Fragment{
    private SharedPreferences pref;
    CircularImageView roundedImageView;
    TextView name_lastname_age,country_city,sex,eye,hair;
    Realm realm;
    FloatingActionButton fab;





    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);
        pref = getActivity().getPreferences(0);
        realm = Realm.getDefaultInstance();

        roundedImageView = (CircularImageView) view.findViewById(R.id.imageView1);
        name_lastname_age = (TextView)view.findViewById(R.id.name_lastname_age);
        country_city = (TextView)view.findViewById(R.id.country_city);
        fab = (FloatingActionButton)view.findViewById(R.id.fab);


//        RealmResults<NewModel> results = realm.where(NewModel.class).findAll();
//        for(NewModel user : results){
//                Log.d("TAG","REALM "+ user.getName());
//                name_lastname_age.setText(user.getName() + " " + user.getLastname());
//
//            }

            RealmResults<HomeModel> results = realm.where(HomeModel.class).findAll();

            for(HomeModel user : results){
                Log.d("TAG","REALM "+ user.getName());
                name_lastname_age.setText(user.getName() + " " + user.getLastname() + " " + user.getAge()+" y.o.");
                country_city.setText(user.getCountry()+", "+user.getCity()+ "\n");
                sex.setText(user.getSex());
                eye.setText(user.getEye_clr());
                hair.setText(user.getHair_clr());

            }

//            RealmResults<Tags> toDoItems = realm.where(Tags.class).findAll();
//
//        ToDoRealmAdapter toDoRealmAdapter = new ToDoRealmAdapter(getActivity(), toDoItems, true, true);
//        RealmRecyclerView realmRecyclerView = (RealmRecyclerView) view.findViewById(R.id.realm_recycler_view);
//        realmRecyclerView.setAdapter(toDoRealmAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAndShowInputDialog();
            }
        });


        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        Picasso.with(getActivity().getApplication())
                .load("http://10.0.2.2/server/uploads/main/"+pref.getString(Constants.UNIQUE_ID,"key")+".png")
                .transform(transformation)
                .into(roundedImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","PICASSO image loaded");
                    }

                    @Override
                    public void onError() {
                        Log.d("TAG","PICASSO image failed");
                    }
                });

        return view;
    }

    private void buildAndShowInputDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create A Task");

        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialogView = li.inflate(R.layout.to_do_dialog_view, null);
        final EditText input = (EditText) dialogView.findViewById(R.id.input);

        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addToDoItem(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.show();
        input.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                (event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                            dialog.dismiss();
                            addToDoItem(input.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void addToDoItem(String toDoItemText) {
        if (toDoItemText == null || toDoItemText.length() == 0) {
            Toast
                    .makeText(getActivity(), "Empty ToDos don't get stuff done!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        realm.beginTransaction();
        Tags todoItem = realm.createObject(Tags.class);
        todoItem.setId(System.currentTimeMillis());
        todoItem.setDescription(toDoItemText);
        realm.commitTransaction();
    }
    @Override
    public void onStop() {
        super.onStop();
        // Remember to close the Realm instance when done with it.
        realm.close();
    }
}
