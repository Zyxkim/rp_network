package com.adzteam.urbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.room.RoomActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.MyViewHolder> {

    private ArrayList<Room> mRoomList;
    private Context mContext;
    public static String CURRENT_ROOM_ID;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mRoomName;
        public TextView mRoomDescription;
        public ImageView mRoomImg;

        public MyViewHolder(View view) {
            super(view);
            mRoomName = (TextView) view.findViewById(R.id.room_name);
            mRoomDescription = (TextView) view.findViewById(R.id.room_description);
            mRoomImg = (ImageView) view.findViewById(R.id.room_image);
        }
    }

    public RoomsAdapter(ArrayList<Room> mRoomList) {
        this.mRoomList = mRoomList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Room c = mRoomList.get(position);
        holder.mRoomName.setText(c.getName());
        holder.mRoomDescription.setText(c.getDescription());

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = mStorageReference.child("rooms/" + c.getId() + "/image.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.mRoomImg);
            }
        });

        holder.mRoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_ROOM_ID = c.getId();
                Log.d("ItemClick", CURRENT_ROOM_ID);
                Intent intent = new Intent(mContext, RoomActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mRoomList.size()+"]");
        return mRoomList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rooms_item, parent, false);
        return new MyViewHolder(v);
    }
}
