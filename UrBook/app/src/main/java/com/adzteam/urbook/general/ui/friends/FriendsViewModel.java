package com.adzteam.urbook.general.ui.friends;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.adzteam.urbook.adapters.Friend;

import java.util.ArrayList;

public class FriendsViewModel extends AndroidViewModel {

    private final FriendsRepo mFriendRepo = new FriendsRepo();

    private final MediatorLiveData<ArrayList<Friend>> mFriendsData = mFriendRepo.getRoomsLiveData();
    
    public FriendsViewModel(@NonNull Application application) {
        super(application);
    }

    public MediatorLiveData<ArrayList<Friend>> getFriends() {
        return mFriendsData;
    }

    public void downloadFriends() {
        mFriendRepo.downloadFriends();
    }
}