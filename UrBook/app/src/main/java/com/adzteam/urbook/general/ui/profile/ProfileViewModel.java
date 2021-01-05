package com.adzteam.urbook.general.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.authentification.AuthRepo;

import java.io.File;
import java.util.ArrayList;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepo mRepo = new ProfileRepo();

    private final MediatorLiveData<ArrayList<Post>> mPostsData = mRepo.getPostsLiveData();
    private final MediatorLiveData<ArrayList<Characters>> mCharactersData = mRepo.getCharactersLiveData();

    public MediatorLiveData<ArrayList<Post>> getPostsData() {
        return mPostsData;
    }

    public MediatorLiveData<ArrayList<Characters>> getCharactersData() {
        return mCharactersData;
    }

    private AuthRepo mAuthRepo = new AuthRepo(getApplication());

    private MediatorLiveData<String> nameLiveData = mRepo.getNameLiveData();
    private MediatorLiveData<String> statusLiveData = mRepo.getStatusLiveData();
    private MediatorLiveData<String> uriLiveData = mRepo.getUriLiveData();
    private MediatorLiveData<String> mSubsLifeData = mRepo.getSubsLiveData();

    public MediatorLiveData<String> getNameLiveData() {
        return nameLiveData;
    }

    public MediatorLiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    public MediatorLiveData<String> getUriLiveData() {
        return uriLiveData;
    }

    public MediatorLiveData<String> getSubsLiveData() {
        return mSubsLifeData;
    }

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public void signOut() {
        mAuthRepo.signOut();
    }

    public void uploadProfileData() {
        mRepo.uploadProfileData();
    }

    public void download() {
        mRepo.donwload();
    }
}
