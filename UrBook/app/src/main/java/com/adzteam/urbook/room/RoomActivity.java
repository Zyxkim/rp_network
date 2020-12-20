package com.adzteam.urbook.room;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adzteam.urbook.adapters.CurrentRoomAdapter;
import com.adzteam.urbook.general.ui.feed.FeedFragment;
import com.adzteam.urbook.general.ui.profile.ProfileViewModel;
import com.adzteam.urbook.general.ui.rooms.RoomsFragment;
import com.adzteam.urbook.room.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stfalcon.chatkit.messages.MessageInput;
import com.adzteam.urbook.R;

import java.util.ArrayList;

import static com.adzteam.urbook.adapters.RoomsAdapter.CURRENT_ROOM_ID;

public class RoomActivity extends AppCompatActivity {

    /*public static void open(Context context) {
        context.startActivity(new Intent(context, RoomActivity.class));
    }*/

    private ProfileViewModel mPostsViewModel;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Message> mPostsData = new ArrayList<>();
    private final CurrentRoomAdapter mAdapter = new CurrentRoomAdapter(mPostsData);

    private MaterialToolbar mToolbar;
    private ActionMenuItemView mBtGoBack;

    private EditText mMessageContent;
    private ImageButton mSendBtn;

    public RoomActivity() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_feed, container, false);
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

        mSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                CollectionReference collectionReference = db.collection("rooms")
                        .document(CURRENT_ROOM_ID)
                        .collection("messages");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                Message newPost = new Message(Timestamp.now(), mAuth.getCurrentUser().getUid(), mMessageContent.getText().toString().trim());
                collectionReference.add(newPost);
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

        if(savedInstanceState == null) {
            downloadPosts(null);
        }

        mSwipeRefreshLayout = findViewById(R.id.room_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                mPostsData.clear();
                mAdapter.notifyDataSetChanged();
                downloadPosts(new RefreshCallBack());
            }
        });
    }

    public void downloadPosts(final RoomActivity.RefreshCallBack callBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.document(CURRENT_ROOM_ID).collection("messages")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String creator = (String) document.get("creator");
                                Timestamp date = (Timestamp) document.get("date");
                                String content = (String) document.get("content");
                                Log.i("Done", "oh yeah :^)");

                                Message newPost = new Message(date, creator, content);
                                mPostsData.add(newPost);
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
