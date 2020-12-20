package com.adzteam.urbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.room.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        //Date d = new Date(Long.parseLong(c.getDate()) * 1000);
        //SimpleDateFormat sdfr = new SimpleDateFormat("dd.mm.yyyy");

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        long milliSeconds= Long.parseLong(c.getDate());
        System.out.println(milliSeconds);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        //System.out.println(formatter.format(calendar.getTime()));

        holder.mDate.setText(formatter.format(calendar.getTime()));
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

