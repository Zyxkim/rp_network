package com.adzteam.urbook.general.ui.rooms;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.adzteam.urbook.adapters.Room;

import java.util.ArrayList;

public class RoomsViewModel extends AndroidViewModel {

    private final RoomsRepo mRoomRepo = new RoomsRepo();

    private final MediatorLiveData<ArrayList<Room>> mRoomsData = mRoomRepo.getRoomsLiveData();
    private final MediatorLiveData<RefreshState> mRefreshState = new MediatorLiveData<>();

    public RoomsViewModel(@NonNull Application application) {
        super(application);
    }

    public MediatorLiveData<ArrayList<Room>> getRooms() {
        return mRoomsData;
    }
    public MediatorLiveData<RefreshState> getRefreshState() {
        return mRefreshState;
    }

    public void downloadRooms() {
        mRoomRepo.downloadRooms();
    }
    public void refresh() {
        MediatorLiveData<RoomsRepo.RefreshProgress> refreshProgressLiveData = mRoomRepo.getRefreshProgress();
        mRefreshState.addSource(refreshProgressLiveData, (Observer<RoomsRepo.RefreshProgress>) refreshProgress -> {
            Log.i("test", String.valueOf(refreshProgress));
            if (refreshProgress == RoomsRepo.RefreshProgress.DONE) {
                mRefreshState.postValue(RefreshState.DONE);
                mRefreshState.removeSource(refreshProgressLiveData);
            }
        });
        mRoomRepo.downloadRooms();
    }

    public boolean isAuthorOf(Room room) {
        return mRoomRepo.isAuthor(room.getCreator());
    }

    public enum RefreshState {
        DONE,
    }
}