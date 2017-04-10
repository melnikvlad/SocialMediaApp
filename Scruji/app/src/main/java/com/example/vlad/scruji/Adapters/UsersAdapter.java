package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Models.UsersWithEqualTags;
import com.example.vlad.scruji.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<UsersWithEqualTags> users;
    private Context context;

    public UsersAdapter(Context context,ArrayList<UsersWithEqualTags> users) {
        this.context = context;
        this.users = users;

    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_for_users, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tv_android.setText(users.get(i).getName()+" "+ users.get(i).getLastname()+", "+users.get(i).getAge()+" y.o.");
        viewHolder.tv2_android.setText(users.get(i).getCountry()+", "+users.get(i).getCity());
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(context).load(IconUrl(users.get(i).getId())).transform(transformation).into(viewHolder.img_android);
    }

    private String IconUrl(String id) {
        return Constants.PICASSO_URL+id+".png";
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_android,tv2_android;
        ImageView img_android;
        public ViewHolder(View view) {
            super(view);
            tv_android = (TextView)view.findViewById(R.id.tv_android);
            tv2_android = (TextView)view.findViewById(R.id.tv2_android);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }
    }
}
