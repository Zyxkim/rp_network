package com.adzteam.urbook.general.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

public class FeedFragment extends Fragment {

    private FeedViewModel mFeedViewModel;
    
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final PostsAdapter mAdapter = new PostsAdapter(mPostsData);

    private final ArrayList<Characters> mCharactersData = new ArrayList<>();
    private final UserCharactersAdapter mCharacterAdapter = new UserCharactersAdapter(mCharactersData);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
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
            mFeedViewModel.downloadPosts();
        }

        mSwipeRefreshLayout = view.findViewById(R.id.feed_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mFeedViewModel.refresh());

        mFeedViewModel.getRefreshState().observe(getViewLifecycleOwner(), new RefreshProgressObserver());
        mFeedViewModel.getPosts().observe(getViewLifecycleOwner(), new PostsDataObserver());
        mFeedViewModel.getCharacters().observe(getViewLifecycleOwner(), new CharactersDataObserver());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class PostsDataObserver implements Observer<ArrayList<Post>> {

        @Override
        public void onChanged(ArrayList<Post> posts) {
            mPostsData.clear();
            mPostsData.addAll(posts);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CharactersDataObserver implements Observer<ArrayList<Characters>> {

        @Override
        public void onChanged(ArrayList<Characters> characters) {
            mCharactersData.clear();
            mCharactersData.addAll(characters);
            mCharacterAdapter.notifyDataSetChanged();
        }
    }

    private class RefreshProgressObserver implements Observer<FeedViewModel.RefreshState> {

        @Override
        public void onChanged(FeedViewModel.RefreshState refreshState) {
            if (refreshState == FeedViewModel.RefreshState.DONE) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}