package com.adzteam.urbook.adapters;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.room.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CurrentRoomAdapter extends RecyclerView.Adapter<CurrentRoomAdapter.MyViewHolder> {

    private ArrayList<Message> mPostsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mDate;
        public TextView mUserName;
        public TextView mMessageContent;

        public MyViewHolder(View view) {
            super(view);
            mDate = view.findViewById(R.id.message_date);
            mUserName = view.findViewById(R.id.message_user);
            mMessageContent = view.findViewById(R.id.message_content);
        }
    }

    public CurrentRoomAdapter(ArrayList<Message> mPostsList) {
        this.mPostsList = mPostsList;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Message c = mPostsList.get(position);

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm 'from'");

        long milliSeconds= Long.parseLong(c.getDate());
        System.out.println(milliSeconds);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (c.getCreator().equals(mAuth.getCurrentUser().getUid())){
            LinearLayout.LayoutParams para=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            para.gravity = Gravity.END;
            holder.mMessageContent.setLayoutParams(para);
        } else {
            LinearLayout.LayoutParams para=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            para.gravity = Gravity.START;
            holder.mMessageContent.setLayoutParams(para);
        }

        holder.mDate.setText(formatter.format(calendar.getTime()));
        holder.mUserName.setText(c.getName());
        holder.mMessageContent.setText(c.getContent());
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

