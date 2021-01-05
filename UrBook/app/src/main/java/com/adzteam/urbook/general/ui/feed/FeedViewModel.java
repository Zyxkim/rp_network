package com.adzteam.urbook.general.ui.feed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.Post;

import java.util.ArrayList;

public class FeedViewModel extends AndroidViewModel {

    private final FeedRepo mRepo = new FeedRepo();

    private final MediatorLiveData<ArrayList<Post>> mPostsData = mRepo.getPostsLiveData();
    private final MediatorLiveData<ArrayList<Characters>> mCharactersData = mRepo.getCharactersLiveData();
    private final MediatorLiveData<RefreshState> mRefreshState = new MediatorLiveData<>();

    public MediatorLiveData<ArrayList<Post>> getPosts() {
        return mPostsData;
    }
    public MediatorLiveData<ArrayList<Characters>> getCharacters() {
        return mCharactersData;
    }
    public MediatorLiveData<RefreshState> getRefreshState() {
        return mRefreshState;
    }

    public FeedViewModel(@NonNull Application application) {
        super(application);
    }

    public void downloadPosts() {
        mRepo.downloadPosts();
    }

    public void refresh() {
        MediatorLiveData<FeedRepo.RefreshProgress> postsRefreshProgressLiveData = mRepo.getRefreshProgress();
        mRefreshState.addSource(postsRefreshProgressLiveData, (Observer<FeedRepo.RefreshProgress>) refreshProgress -> {
            if (refreshProgress == FeedRepo.RefreshProgress.DONE) {
                mRefreshState.postValue(RefreshState.DONE);
                mRefreshState.removeSource(postsRefreshProgressLiveData);
            }
        });

        mRepo.downloadPosts();
    }

    public enum RefreshState {
        DONE,
    }
}
