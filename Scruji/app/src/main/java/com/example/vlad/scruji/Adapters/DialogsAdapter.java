package com.example.vlad.scruji.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;


public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.ViewHolder> {
    private ArrayList<String> names;
    private ArrayList<String> messages;
    private ArrayList<String> ids;
    private Context context;
    private ArrayList<String> filtered;

    public DialogsAdapter(Context context, ArrayList<String> ids, ArrayList<String> names, ArrayList<String> messages) {
        this.names = names;
        this.messages = messages;
        this.ids = ids;
        this.context = context;
        this.filtered = new ArrayList<>();
        this.filtered.addAll(this.names);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,message;
        ImageView img_android;
        public ViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.name);
            message = (TextView)view.findViewById(R.id.message);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }
    }

    @Override
    public DialogsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_for_dialog_item, parent, false);
        return new DialogsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DialogsAdapter.ViewHolder holder, int i) {
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences prefs = getPreferences();
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putString(Constants.TEMP_ID,filtered.get(i).getId());
//                editor.apply();
//                goToOtherUserProfile();
//            }
//        });

        holder.name.setText(filtered.get(i));
        holder.message.setText(messages.get(i));
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(context).load(IconUrl(ids.get(i))).transform(transformation).into(holder.img_android);
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
            filtered.addAll(names);

        }
        else {
            // Iterate in the original List and add it to filter list...
            for (int i=0;i<names.size();i++) {
                if (names.get(i).toLowerCase().contains(text.toLowerCase())) {
                    // Adding Matched items
                    filtered.add(names.get(i));

                }
            }
        }
        notifyDataSetChanged();
    }
}
