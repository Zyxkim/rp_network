package com.adzteam.urbook.general.ui.friends;

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

import com.adzteam.urbook.adapters.Friend;
import com.adzteam.urbook.adapters.FriendsAdapter;

public class FriendsFragment extends Fragment {

    private FriendsViewModel mFriendsViewModel;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Friend> mFriendsData = new ArrayList<>();
    private FriendsAdapter mAdapter;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFriendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        mAdapter = new FriendsAdapter(mFriendsData, mFriendsViewModel);
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        rv.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            mFriendsViewModel.downloadFriends();
        }

        mSwipeRefreshLayout = view.findViewById(R.id.friends_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mFriendsViewModel.refresh());

        mFriendsViewModel.getRefreshState().observe(getViewLifecycleOwner(), new RefreshProgressObserver());
        mFriendsViewModel.getFriends().observe(getViewLifecycleOwner(), new FriendsDataObserver());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("isThereRoomArray", "true");
        super.onSaveInstanceState(outState);
    }

    private class FriendsDataObserver implements Observer<ArrayList<Friend>> {

        @Override
        public void onChanged(ArrayList<Friend> friends) {
            mFriendsData.clear();
            mFriendsData.addAll(friends);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class RefreshProgressObserver implements Observer<FriendsViewModel.RefreshState> {

        @Override
        public void onChanged(FriendsViewModel.RefreshState refreshState) {
            if (refreshState == FriendsViewModel.RefreshState.DONE) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
