package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Fragments.UsersWithEqualTagsFragment;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.R;

import java.util.ArrayList;
import java.util.List;


public class TagsVerticalAdapter extends RecyclerView.Adapter<TagsVerticalAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mDataSet;
    private List<String> filtered;

    public TagsVerticalAdapter(Context mContext, List<String> mDataSet) {
        this.mContext = mContext;
        this.mDataSet = mDataSet;
        filtered = new ArrayList<>();
        this.filtered.addAll(this.mDataSet);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_for_vertical_rv_tags,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.tag.setText(filtered.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getPreferences();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.TAG_ONCLICK,holder.tag.getText().toString());
                editor.apply();

            }
        });
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
            filtered.addAll(mDataSet);
        }
        else {
            // Iterate in the original List and add it to filter list...
            for (int i=0;i<mDataSet.size();i++) {
                if (mDataSet.get(i).toLowerCase().contains(text.toLowerCase())) {
                    // Adding Matched items
                    filtered.add(mDataSet.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public String getTag(int position){
        return filtered.get(position);
    }

    public void deleteItem(int position) {
        filtered.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    private void goToUsersFragment(){
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tag;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            tag = (TextView) view.findViewById(R.id.tag);
        }
    }
}
