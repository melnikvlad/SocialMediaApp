package com.example.vlad.scruji.Main_Screen_With_Tabs.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.Tags;
import com.example.vlad.scruji.R;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class ToDoRealmAdapter
        extends RealmBasedRecyclerViewAdapter<Tags, ToDoRealmAdapter.ViewHolder> {

    public class ViewHolder extends RealmViewHolder {

        public TextView todoTextView;
        public ViewHolder(FrameLayout container) {
            super(container);
            this.todoTextView = (TextView) container.findViewById(R.id.todo_text_view);
        }
    }

    public ToDoRealmAdapter(
            Context context,
            RealmResults<Tags> realmResults,
            boolean automaticUpdate,
            boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.tag, viewGroup, false);
        ViewHolder vh = new ViewHolder((FrameLayout) v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final Tags toDoItem = realmResults.get(position);
        viewHolder.todoTextView.setText(toDoItem.getDescription());
    }
}