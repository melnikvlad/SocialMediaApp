package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Post;
import com.example.vlad.scruji.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> mDataSet;
    private String username;

    public PostsAdapter(Context mContext, List<Post> mDataSet,String user) {
        this.mContext = mContext;
        this.mDataSet = mDataSet;
        this.username = user;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user,date,description;
        CircularImageView user_icon;
        ImageView photo;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view.findViewById(R.id.post);
            user = (TextView)view.findViewById(R.id.user);
            date = (TextView)view.findViewById(R.id.date);
            description = (TextView)view.findViewById(R.id.description);
            user_icon = (CircularImageView)view.findViewById(R.id.user_pic);
            photo = (ImageView)view.findViewById(R.id.photo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_for_posts, parent, false);
        return new PostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(mContext)
                .load(IconUrl(mDataSet.get(position).getUserId()))
                .transform(transformation)
                .into(holder.user_icon);

        holder.user.setText(username);
        holder.date.setText(mDataSet.get(position).getDate());
        holder.description.setText(mDataSet.get(position).getDescription());

        if(mDataSet.get(position).getPhoto() == ""){
            holder.photo.setVisibility(View.GONE);
        }
        else{
            Picasso.with(mContext)
                    .load(PhotoPost(mDataSet.get(position).getPhoto()))
                    .into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private String IconUrl(String id) {
        return Constants.PICASSO_MAIN +id+".png";
    }

    private String PhotoPost(String name) {
        SharedPreferences pref;
        pref = getPreferences();
        String res = Constants.PICASSO_POSTS+pref.getString(Constants.UNIQUE_ID,"")+"/"+name+".png";
        return res;
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }
}
