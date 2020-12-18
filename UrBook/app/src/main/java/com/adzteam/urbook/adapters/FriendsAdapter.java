package com.adzteam.urbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private ArrayList<Friend> mFriendsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mFriendName;
        public TextView mFriendStatus;

        public MyViewHolder(View view) {
            super(view);
            mFriendName = (TextView) view.findViewById(R.id.friend_name);
            mFriendStatus = (TextView) view.findViewById(R.id.friend_status);
        }
    }

    public FriendsAdapter(ArrayList<Friend> mFriendsList) {
        this.mFriendsList = mFriendsList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Friend c = mFriendsList.get(position);
        holder.mFriendName.setText(c.name);
        holder.mFriendStatus.setText(c.status);
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mFriendsList.size()+"]");
        return mFriendsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new MyViewHolder(v);
    }
}
