package com.adzteam.urbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.general.GeneralActivity;
import com.adzteam.urbook.general.ui.friends.FriendsViewModel;
import com.adzteam.urbook.general.ui.friends.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.adzteam.urbook.adapters.CurrentRoomAdapter.CURRENT_USER_ID;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private final ArrayList<Friend> mRoomList;
    private Context mContext;
    private final FriendsViewModel mViewModel;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public final TextView mFriendName;
        public final TextView mFriendStatus;
        public final CircleImageView mFriendImage;
        public final ImageButton mDeleteBtn;

        public String mImgUri;

        public MyViewHolder(View view) {
            super(view);
            mDeleteBtn = view.findViewById(R.id.button_delete);
            mFriendName = view.findViewById(R.id.friend_name);
            mFriendStatus = view.findViewById(R.id.friend_status);
            mFriendImage = view.findViewById(R.id.friend_image);
        }
    }

    public FriendsAdapter(ArrayList<Friend> mRoomList, FriendsViewModel viewModel) {
        this.mRoomList = mRoomList;
        mViewModel = viewModel;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind ["+holder+"] - Pos ["+position+"]");
        Friend c = mRoomList.get(position);
        holder.mFriendName.setText(c.getName());
        holder.mFriendStatus.setText(c.getStatus());
        holder.mImgUri = c.getFriendImg();

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = mStorageReference.child("users/" + c.getId() + "/profile.jpg");
        Picasso.get().load(holder.mImgUri).into((holder.mFriendImage));
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.mFriendImage));

        holder.mDeleteBtn.setOnClickListener(view -> {
            if (GeneralActivity.hasConnection(view.getContext())) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("subscriptions").document(c.getId()).delete();
                db.collection("users").document(c.getId()).collection("subscribers").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete();
                Toast.makeText(view.getContext(), "Please, refresh page.", Toast.LENGTH_SHORT).show();
                mRoomList.remove(position);

                DocumentReference docRef = db.collection("users").document(c.getId());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Long subs = (Long) document.get("subs");
                                Map<String,Object> updates = new HashMap<>();
                                updates.put("subs", FieldValue.delete());
                                db.collection("users").document(c.getId()).update(updates);
                                updates.clear();
                                updates.put("subs", subs - 1);
                                db.collection("users").document(c.getId()).update(updates);
                                }
                            }
                        }
                    });
            } else {
                Toast.makeText(view.getContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.mFriendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_USER_ID = c.getId();
                Log.d("ItemClick", CURRENT_USER_ID);
                Intent intent = new Intent(mContext, UserActivity.class);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new MyViewHolder(v);
    }
}

