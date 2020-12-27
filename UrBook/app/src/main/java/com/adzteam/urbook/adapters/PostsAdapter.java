package com.adzteam.urbook.adapters;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    private ArrayList<Post> mPostsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mDate;
        public TextView mUserName;
        public TextView mPostName;
        public TextView mDescription;
        public ImageView mPostImg;

        public MyViewHolder(View view) {
            super(view);
            mDate = view.findViewById(R.id.post_date);
            mUserName = view.findViewById(R.id.post_user);
            mPostName = view.findViewById(R.id.post_name);
            mDescription = view.findViewById(R.id.post_description);
            mPostImg = (ImageView) view.findViewById(R.id.post_image);
        }
    }

    public PostsAdapter(ArrayList<Post> mPostsList) {
        this.mPostsList = mPostsList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Post c = mPostsList.get(position);

        if (c.isThereImage()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("posts/" + c.getId() + "/image.jpg");
            Log.i("rrr", String.valueOf(profileRef.getDownloadUrl()));
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.mPostImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("f", "Ooops");
                }
            });
        }

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm 'by'");

        long milliSeconds= Long.parseLong(c.getDate());
        System.out.println(milliSeconds);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        holder.mDate.setText(formatter.format(calendar.getTime()));
        holder.mUserName.setText(c.getName());
        holder.mPostName.setText(c.getCharacterName());
        holder.mDescription.setText(c.getContent());
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
