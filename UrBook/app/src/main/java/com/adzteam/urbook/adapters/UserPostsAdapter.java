package com.adzteam.urbook.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.general.GeneralActivity;
import com.adzteam.urbook.general.ui.profile.ProfileFragment;
import com.adzteam.urbook.room.RoomActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserPostsAdapter extends RecyclerView.Adapter<UserPostsAdapter.MyViewHolder> {

    private ArrayList<Post> mPostsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mDate;
        public TextView mUserName;
        public TextView mPostName;
        public TextView mDescription;
        public ImageButton mDeleteBtn;
        public ImageView mPostImage;

        public MyViewHolder(View view) {
            super(view);
            mDate = view.findViewById(R.id.post_date);
            mDeleteBtn = view.findViewById(R.id.button_delete);
            mUserName = view.findViewById(R.id.post_user);
            mPostName = view.findViewById(R.id.post_name);
            mDescription = view.findViewById(R.id.post_description);
            mPostImage = view.findViewById(R.id.post_image);
        }
    }

    public UserPostsAdapter(ArrayList<Post> mPostsList) {
        this.mPostsList = mPostsList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Post c = mPostsList.get(position);

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm 'by'");

        long milliSeconds= Long.parseLong(c.getDate());
        System.out.println(milliSeconds);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        holder.mDate.setText(formatter.format(calendar.getTime()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(c.getCreator());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    holder.mUserName.setText(documentSnapshot.getString("name"));
                }
            }
        });

        holder.mPostName.setText(c.getCharacterName());
        holder.mDescription.setText(c.getContent());

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralActivity.hasConnection(view.getContext())) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("posts").document(c.getId()).delete();
                    mPostsList.remove(position);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(view.getContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mPostsList.size()+"]");
        return mPostsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_item, parent, false);
        return new MyViewHolder(v);
    }
}
