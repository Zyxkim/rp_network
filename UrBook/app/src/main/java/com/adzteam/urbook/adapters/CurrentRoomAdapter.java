package com.adzteam.urbook.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.general.ui.friends.UserActivity;
import com.adzteam.urbook.general.ui.profile.CharacterActivity;
import com.adzteam.urbook.room.RoomActivity;
import com.adzteam.urbook.room.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import static com.adzteam.urbook.adapters.RoomsAdapter.CURRENT_ROOM_ID;

public class CurrentRoomAdapter extends RecyclerView.Adapter<CurrentRoomAdapter.MyViewHolder> {

    private ArrayList<Message> mPostsList;
    private Context mContext;
    public static String CURRENT_USER_ID;

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

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!c.getCreator().equals(mAuth.getCurrentUser().getUid())) {
                    CURRENT_USER_ID = c.getCreator();
                    Log.d("ItemClick", CURRENT_USER_ID);
                    Intent intent = new Intent(mContext, UserActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });

        holder.mMessageContent.setText(c.getContent());
    }

    @Override
    public int getItemCount() {
        Log.d("RV", "Item size ["+mPostsList.size()+"]");
        return mPostsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
        return new MyViewHolder(v);
    }
}

