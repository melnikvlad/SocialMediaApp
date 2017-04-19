package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UserOtherPhoto;
import com.example.vlad.scruji.Models.UsersWithEqualTags;
import com.example.vlad.scruji.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class OtherPhotosAdapter extends RecyclerView.Adapter<OtherPhotosAdapter.ViewHolder> {
    private ArrayList<UserOtherPhoto> photos;
    private Context context;

    public OtherPhotosAdapter(Context context,ArrayList<UserOtherPhoto> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public OtherPhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_for_photos, viewGroup, false);
        return new OtherPhotosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context).load(PhotoUrl(photos.get(position).getPhoto()))
                .into(holder.img_android);
    }

    private String PhotoUrl(String name) {
        SharedPreferences pref;
        pref = getPreferences();
        return "http://10.0.2.2/server/uploads/other/"+pref.getString(Constants.UNIQUE_ID,"")+"/"+name+".png";
    }
    public Context getActivityContex(){
        Context applicationContext = MainActivity.getContextOfApplication();
        return applicationContext;
    }

    public SharedPreferences getPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivityContex());
        return prefs;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img_android;
        public ViewHolder(View view) {
            super(view);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }
    }
}
