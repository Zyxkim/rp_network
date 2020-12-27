package com.adzteam.urbook.general.ui.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adzteam.urbook.R;

import java.util.ArrayList;

import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.PostsAdapter;
import com.adzteam.urbook.adapters.UserCharactersAdapter;
import com.adzteam.urbook.general.ui.profile.ProfileViewModel;
import com.adzteam.urbook.general.ui.rooms.RoomsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FeedFragment extends Fragment {

    private ProfileViewModel mPostsViewModel;
    
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final PostsAdapter mAdapter = new PostsAdapter(mPostsData);

    private final ArrayList<Characters> mCharactersData = new ArrayList<>();
    private final UserCharactersAdapter mCharacterAdapter = new UserCharactersAdapter(mCharactersData);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPostsViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        rv.setAdapter(mAdapter);

        RecyclerView rvc = view.findViewById(R.id.characters_view);
        rvc.setHasFixedSize(true);
        rvc.setLayoutManager(new GridLayoutManager(view.getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        rvc.setAdapter(mCharacterAdapter);

        if(savedInstanceState == null) {
            downloadPosts(null);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.feed_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                mPostsData.clear();
                mAdapter.notifyDataSetChanged();
                downloadPosts(new RefreshCallBack());
            }
        });
    }

    public void downloadPosts(final FeedFragment.RefreshCallBack callBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String creator = (String) document.get("creator");
                        if (!creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            String name = (String) document.get("name");
                            String date = (String) document.get("date");
                            String id = (String) document.get("id");
                            String characterName = (String) document.get("characterName");
                            String content = (String) document.get("content");

                            Boolean isThereImage;
                            isThereImage = document.getBoolean("thereImage");
                            if (isThereImage == null) isThereImage = false;
                            Log.i("eee", String.valueOf(isThereImage));

                            Post newPost = new Post(document.getId(), Long.parseLong(date), name, creator, characterName, content);
                            mPostsData.add(newPost);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if (callBack != null) {
                        callBack.stopRefreshing();
                    }
                } else {
                    Log.i("lol", "kek");
                }
            }
        });

        db.collection("characters").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                            if (!creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                String date = (String) document.get("date");
                                String fandom = (String) document.get("fandom");
                                String name = (String) document.get("name");
                                String characterName = (String) document.get("characterName");
                                String characterSurname = (String) document.get("characterSurname");
                                String content = (String) document.get("content");

                                Boolean isThereImage;
                                isThereImage = document.getBoolean("thereImage");
                                if (isThereImage == null) isThereImage = false;
                                Log.i("eee", characterName + " " + String.valueOf(isThereImage));

                                Characters newCharacter = new Characters(document.getId(), Long.parseLong(date), name, creator, fandom, characterName, characterSurname, content, isThereImage);
                                mCharactersData.add(newCharacter);
                                mCharacterAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    public class RefreshCallBack {
        public void stopRefreshing() {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}