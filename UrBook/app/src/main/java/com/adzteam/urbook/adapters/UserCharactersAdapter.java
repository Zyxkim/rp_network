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

import java.util.ArrayList;

public class UserCharactersAdapter extends RecyclerView.Adapter<UserCharactersAdapter.MyViewHolder> {

    private ArrayList<Characters> mCharactersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mPostName;
        public ImageView mPostImage;

        public MyViewHolder(View view) {
            super(view);
            mPostName = view.findViewById(R.id.character_post_name);
            mPostImage = view.findViewById(R.id.character_post_image);
        }
    }

    public UserCharactersAdapter(ArrayList<Characters> mCharactersList) {
        this.mCharactersList = mCharactersList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Characters c = mCharactersList.get(position);

        if (c.isThereImage()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("characters/" + c.getId() + "/image.jpg");
            Log.i("rrr", String.valueOf(profileRef.getDownloadUrl()));
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.mPostImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("f", "Ooops");
                }
            });
        }


        holder.mPostName.setText(c.getCharacterName());
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mCharactersList.size()+"]");
        return mCharactersList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_item, parent, false);
        return new MyViewHolder(v);
    }
}
