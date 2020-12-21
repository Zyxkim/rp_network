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

import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.PostsAdapter;
import com.adzteam.urbook.general.ui.profile.ProfileViewModel;
import com.adzteam.urbook.general.ui.rooms.RoomsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FeedFragment extends Fragment {

    private ProfileViewModel mPostsViewModel;
    
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final PostsAdapter mAdapter = new PostsAdapter(mPostsData);

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
                        String date = (String) document.get("date");
                        String characterName = (String) document.get("characterName");
                        String content = (String) document.get("content");

                        Post newPost = new Post(Long.parseLong(date), creator, characterName, content);
                        mPostsData.add(newPost);
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