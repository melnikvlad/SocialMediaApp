package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Fragments.UsersWithEqualTagsFragment;
import com.example.vlad.scruji.Interfaces.DeleteTagInterface;
import com.example.vlad.scruji.Interfaces.InsertTagInterface;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Tag;
import com.example.vlad.scruji.R;
import com.example.vlad.scruji.SQLite.MyDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mDataSet;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tag;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view.findViewById(R.id.card_view);
            tag = (TextView)view.findViewById(R.id.tag);
        }
    }

    public TagsAdapter(Context mContext, List<String> mDataSet) {
        this.mContext = mContext;
        this.mDataSet = mDataSet;
    }

    @Override
    public TagsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TagsAdapter.ViewHolder holder, final int position) {

        holder.tag.setText(mDataSet.get(position).toString());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getPreferences();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.TAG_ONCLICK,holder.tag.getText().toString());
                editor.apply();
                goToSettingsFragment();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private void goToSettingsFragment(){
        UsersWithEqualTagsFragment fragment = new UsersWithEqualTagsFragment();
        FragmentManager fragmentManager = ((MainActivity)mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_frame,fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
    }

    public Context getActivityContex(){
        Context applicationContext = MainActivity.getContextOfApplication();
        return applicationContext;
    }

    public SharedPreferences getPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivityContex());
        return prefs;
    }
}
