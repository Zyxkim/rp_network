package com.adzteam.urbook.general.ui.friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.CurrentRoomAdapter;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.UserCharactersAdapter;
import com.adzteam.urbook.adapters.PostsAdapter;
import com.adzteam.urbook.general.GeneralActivity;
import com.adzteam.urbook.general.ui.profile.EditProfileActivity;
import com.adzteam.urbook.general.ui.profile.ProfileViewModel;
import com.adzteam.urbook.room.RoomActivity;
import com.adzteam.urbook.room.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.adzteam.urbook.adapters.CurrentRoomAdapter.CURRENT_USER_ID;
import static com.adzteam.urbook.adapters.RoomsAdapter.CURRENT_ROOM_ID;

public class UserActivity extends AppCompatActivity {

    private ActionMenuItemView mBtGoBack;
    private ActionMenuItemView mAdd;

    private CircleImageView mProfileImage;
    private StorageReference mStorageReference;

    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final PostsAdapter mPostsAdapter = new PostsAdapter(mPostsData);

    private final ArrayList<Characters> mCharactersData = new ArrayList<>();
    private final UserCharactersAdapter mCharacterAdapter = new UserCharactersAdapter(mCharactersData);

    public UserActivity() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_user, container, false);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        FirebaseFirestore mFStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = mFStore.collection("users").document(CURRENT_USER_ID);
        TextView mName = findViewById(R.id.user_name);
        TextView mStatus = findViewById(R.id.user_status);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    mName.setText(documentSnapshot.getString("name"));
                    mStatus.setText(documentSnapshot.getString("status"));
                }
            }
        });

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        rv.setAdapter(mPostsAdapter);

        RecyclerView rvc = findViewById(R.id.characters_view);
        rvc.setHasFixedSize(true);
        rvc.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        rvc.setAdapter(mCharacterAdapter);

        mProfileImage = findViewById(R.id.user_image);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = mStorageReference.child("users/" + CURRENT_USER_ID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfileImage);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        mPostsData.clear();
                        for (QueryDocumentSnapshot document : value) {
                            String creator = (String) document.get("creator");

                            if (creator.equals(CURRENT_USER_ID)) {
                                String date = (String) document.get("date");
                                String name = (String) document.get("name");
                                String characterName = (String) document.get("characterName");
                                String content = (String) document.get("content");

                                Post newPost = new Post(document.getId(), Long.parseLong(date), name, creator, characterName, content);
                                mPostsData.add(newPost);
                            }
                            mPostsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


        db.collection("characters").orderBy("fandom", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        mCharactersData.clear();
                        for (QueryDocumentSnapshot document : value) {
                            String creator = (String) document.get("creator");

                            if (creator.equals(CURRENT_USER_ID)) {
                                String date = (String) document.get("date");
                                String fandom = (String) document.get("fandom");
                                String name = (String) document.get("name");
                                String characterName = (String) document.get("characterName");
                                String characterSurname = (String) document.get("characterSurname");
                                String content = (String) document.get("content");

                                Boolean isThereImage;
                                isThereImage = document.getBoolean("thereImage");
                                if (isThereImage == null) isThereImage =false;
                                Log.i("eee", characterName +" "+ String.valueOf(isThereImage));

                                Characters newCharacter = new Characters(document.getId(), Long.parseLong(date), name, creator, fandom, characterName, characterSurname, content, isThereImage);
                                mCharactersData.add(newCharacter);
                            }
                            mCharacterAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        mBtGoBack = findViewById(R.id.back);
        mBtGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAdd = findViewById(R.id.subscribe);

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("subscriptions").document(CURRENT_USER_ID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mAdd.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Log.d("Subscribiton", "Failed connecting database: ", task.getException());
                }
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralActivity.hasConnection(view.getContext())) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> data = new HashMap<>();
                    data.put("id", CURRENT_USER_ID);

                    db.collection("users")
                            .document(mAuth.getCurrentUser().getUid())
                            .collection("subscriptions").document(CURRENT_USER_ID).set(data);

                    data.clear();
                    data.put("id", mAuth.getCurrentUser().getUid());

                    db.collection("users")
                            .document(CURRENT_USER_ID)
                            .collection("subscribers").document(mAuth.getCurrentUser().getUid()).set(data);

                    DocumentReference docRef = db.collection("users").document(CURRENT_USER_ID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Long subs = (Long) document.get("subs");
                                    if (subs == null) {
                                        data.clear();
                                        data.put("subs", 1);
                                        db.collection("users").document(CURRENT_USER_ID).update(data);
                                    } else {
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("subs", FieldValue.delete());
                                        db.collection("users").document(CURRENT_USER_ID).update(updates);
                                        data.clear();
                                        data.put("subs", subs + 1);
                                        db.collection("users").document(CURRENT_USER_ID).update(data);
                                    }
                                }
                            } else {
                                Log.d("Error", "Failed with subs counter!", task.getException());
                            }
                        }
                    });

                    Toast.makeText(getApplicationContext(), "Subscription added!", Toast.LENGTH_SHORT).show();

                    mAdd.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(view.getContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}