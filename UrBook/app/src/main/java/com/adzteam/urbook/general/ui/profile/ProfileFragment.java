package com.adzteam.urbook.general.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.UserCharactersAdapter;
import com.adzteam.urbook.adapters.UserPostsAdapter;

import com.adzteam.urbook.general.ui.subscribers.SubsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.adzteam.urbook.general.GeneralActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mProfileViewModel;
    private ActionMenuItemView mLogOutBtn;
    private ActionMenuItemView mEditProfileBtn;
    private CircleImageView mProfileImage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;

    private ImageButton mNewPostBtn;
    private ImageButton mNewCharacterBtn;
    
    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final UserPostsAdapter mPostsAdapter = new UserPostsAdapter(mPostsData);

    private final ArrayList<Characters> mCharactersData = new ArrayList<>();
    private final UserCharactersAdapter mCharacterAdapter = new UserCharactersAdapter(mCharactersData);
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLogOutBtn = view.findViewById(R.id.logout);
        mEditProfileBtn = view.findViewById(R.id.edit);

        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileViewModel.signOut();
                Log.i("ggg", "rrr");
                ((GeneralActivity) getActivity()).replaceWithAuthActivity();
            }
        });
        FirebaseFirestore mFStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = mFStore.collection("users").document(mAuth.getCurrentUser().getUid());
        TextView mName = view.findViewById(R.id.profile_name);
        TextView mStatus = view.findViewById(R.id.profile_status);
        TextView mSubs = view.findViewById(R.id.profile_subs);
        docRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    mName.setText(documentSnapshot.getString("name"));
                    mStatus.setText(documentSnapshot.getString("status"));
                }
            }
        });
        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("name", mName.getText().toString());
                intent.putExtra("status", mStatus.getText().toString());
                startActivity(intent);
            }
        });

        mSubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubsActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        rv.setAdapter(mPostsAdapter);

        RecyclerView rvc = view.findViewById(R.id.characters_view);
        rvc.setHasFixedSize(true);
        rvc.setLayoutManager(new GridLayoutManager(view.getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        rvc.setAdapter(mCharacterAdapter);

        mNewPostBtn = view.findViewById(R.id.add_feed);
        mNewPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GeneralActivity) getActivity()).replaceWithCreatePostActivity();
            }
        });

        mNewCharacterBtn = view.findViewById(R.id.add_character);
        mNewCharacterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GeneralActivity) getActivity()).replaceWithCreateCharacterActivity();
            }
        });

        mProfileImage = view.findViewById(R.id.profile_image);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        StorageReference profileRef = mStorageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
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

                            if (creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                String date = (String) document.get("date");
                                //String id = (String) document.get("id");
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

                            if (creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}