package com.adzteam.urbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;

import java.util.ArrayList;
import java.util.Date;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    private ArrayList<Post> mPostsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mDate;
        public TextView mDescription;

        public MyViewHolder(View view) {
            super(view);
            mDate = (TextView) view.findViewById(R.id.post_date);
            mDescription = (TextView) view.findViewById(R.id.post_description);
        }
    }

    public PostsAdapter(ArrayList<Post> mPostsList) {
        this.mPostsList = mPostsList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Post c = mPostsList.get(position);
        holder.mDate.setText(java.text.DateFormat.getDateTimeInstance().format(new Date()));
        holder.mDescription.setText(c.description);
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mPostsList.size()+"]");
        return mPostsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new MyViewHolder(v);
    }
}
