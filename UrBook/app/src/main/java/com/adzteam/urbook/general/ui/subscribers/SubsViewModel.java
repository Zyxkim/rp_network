package com.adzteam.urbook.general.ui.subscribers;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.adzteam.urbook.adapters.Friend;

import java.util.ArrayList;

public class SubsViewModel extends AndroidViewModel {

    private final SubsRepo mFriendRepo = new SubsRepo();

    private final MediatorLiveData<ArrayList<Friend>> mFriendsData = mFriendRepo.getRoomsLiveData();
    private final MediatorLiveData<RefreshState> mRefreshState = new MediatorLiveData<>();

    public MediatorLiveData<RefreshState> getRefreshState() {
        return mRefreshState;
    }

    public SubsViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        MediatorLiveData<SubsRepo.RefreshProgress> refreshProgressLiveData = mFriendRepo.getRefreshProgress();
        mRefreshState.addSource(refreshProgressLiveData, (Observer<SubsRepo.RefreshProgress>) refreshProgress -> {
            Log.i("test", String.valueOf(refreshProgress));
            if (refreshProgress == SubsRepo.RefreshProgress.DONE) {
                mRefreshState.postValue(RefreshState.DONE);
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