package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Fragments.OtherUserProfile;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UserResponse;
import com.example.vlad.scruji.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<UserResponse> users;
    private Context context;
    private ArrayList<UserResponse> filtered;

    public UsersAdapter(Context context,ArrayList<UserResponse> users) {
        this.context = context;
        this.users = users;
        this.filtered = new ArrayList<>();
        this.filtered.addAll(this.users);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_android,tv2_android;
        ImageView img_android;
        CardView cardView;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view.findViewById(R.id.card_for_list_of_users);
            tv_android = (TextView)view.findViewById(R.id.tv_android);
            tv2_android = (TextView)view.findViewById(R.id.tv2_android);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_for_users, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getPreferences();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.TEMP_ID,filtered.get(i).getId());
                editor.apply();
                goToOtherUserProfile();
            }
        });
        viewHolder.tv_android.setText(filtered.get(i).getName()+", "+filtered.get(i).getAge()+" y.o.");
        viewHolder.tv2_android.setText(filtered.get(i).getCountry()+", "+filtered.get(i).getCity());
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(context).load(IconUrl(filtered.get(i).getId())).transform(transformation).into(viewHolder.img_android);
    }

    private String IconUrl(String id) {
        return Constants.PICASSO_MAIN +id+".png";
    }

    @Override
    public int getItemCount() {
        return (null != filtered ? filtered.size() : 0);
    }

    // Do Search...
    public void filter(final String text) {
        filtered.clear();

        // If there is no search value, then add all original list items to filter list
        if (TextUtils.isEmpty(text)) {
            filtered.addAll(users);
        }
        else {
            // Iterate in the original List and add it to filter list...
            for (int i=0;i<users.size();i++) {
                if (users.get(i).getName().toLowerCase().contains(text.toLowerCase())) {
                    // Adding Matched items
                    filtered.add(users.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }

    private void goToOtherUserProfile() {
        OtherUserProfile fragment = new OtherUserProfile();
        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_frame, fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
    }
}
