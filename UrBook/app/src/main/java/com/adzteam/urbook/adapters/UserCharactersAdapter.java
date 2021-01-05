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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.general.ui.profile.CharacterActivity;
import com.adzteam.urbook.general.ui.profile.CreatePostActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserCharactersAdapter extends RecyclerView.Adapter<UserCharactersAdapter.MyViewHolder> {

    public static ArrayList<Characters> mCharactersList;
    public static int POSITION;
    private Context mContext;
    public static String CURRENT_CHARACTER_ID;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mPostName;
        public ImageView mCharacterImage;
        public CardView mCard;
        public String mImgUri;

        public MyViewHolder(View view) {
            super(view);
            mPostName = view.findViewById(R.id.character_post_name);
            mCharacterImage = view.findViewById(R.id.character_post_image);
            mCard = view.findViewById(R.id.cardView);
        }
    }

    public UserCharactersAdapter(ArrayList<Characters> mCharactersList) {
        this.mCharactersList = mCharactersList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        POSITION = position;
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Characters c = mCharactersList.get(position);

        holder.mImgUri = c.getCharacterImg();

        if (c.isThereImage()) {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("characters/" + c.getId() + "/image.jpg");
            Log.i("rrr", String.valueOf(profileRef.getDownloadUrl()));
            Picasso.get().load(holder.mImgUri).into(holder.mCharacterImage);
            Log.i("download", String.valueOf(holder.mImgUri));
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.mCharacterImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("f", "Ooops");
                }
            });
        }

        holder.mPostName.setText(c.getCharacterName());
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_CHARACTER_ID = c.getId();
                Log.d("ItemClick", CURRENT_CHARACTER_ID);
                Intent intent = new Intent(mContext, CharacterActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mCharactersList.size()+"]");
        return mCharactersList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_item, parent, false);
        return new MyViewHolder(v);
    }
}
