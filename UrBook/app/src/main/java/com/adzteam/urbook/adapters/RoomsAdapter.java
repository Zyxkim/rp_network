package com.adzteam.urbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.general.GeneralActivity;
import com.adzteam.urbook.general.ui.rooms.RoomsViewModel;
import com.adzteam.urbook.room.RoomActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.MyViewHolder> {

    private final ArrayList<Room> mRoomList;
    private Context mContext;
    private final RoomsViewModel mViewModel;
    public static String CURRENT_ROOM_ID;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public final TextView mRoomName;
        public final TextView mRoomDescription;
        public final ImageView mRoomImg;
        public final ImageButton mDeleteBtn;
        public String mImgUri;

        public MyViewHolder(View view) {
            super(view);
            mDeleteBtn = view.findViewById(R.id.button_delete);
            mRoomName = (TextView) view.findViewById(R.id.room_name);
            mRoomDescription = (TextView) view.findViewById(R.id.room_description);
            mRoomImg = (ImageView) view.findViewById(R.id.room_image);
        }
    }

    public RoomsAdapter(ArrayList<Room> mRoomList, RoomsViewModel viewModel) {
        this.mRoomList = mRoomList;
        mViewModel = viewModel;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Room c = mRoomList.get(position);
        holder.mRoomName.setText(c.getName());
        holder.mRoomDescription.setText(c.getDescription());
        holder.mImgUri = c.getRoomImg();

        if (mViewModel.isAuthorOf(c)) {
            holder.mDeleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.mDeleteBtn.setVisibility(View.INVISIBLE);
        }

        if (c.isThereImage()) {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("rooms/" + c.getId() + "/image.jpg");
            Picasso.get().load(holder.mImgUri).into((holder.mRoomImg));
            profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.mRoomImg));
        }

        holder.mRoomName.setOnClickListener(view -> {
            CURRENT_ROOM_ID = c.getId();
            Log.d("ItemClick", CURRENT_ROOM_ID);
            Intent intent = new Intent(mContext, RoomActivity.class);
            mContext.startActivity(intent);
        });

        holder.mDeleteBtn.setOnClickListener(view -> {
            if (GeneralActivity.hasConnection(view.getContext())) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("rooms").document(c.getId()).delete();
                Toast.makeText(view.getContext(), "Please, refresh page.", Toast.LENGTH_SHORT).show();
                mRoomList.remove(position);
            } else {
                Toast.makeText(view.getContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
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
