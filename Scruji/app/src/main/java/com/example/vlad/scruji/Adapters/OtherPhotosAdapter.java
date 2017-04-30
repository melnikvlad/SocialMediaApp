package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.UserOtherPhoto;
import com.example.vlad.scruji.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OtherPhotosAdapter extends RecyclerView.Adapter<OtherPhotosAdapter.ViewHolder> {
    private ArrayList<UserOtherPhoto> photos;
    private Context context;

    public OtherPhotosAdapter(Context context,ArrayList<UserOtherPhoto> photos) {
        this.context = context;
        this.photos = photos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img_android;
        public ViewHolder(View view) {
            super(view);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }
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

    @Override
    public int getItemCount() {
        return photos.size();
    }

    private String PhotoUrl(String name) {
        return Constants.PICASSO_OTHER+name+".png";
    }
    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }

}
