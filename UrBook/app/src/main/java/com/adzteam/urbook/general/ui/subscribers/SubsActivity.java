package com.adzteam.urbook.general.ui.subscribers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Friend;
import com.adzteam.urbook.adapters.SubsAdapter;
import com.adzteam.urbook.general.ui.subscribers.SubsViewModel;

import java.util.ArrayList;

public class SubsActivity extends AppCompatActivity {

    private SubsViewModel mFriendsViewModel;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Friend> mFriendsData = new ArrayList<>();
    private SubsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subs);

        mFriendsViewModel = new ViewModelProvider(this).get(SubsViewModel.class);
        mAdapter = new SubsAdapter(mFriendsData, mFriendsViewModel);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        rv.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            mFriendsViewModel.downloadFriends();
        }

        mSwipeRefreshLayout = findViewById(R.id.friends_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mFriendsViewModel.refresh());

        mFriendsViewModel.getRefreshState().observe(this, new RefreshProgressObserver());
        mFriendsViewModel.getFriends().observe(this, new FriendsDataObserver());
    }

    private class FriendsDataObserver implements Observer<ArrayList<Friend>> {

        @Override
        public void onChanged(ArrayList<Friend> friends) {
            mFriendsData.clear();
            mFriendsData.addAll(friends);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class RefreshProgressObserver implements Observer<SubsViewModel.RefreshState> {

        @Override
        public void onChanged(SubsViewModel.RefreshState refreshState) {
            if (refreshState == SubsViewModel.RefreshState.DONE) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}