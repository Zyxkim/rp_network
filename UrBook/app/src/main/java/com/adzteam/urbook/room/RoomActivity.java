package com.adzteam.urbook.room;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adzteam.urbook.adapters.CurrentRoomAdapter;
import com.adzteam.urbook.general.ui.profile.ProfileViewModel;
import com.adzteam.urbook.room.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.adzteam.urbook.R;

import java.util.ArrayList;

import static com.adzteam.urbook.adapters.RoomsAdapter.CURRENT_ROOM_ID;

public class RoomActivity extends AppCompatActivity {

    private ProfileViewModel mPostsViewModel;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Message> mMessagesData = new ArrayList<>();
    private final CurrentRoomAdapter mAdapter = new CurrentRoomAdapter(mMessagesData);

    private MaterialToolbar mToolbar;
    private ActionMenuItemView mBtGoBack;

    private EditText mMessageContent;
    private ImageButton mSendBtn;

    private String mUserName;

    public RoomActivity() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_room, container, false);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db.collection("rooms")
            .document(CURRENT_ROOM_ID)
            .collection("messages");

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        rv.setAdapter(mAdapter);

        mMessageContent = findViewById(R.id.message_input);
        mSendBtn = findViewById(R.id.button_send);

        db.collection("rooms")
                    .document(CURRENT_ROOM_ID)
                    .collection("messages")
                .orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                System.err.println("Listen failed: " + error);
                                return;
                            }

                            for (DocumentChange dc : value.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    mMessagesData.clear();
                                    for (QueryDocumentSnapshot document : value) {
                                        String creator = (String) document.get("creator");
                                        String name = (String) document.get("name");
                                        String date = (String) document.get("date");
                                        String content = (String) document.get("content");
                                        Log.i("Done", "oh yeah :^)");

                                        Message newPost = new Message(Long.parseLong(date), creator, name, content);
                                        mMessagesData.add(newPost);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    rv.smoothScrollToPosition(rv.getAdapter().getItemCount()-1);
                                }
                            }
                        }
                    });

        mSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(mMessageContent.getText().toString().isEmpty())){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    db.collection("users")
                            .document(mAuth.getCurrentUser().getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                mUserName = (String) document.get("name");
                                String err = (mUserName == null) ? "Failed" : mUserName;
                                Log.i("Name", err);
                                CollectionReference collectionReference = db.collection("rooms")
                                        .document(CURRENT_ROOM_ID)
                                        .collection("messages");

                                Message newPost = new Message(System.currentTimeMillis(), mAuth.getCurrentUser().getUid(), mUserName, mMessageContent.getText().toString().trim());
                                collectionReference.add(newPost);
                                mMessageContent.setText(null);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Empty Message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mToolbar = (MaterialToolbar)findViewById(R.id.room_bar);
        FirebaseFirestore.getInstance()
                        .collection("rooms")
                        .document(CURRENT_ROOM_ID)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    String name = (String) document.get("name");
                                    mToolbar.setTitle(name);
                                }
                            }
                        });

        mBtGoBack = (ActionMenuItemView) findViewById(R.id.home);
        mBtGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.room_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                mMessagesData.clear();
                mAdapter.notifyDataSetChanged();
                downloadPosts(new RefreshCallBack());
            }
        });
    }

    public void downloadPosts(final RoomActivity.RefreshCallBack callBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("rooms");
        collectionReference.document(CURRENT_ROOM_ID).collection("messages")
                .orderBy("date", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String creator = (String) document.get("creator");
                                String name = (String) document.get("name");
                                String date = (String) document.get("date");
                                String content = (String) document.get("content");
                                Log.i("Done", "oh yeah :^)");

                                Message newPost = new Message(Long.parseLong(date), creator, name, content);
                                mMessagesData.add(newPost);
                            }
                            mAdapter.notifyDataSetChanged();
                            if (callBack != null) {
                                callBack.stopRefreshing();
                            }
                        } else {
                            Log.i("RoomActivity", "oh no :^(");
                        }
                    }
                });
    }

    public class RefreshCallBack {
        public void stopRefreshing() {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
