package com.adzteam.urbook.general.ui.friends;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.adzteam.urbook.adapters.Friend;

import java.util.ArrayList;

public class FriendsViewModel extends AndroidViewModel {

    private final FriendsRepo mFriendRepo = new FriendsRepo();

    private final MediatorLiveData<ArrayList<Friend>> mFriendsData = mFriendRepo.getRoomsLiveData();
    private final MediatorLiveData<FriendsViewModel.RefreshState> mRefreshState = new MediatorLiveData<>();

    public MediatorLiveData<FriendsViewModel.RefreshState> getRefreshState() {
        return mRefreshState;
    }
    
    public FriendsViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        MediatorLiveData<FriendsRepo.RefreshProgress> refreshProgressLiveData = mFriendRepo.getRefreshProgress();
        mRefreshState.addSource(refreshProgressLiveData, (Observer<FriendsRepo.RefreshProgress>) refreshProgress -> {
            Log.i("test", String.valueOf(refreshProgress));
            if (refreshProgress == FriendsRepo.RefreshProgress.DONE) {
                mRefreshState.postValue(FriendsViewModel.RefreshState.DONE);
                mRefreshState.removeSource(refreshProgressLiveData);
            }
        });
        mFriendRepo.downloadFriends();
    }

    public MediatorLiveData<ArrayList<Friend>> getFriends() {
        return mFriendsData;
    }

    public void downloadFriends() {
        mFriendRepo.downloadFriends();
    }

    public enum RefreshState {
        DONE,
    }
}