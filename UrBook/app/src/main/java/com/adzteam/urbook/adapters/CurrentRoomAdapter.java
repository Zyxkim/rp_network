package com.adzteam.urbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.room.model.Message;

import java.util.ArrayList;

public class CurrentRoomAdapter extends RecyclerView.Adapter<CurrentRoomAdapter.MyViewHolder> {

    private ArrayList<Message> mPostsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mDate;
        public TextView mPostName;
        public TextView mDescription;

        public MyViewHolder(View view) {
            super(view);
            mDate = view.findViewById(R.id.message_date);
            mPostName = view.findViewById(R.id.message_user);
            mDescription = view.findViewById(R.id.message_content);
        }
    }

    public CurrentRoomAdapter(ArrayList<Message> mPostsList) {
        this.mPostsList = mPostsList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Message c = mPostsList.get(position);
        holder.mDate.setText(c.getDate());
        holder.mPostName.setText(c.getCreator());
        holder.mDescription.setText(c.getContent());
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mPostsList.size()+"]");
        return mPostsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
        return new MyViewHolder(v);
    }
}

